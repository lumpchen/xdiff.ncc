package me.lumpchen.xdiff;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import me.lumpchen.xdiff.PDocDiffResult.PageInfo;
import me.lumpchen.xdiff.document.PageContent;
import me.lumpchen.xdiff.document.compare.PageContentComparator;

public abstract class ConcurrentDiff {
	
	public static Logger logger = Logger.getLogger(ConcurrentDiff.class.getName());
	
	protected File base;
	protected File test;
	protected DiffSetting setting;
	
	protected int basePageCount;
	protected int testPageCount;
	protected PDocDiffResult result;
	
	protected volatile int pageCompleted;
	private int maxPageNum;
	
	private static final int TIMEOUT = 10000;
	private static final int CPU_CORE_NUM = Runtime.getRuntime().availableProcessors();
	
	public ConcurrentDiff(File base, File test, DiffSetting setting) {
		this.base = base;
		this.test = test;
		this.setting = setting;
	}
	
	public PDocDiffResult diff() throws DiffException {

        try {
    		this.result = new PDocDiffResult(this.setting, this.getFormat());
    		this.prepareCompare();
    		
    		this.maxPageNum = basePageCount > testPageCount ? basePageCount : testPageCount;

    		final List<Callable<CompareResult>> partitions = new ArrayList<Callable<CompareResult>>();
        	
        	if (this.setting.fromPage > -1 && this.setting.toPage > -1) {
        		final int startPage = this.setting.fromPage < maxPageNum ? this.setting.fromPage : maxPageNum;
        		final int endPage = this.setting.toPage < maxPageNum ? this.setting.toPage : maxPageNum;
        		
        		partitions.add(new Callable<CompareResult>() {
					@Override
					public CompareResult call() throws Exception {
						CompareResult ret = compare(startPage, endPage);
						return ret;
					}
        		});
        	} else {
            	int partition = CPU_CORE_NUM * 2;
            	int pagesPerPartition = maxPageNum / partition;
            	if (maxPageNum <= partition) {
            		pagesPerPartition = 1;
            		partition = maxPageNum;
            	} else {
            		pagesPerPartition = maxPageNum / partition + 1;
            	}
            	for (int i = 0; i < partition; i++) {
            		final int begin = i * pagesPerPartition;
            		if (begin >= this.maxPageNum) {
            			break;
            		}
            		int nend = begin + pagesPerPartition - 1;
            		final int end = nend > maxPageNum ? maxPageNum : nend;
            		
            		partitions.add(new Callable<CompareResult>() {
    					@Override
    					public CompareResult call() throws Exception {
    						CompareResult ret = compare(begin, end);
    						return ret;
    					}
            		});
            	}
        	}
        	
        	final ExecutorService executorPool = Executors.newFixedThreadPool(CPU_CORE_NUM);
			final List<Future<CompareResult>> resultFromParts = executorPool.invokeAll(partitions, TIMEOUT, TimeUnit.SECONDS);
			executorPool.shutdown();
//			for (final Future<CompareResult> ret : resultFromParts) {
//				System.out.println(ret.get());
//			}

			this.postCompare();
			
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Can't compare " + this.getFormat() + " files: ", e);
        	throw new DiffException("Can't compare " + this.getFormat() + " files: " + e);
        }
		return result;
	}
	
	protected String writeOutImage(int pageNo, BufferedImage image) throws Exception {
        File temp = File.createTempFile("xdiff", "." + setting.previewImageFormat);
        temp.deleteOnExit();
        if (ImageIOUtil.writeImage(image, temp.getAbsolutePath(), (int) this.setting.resolution)) {
        	return temp.getAbsolutePath();
        }
        throw new DiffException("Can't render page: " + pageNo);
	}
	
	protected String compareBitmap(BufferedImage baseBitmap, BufferedImage testBitmap, int pageNo) throws Exception {
		try {
			BufferedImage xorBitmap = BitmapComparor.diffImages(baseBitmap, testBitmap, setting.diffBitmapBackground);
			if (xorBitmap != null) {
				return this.writeOutImage(pageNo, xorBitmap);
			}
			return null;
		} catch (IOException e) {
			throw new DiffException("Fail to bitmap comparison of page: " + pageNo, e);
		}
	}
	
	protected int diffPage(int pageNo, List<PageContent> basePageContents, PageInfo basePageInfo, 
			List<PageContent> testPageContents, PageInfo testPageInfo, PDocDiffResult result) throws DiffException {
		try {
			PageContentComparator pageComparator = new PageContentComparator(this.setting.compSetting);
			PageDiffResult pageDiffResult = pageComparator.compare(basePageContents, basePageInfo, testPageContents, testPageInfo);
			
			result.add(pageNo, pageDiffResult);
			
			return pageDiffResult.countDiffs();
		} catch (Exception e) {
			throw new DiffException("Page content extract failure: " + pageNo, e);
		}
	}
	
	protected synchronized void updateProgress() {
		float progress = (float) this.pageCompleted / this.maxPageNum;
		this.setting.progressListener.progress(progress);
	}
	
	protected abstract String getFormat();
	
	protected abstract void prepareCompare() throws Exception;
	
	protected abstract CompareResult compare(int begin, int end) throws Exception;
	
	protected abstract void postCompare() throws Exception;
	
	public static class CompareResult {
		
		public static final String Complete = "Complete";
		public static final String Failed = "Failed";
		
		private Map<Integer, Result> resultMap; 
		
		public static class Result {
			public String status;
			public boolean bitmapCompareResult = true;
			public int diffCountInPage;
		}
		
		public CompareResult() {
			this.resultMap = new HashMap<Integer, Result>();
		}
		
		public String toString() {
			StringBuilder buf = new StringBuilder();
			
			Iterator<Entry<Integer, Result>> it = this.resultMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, Result> next = it.next();
				buf.append("Page " + next.getKey());
				buf.append(": ");
				buf.append(next.getValue().status);
				buf.append("; ");
				
				buf.append("\"Bitmap Compare Result\": " + (next.getValue().bitmapCompareResult ? "Identity" : "Different"));
				buf.append("; ");
				buf.append("\"Content Compare Result\": " + (next.getValue().diffCountInPage == 0 ? "Identity" : "Different"));
				
				buf.append("; \n");
			}
			
			return buf.toString();
		}
		
		public void addResult(int pageNo, Result result) {
			this.resultMap.put(pageNo, result);
		}
		
		public Result getResult(int pageNo) {
			return this.resultMap.get(pageNo);
		}
	}
	
}
