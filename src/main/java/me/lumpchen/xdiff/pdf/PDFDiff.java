package me.lumpchen.xdiff.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import me.lumpchen.xdiff.ConcurrentDiff;
import me.lumpchen.xdiff.DiffException;
import me.lumpchen.xdiff.DiffSetting;
import me.lumpchen.xdiff.PDocDiffResult;
import me.lumpchen.xdiff.PDocDiffResult.DocumentProperties;
import me.lumpchen.xdiff.PDocDiffResult.PageInfo;
import me.lumpchen.xdiff.document.PageContent;

public class PDFDiff extends ConcurrentDiff {
	
	private PDDocument baselinePDF = null;
	private PDDocument testPDF = null;

	private static Logger logger = Logger.getLogger(PDFDiff.class.getName());
	
	public PDFDiff(File base, File test, DiffSetting setting) {
		super(base, test, setting);
	}
	
	@Override
	protected String getFormat() {
		return PDocDiffResult.FORMAT_PDF;
	}

	@Override
	protected void prepareCompare() throws Exception {
		try {
			if (this.setting.ignorePageBlankArea) {
				this.base = this.cropPDF(this.base);
				this.test = this.cropPDF(this.test);
			}
			
			this.baselinePDF = PDDocument.load(this.base);
			this.baselinePDF.setResourceCache(new ConcurrentResourceCache());
			this.result.getBaseDocumentInfo().setCategory("base");
			this.result.getBaseDocumentInfo().setFileName(this.base.getName());
			this.result.getBaseDocumentInfo().setProperties(getDocumentProperties(this.base, this.baselinePDF, this.setting.hiddenFileAbsolutePath));
					
			this.testPDF = PDDocument.load(this.test);
			this.testPDF.setResourceCache(new ConcurrentResourceCache());
			this.result.getTestDocumentInfo().setCategory("test");
			this.result.getTestDocumentInfo().setFileName(this.test.getName());
			this.result.getTestDocumentInfo().setProperties(getDocumentProperties(this.test, this.testPDF, this.setting.hiddenFileAbsolutePath));
			
			this.basePageCount = this.baselinePDF.getNumberOfPages();
			this.result.getBaseDocumentInfo().setPageCount(this.basePageCount);
			this.result.getBaseDocumentInfo().setImageSuffix(setting.previewImageFormat);
			
			this.testPageCount = this.testPDF.getNumberOfPages();
			this.result.getTestDocumentInfo().setPageCount(this.testPageCount);
			this.result.getTestDocumentInfo().setImageSuffix(setting.previewImageFormat);
		} catch (Exception e) {
			throw new DiffException("Diff error: ", e);
		}
	}

	@Override
	protected CompareResult compare(int startPage, int endPage) throws Exception {
		CompareResult compareResult = new CompareResult();
        try {
        	for (int i = startPage; i <= endPage; i++) {
        		logger.info("Comparing control page " + (this.baseStartPageIndex + i + 1) 
        				+ " to test page " + (this.testStartPageIndex + i + 1));

            	CompareResult.Result cres = new CompareResult.Result();

            	int controlPageNo = i + this.baseStartPageIndex;
            	int testPageNo = i + this.testStartPageIndex;
                PDPage page_1 = controlPageNo < this.basePageCount ? this.baselinePDF.getPage(controlPageNo) : null;
                PDPage page_2 = testPageNo < this.testPageCount ? this.testPDF.getPage(testPageNo) : null;
                
                BufferedImage baseBitmap = null;
                BufferedImage testBitmap = null;
                PageInfo basePageInfo = null;
                List<PageContent> baseContentList = null;
                List<PageContent> testContentList = null;
                if (page_1 != null) {
                	basePageInfo = new PageInfo(controlPageNo);

                	baseContentList = new ArrayList<PageContent>();
                	baseBitmap = this.renderPage(controlPageNo, this.baselinePDF, baseContentList);
                	basePageInfo.setPreviewImage(this.writeOutImage(i, baseBitmap), baseBitmap);
                	this.setPageInfo(page_1, basePageInfo);
            		result.getBaseDocumentInfo().setPageInfo(i, basePageInfo);	
                }
                
                PageInfo testPageInfo = null;
                if (page_2 != null) {
                	testPageInfo = new PageInfo(testPageNo);
                	
                	testContentList = new ArrayList<PageContent>();
                	testBitmap = this.renderPage(testPageNo, this.testPDF, testContentList);
                	testPageInfo.setPreviewImage(this.writeOutImage(i, testBitmap), testBitmap);
                	this.setPageInfo(page_2, testPageInfo);
            		result.getTestDocumentInfo().setPageInfo(i, testPageInfo);
                }
                
                String xorImage = this.compareBitmap(baseBitmap, testBitmap, i);
                if (xorImage != null) {
                	int diffCount = this.diffPage(i, baseContentList, basePageInfo, testContentList, testPageInfo, result);
                	this.result.addPageXORImage(i, xorImage, result.getPageDiffResult(i).isAcceptWithDiffs());
                	
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
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Can't render page: ", e);
        	throw new DiffException("Can't render page: " + e);
        }
        
		return compareResult;
	}

	@Override
	protected void postCompare() throws Exception {
		if (this.baselinePDF != null) {
			this.baselinePDF.close();
		}
		
		if (this.testPDF != null) {
			this.testPDF.close();
		}
	}
	
	private synchronized BufferedImage renderPage(int pageNo, PDDocument pdoc, List<PageContent> contentList) throws Exception {
//		PDFRenderer baseRenderer = new PDFRenderer(pdoc);
//        BufferedImage image = baseRenderer.renderImageWithDPI(pageNo, this.setting.resolution, ImageType.RGB);
//        return image;
        
        PDFContentRenderer renderer = new PDFContentRenderer(pdoc);
        BufferedImage image = renderer.renderImageWithDPI(pageNo, this.setting.resolution, PDFContentRenderer.ImageType.RGB);
        if (contentList != null) {
        	contentList.addAll(renderer.getPageContentList());
        }
        return image;
	}
	
	private void setPageInfo(PDPage page, PageInfo pageInfo) {
        PDRectangle cropbBox = page.getCropBox();
        float widthPt = cropbBox.getWidth();
        float heightPt = cropbBox.getHeight();
        int rotationAngle = page.getRotation();

        float[] size;
        if (rotationAngle == 90 || rotationAngle == 270) {
            size = new float[]{heightPt, widthPt};
        } else {
        	size = new float[]{widthPt, heightPt};
        }
        
        pageInfo.setWidth(size[0]);
		pageInfo.setHeight(size[1]);
		pageInfo.setRotation(rotationAngle);
		pageInfo.setCropBox(new float[] {
				cropbBox.getLowerLeftX(),
				cropbBox.getLowerLeftY(),
				cropbBox.getUpperRightX(),
				cropbBox.getUpperRightY()
		});
	}
	
	private File cropPDF(File src) throws IOException {
		PageCrop cropper = new PageCrop(src);
		String dst = src.getParentFile().getAbsolutePath() + File.separator + "cropped_" + src.getName();
		File dstFile = new File(dst);
		cropper.crop(dstFile);
		
		return dstFile;
	}
	
	private static DocumentProperties getDocumentProperties(File file, PDDocument pdf, boolean hiddenFileAbsolutePath) {
		DocumentProperties props = new DocumentProperties();
		props.title = pdf.getDocumentInformation().getTitle();
		props.author = pdf.getDocumentInformation().getAuthor();
		props.subject = pdf.getDocumentInformation().getSubject();
		props.keywords = pdf.getDocumentInformation().getKeywords();
		if (pdf.getDocumentInformation().getCreationDate() != null) {
			props.created = pdf.getDocumentInformation().getCreationDate().getTime().toString();
		}
		
		if (pdf.getDocumentInformation().getModificationDate() != null) {
			props.modified = pdf.getDocumentInformation().getModificationDate().getTime().toString();
		}
		
		props.producer = pdf.getDocumentInformation().getProducer();
		props.version = pdf.getVersion() + "";
		if (hiddenFileAbsolutePath) {
			props.location = "./" + file.getName();
		} else {
			props.location = file.getAbsolutePath();			
		}
		props.fileSize = file.length() + " Bytes";
		return props;
	}
}
