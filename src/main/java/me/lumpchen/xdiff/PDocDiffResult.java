package me.lumpchen.xdiff;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.lumpchen.xdiff.afp.AFPCommentComparator.NopCompareEntry;
import me.lumpchen.xdiff.afp.TLECompartor.TLECompareEntry;

public class PDocDiffResult {

	public static final String FORMAT_PDF = "pdf";
	public static final String FORMAT_AFP = "afp";
	public static final String FORMAT_PS = "ps";
	
	private String format;
	
	private Map<Integer, PageDiffResult> entryMap;
	private DocumentInfo baseInfo;
	private DocumentInfo testInfo;
	private int diffPageCount;
	private List<Integer> diffPageNumbs;
	private DiffSetting setting;

	private Map<Integer, String> xorImageMap;
	private List<Integer> diffAcceptedPages;

	private List<Integer> acceptDiffPageNumbs;
	
	private List<TLECompareEntry> tleCompareEntries;
	private boolean tleCompareResult = true;
	private List<NopCompareEntry> nopCompareEntries;
	private boolean nopCompareResult = true;
	
	public PDocDiffResult(DiffSetting setting, String format) {
		this.setting = setting;
		this.format = format;
		
		this.entryMap = new ConcurrentHashMap<Integer, PageDiffResult>();
		this.diffPageNumbs = Collections.synchronizedList(new ArrayList<Integer>());
		this.xorImageMap = new ConcurrentHashMap<Integer, String>();
		
		this.diffAcceptedPages = Collections.synchronizedList(new ArrayList<Integer>());
		this.acceptDiffPageNumbs = Collections.synchronizedList(new ArrayList<Integer>());
		
		if (this.format.equals(FORMAT_AFP)) {
			this.tleCompareEntries = Collections.synchronizedList(new ArrayList<TLECompareEntry>());
			this.nopCompareEntries = Collections.synchronizedList(new ArrayList<NopCompareEntry>());
		}
	}
	
	public String getFormat() {
		return this.format;
	}

	public float getResolution() {
		return this.setting.resolution;
	}

	public void setDocumentInfo(DocumentInfo baseInfo, DocumentInfo testInfo) {
		this.baseInfo = baseInfo;
		this.testInfo = testInfo;
	}

	public DocumentInfo getBaseDocumentInfo() {
		if (this.baseInfo == null) {
			this.baseInfo = new DocumentInfo();
		}
		return this.baseInfo;
	}

	public DocumentInfo getTestDocumentInfo() {
		if (this.testInfo == null) {
			this.testInfo = new DocumentInfo();
		}
		return this.testInfo;
	}

	public void add(int pageNo, PageDiffResult pageResult) {
		int before = pageResult.countDiffs();
		
		pageResult.acceptDifferenceArea(this.setting.compSetting.getAcceptanceDifferenceArea(pageNo, true),
				this.setting.compSetting.getAcceptanceDifferenceArea(pageNo, false));
		
		int after = pageResult.countDiffs();
		this.entryMap.put(pageNo, pageResult);
		
		if (pageResult.countDiffs() > 0) {
			this.diffPageCount++;
			this.diffPageNumbs.add(pageNo);
		} else if (before != after) {
			this.acceptDiffPageNumbs.add(pageNo);
		}
	}

	public PageDiffResult getPageDiffResult(int pageNo) {
		return this.entryMap.get(pageNo);
	}

	public int countOfDiffPages() {
		return this.diffPageCount;
	}

	public void setNopCompareResult(boolean compareResult, List<NopCompareEntry> entries) {
		this.nopCompareEntries = entries;
		this.nopCompareResult = compareResult;
	}
	
	public List<NopCompareEntry> getNopCompareData() {
		return this.nopCompareEntries;
	}
	
	public boolean getNopCompareResult() {
		return this.nopCompareResult;
	}
	
	public void setTLECompareResult(boolean compareResult, List<TLECompareEntry> entries) {
		this.tleCompareResult = compareResult;
		this.tleCompareEntries = entries;
	}
	
	public List<TLECompareEntry> getTLECompareData() {
		return this.tleCompareEntries;
	}
	
	public boolean getTLECompareResult() {
		return this.tleCompareResult;
	}

	public Integer[] getDiffPageNums() {
		Integer[] ret = new Integer[this.diffPageNumbs.size()];
		return this.diffPageNumbs.toArray(ret);
	}
	
	public Integer[] getAcceptDiffPageNums() {
		if (this.acceptDiffPageNumbs == null) {
			return new Integer[0];
		}
		Integer[] ret = new Integer[this.acceptDiffPageNumbs.size()];
		return this.acceptDiffPageNumbs.toArray(ret);
	}

	public void addPageXORImage(int pageNo, String image, boolean isAccepted) {
		this.xorImageMap.put(pageNo, image);
		
		if (isAccepted) {
			this.diffAcceptedPages.add(pageNo);
		}
	}
	
	public Integer[] getDiffAcceptedPages() {
		if (this.diffAcceptedPages == null || this.diffAcceptedPages.isEmpty()) {
			return new Integer[0];
		}
		Integer[] ret = new Integer[this.diffAcceptedPages.size()];
		return this.diffAcceptedPages.toArray(ret);
	}

	public Map<Integer, String> getPageXORImageMap() {
		return this.xorImageMap;
	}

	public static class DocumentInfo {
		private int pageCount;
		private String category;
		private String fileName;
		private String imageSuffix;
		private DocumentProperties properties;

		private Map<Integer, PageInfo> pageInfoMap;

		public DocumentInfo() {
			this.pageInfoMap = new HashMap<Integer, PageInfo>();
		}

		public void setPageInfo(int page, PageInfo pageInfo) {
			this.pageInfoMap.put(page, pageInfo);
		}

		public PageInfo getPageInfo(int page) {
			if (page < 0 || page >= this.pageCount) {
				return null;
			}
			return this.pageInfoMap.get(page);
		}

		public int getPageCount() {
			return pageCount;
		}

		public void setPageCount(int pageCount) {
			this.pageCount = pageCount;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getImageSuffix() {
			return imageSuffix;
		}

		public void setImageSuffix(String imageSuffix) {
			this.imageSuffix = imageSuffix;
		}
		
		public void setProperties(DocumentProperties properties) {
			this.properties = properties;
		}
		
		public DocumentProperties getProperties() {
			return this.properties;
		}
	}
	
	public static class DocumentProperties {
		public String title;
		public String author;
		public String subject;
		public String keywords;
		public String created;
		public String modified;
		public String producer;
		public String version;
		public String location;
		public String fileSize;
	}

	public static class PageInfo {
		private float width;
		private float height;
		private String previewImage;
		private BufferedImage imageData;
		private int pageNo;
		private int rotation;
		private float[] cropBox;

		public PageInfo(int pageNo) {
			this.pageNo = pageNo;
		}

		public int getPageNo() {
			return this.pageNo;
		}

		public float getWidth() {
			return width;
		}

		public void setWidth(float width) {
			this.width = width;
		}

		public float getHeight() {
			return height;
		}

		public void setHeight(float height) {
			this.height = height;
		}

		public String getPreviewImage() {
			return previewImage;
		}

		public void setPreviewImage(String previewImage, BufferedImage imageData) {
			this.previewImage = previewImage;
			this.imageData = imageData;
		}

		public int getRotation() {
			return rotation;
		}

		public void setRotation(int rotation) {
			this.rotation = rotation;
		}

		public float[] getCropBox() {
			return cropBox;
		}

		public void setCropBox(float[] cropBox) {
			this.cropBox = cropBox;
		}

		public BufferedImage getImageData() {
			return this.imageData;
		}
		
		public void cleanImageData() {
			this.imageData = null;
		}

	}

}
