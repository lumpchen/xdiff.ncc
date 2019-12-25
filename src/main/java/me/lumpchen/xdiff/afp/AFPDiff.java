package me.lumpchen.xdiff.afp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import me.lumpchen.xafp.AFPException;
import me.lumpchen.xafp.AFPFileReader;
import me.lumpchen.xafp.Page;
import me.lumpchen.xafp.PrintFile;
import me.lumpchen.xafp.render.RenderParameter;
import me.lumpchen.xdiff.ConcurrentDiff;
import me.lumpchen.xdiff.DiffSetting;
import me.lumpchen.xdiff.PDocDiffResult;
import me.lumpchen.xdiff.PDocDiffResult.DocumentProperties;
import me.lumpchen.xdiff.PDocDiffResult.PageInfo;
import me.lumpchen.xdiff.document.PageContent;

public class AFPDiff extends ConcurrentDiff {
	
	private PrintFile basePrintFile;
	private PrintFile testPrintFile;
	private RenderParameter para;
	
	public AFPDiff(File base, File test, DiffSetting setting) {
		super(base, test, setting);
	}
	
	@Override
	protected String getFormat() {
		return PDocDiffResult.FORMAT_AFP;
	}

	@Override
	protected void prepareCompare() {
		this.result.getBaseDocumentInfo().setCategory("base");
		this.result.getBaseDocumentInfo().setFileName(this.base.getName());
		this.result.getTestDocumentInfo().setCategory("test");
		this.result.getTestDocumentInfo().setFileName(this.test.getName());
		
		this.para = new RenderParameter();
		this.para.usePageResolution = false;
		this.para.resolution = this.setting.resolution;
		
		this.basePrintFile = this.getPrintFile(this.base);
		this.basePageCount = this.basePrintFile.getPageCount();
		this.result.getBaseDocumentInfo().setPageCount(this.basePageCount);
		this.result.getBaseDocumentInfo().setImageSuffix(this.setting.previewImageFormat);
		this.result.getBaseDocumentInfo().setProperties(getDocumentProperties(this.base, this.basePrintFile, this.setting.hiddenFileAbsolutePath));
		
		this.testPrintFile = this.getPrintFile(this.test);
		this.testPageCount = this.testPrintFile.getPageCount();
		this.result.getTestDocumentInfo().setPageCount(testPageCount);
		this.result.getTestDocumentInfo().setImageSuffix(this.setting.previewImageFormat);
		this.result.getTestDocumentInfo().setProperties(getDocumentProperties(this.test, this.testPrintFile, this.setting.hiddenFileAbsolutePath));
	}

	@Override
	protected CompareResult compare(int startPage, int endPage) throws Exception {
		CompareResult compareResult = new CompareResult();
		
		AFPPageContentDrawer baseDrawer = new AFPPageContentDrawer(this.para, this.basePrintFile);
		AFPPageContentDrawer testDrawer = new AFPPageContentDrawer(this.para, this.testPrintFile);
		for (int i = startPage; i <= endPage; i++) {
       		logger.info("Comparing control page " + (this.baseStartPageIndex + i + 1) 
    				+ " to test page " + (this.testStartPageIndex + i + 1));
        	
			CompareResult.Result cres = new CompareResult.Result();
			
        	int controlPageNo = i + this.baseStartPageIndex;
        	int testPageNo = i + this.testStartPageIndex;
            Page page_1 = controlPageNo < basePageCount ? basePrintFile.getPage(controlPageNo) : null;
            Page page_2 = testPageNo < testPageCount ? testPrintFile.getPage(testPageNo) : null;
            
            BufferedImage baseBitmap = null;
            BufferedImage testBitmap = null;
            PageInfo basePageInfo = null;
            List<PageContent> baseContentList = null;
            List<PageContent> testContentList = null;
            
            if (page_1 != null) {
            	basePageInfo = new PageInfo(controlPageNo);

            	baseBitmap = baseDrawer.renderPage(page_1);
            	baseContentList = baseDrawer.getPageContentList();
            	
            	basePageInfo.setPreviewImage(this.writeOutImage(i, baseBitmap), baseBitmap);
            	this.setPageInfo(page_1, basePageInfo);
        		result.getBaseDocumentInfo().setPageInfo(i, basePageInfo);	
            }
            
            PageInfo testPageInfo = null;
            if (page_2 != null) {
            	testPageInfo = new PageInfo(testPageNo);

            	testBitmap = testDrawer.renderPage(page_2);
            	testContentList = testDrawer.getPageContentList();
            	
            	testPageInfo.setPreviewImage(this.writeOutImage(i, testBitmap), testBitmap);
            	this.setPageInfo(page_2, testPageInfo);
        		result.getTestDocumentInfo().setPageInfo(i, testPageInfo);	
            }
            
            String xorImage = this.compareBitmap(baseBitmap, testBitmap, i);
            if (xorImage != null) {
            	int diffCount = this.diffPage(i, baseContentList, basePageInfo, testContentList, testPageInfo, result);
            	result.addPageXORImage(i, xorImage, result.getPageDiffResult(i).isAcceptWithDiffs());
            	
            	cres.bitmapCompareResult = false;
            	cres.diffCountInPage = diffCount;
            }
            if (basePageInfo != null) {
            	basePageInfo.cleanImageData();
            }
        	if (testPageInfo != null) {
        		testPageInfo.cleanImageData();
        	}
        	
        	cres.status = CompareResult.Complete;
        	compareResult.addResult(i + 1, cres);
        	
        	this.pageCompleted++;
        	this.updateProgress();
        }
		return compareResult;
	}

	@Override
	protected void postCompare() {
    	if (setting.enableAFPTLEComparison) {
    		logger.info("Start TLE comparing...");
    		TLECompartor comp = new TLECompartor(basePrintFile, testPrintFile);
    		boolean compareResult = comp.compare();
    		result.setTLECompareResult(compareResult, comp.getCompareEntries());
    		logger.info("End TLE comparing, " + (compareResult ? "not " : "") + "found different TLE");
    	}
    	
    	if (setting.enableAFPNOPComparison) {
    		logger.info("Start NOP comparing...");
    		AFPCommentComparator comp = new AFPCommentComparator(basePrintFile, testPrintFile);
    		boolean compareResult = comp.compare();
    		result.setNopCompareResult(compareResult, comp.getCompareEntries());
    		logger.info("End NOP comparing, " + (compareResult ? "not " : "") + "found different NOP");
    	}
	}
	
	private PrintFile getPrintFile(File file) {
		AFPFileReader reader = new AFPFileReader();
		try {
			reader.read(file);
			PrintFile pf = reader.getPrintFile();
			
			return pf;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				throw new AFPException("Can't read AFP file: " + file.getAbsolutePath(), e);
			}
		}
		return null;
	}
	
	private void setPageInfo(Page page, PageInfo pageInfo) {
        float widthPt = (float) page.getPageWidth();
        float heightPt = (float) page.getPageHeight();
        int rotationAngle = 0;

        float[] size;
        if (rotationAngle == 90 || rotationAngle == 270) {
            size = new float[]{heightPt, widthPt};
        } else {
        	size = new float[]{widthPt, heightPt};
        }
        
        pageInfo.setWidth(size[0]);
		pageInfo.setHeight(size[1]);
		pageInfo.setRotation(rotationAngle);
	}
	
	private static DocumentProperties getDocumentProperties(File file, PrintFile pf, boolean hiddenFileAbsolutePath) {
		DocumentProperties props = new DocumentProperties();
		if (hiddenFileAbsolutePath) {
			props.location = "./" + file.getName();
		} else {
			props.location = file.getAbsolutePath();
		}
		props.fileSize = file.length() + " Bytes";
		return props;
	}
}
