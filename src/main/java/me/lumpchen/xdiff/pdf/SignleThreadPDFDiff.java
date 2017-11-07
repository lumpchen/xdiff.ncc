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
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import me.lumpchen.xdiff.BitmapComparor;
import me.lumpchen.xdiff.DiffException;
import me.lumpchen.xdiff.DiffSetting;
import me.lumpchen.xdiff.PDocDiffResult;
import me.lumpchen.xdiff.PageDiffResult;
import me.lumpchen.xdiff.PDocDiffResult.DocumentProperties;
import me.lumpchen.xdiff.PDocDiffResult.PageInfo;
import me.lumpchen.xdiff.document.PageContent;
import me.lumpchen.xdiff.document.compare.PageContentComparator;

public class SignleThreadPDFDiff {

	private File base;
	private File test;
	private DiffSetting setting;
	private static Logger logger = Logger.getLogger(SignleThreadPDFDiff.class.getName());
	
	public SignleThreadPDFDiff(File base, File test, DiffSetting setting) {
		this.base = base;
		this.test = test;
		this.setting = setting;
	}
	
	private File cropPDF(File src) throws IOException {
		PageCrop cropper = new PageCrop(src);
		String dst = src.getParentFile().getAbsolutePath() + File.separator + "cropped_" + src.getName();
		File dstFile = new File(dst);
		cropper.crop(dstFile);
		
		return dstFile;
	}
	
	public PDocDiffResult diff() throws DiffException {
		PDDocument baselinePDF = null;
		PDDocument testPDF = null;
		
		PDocDiffResult result = new PDocDiffResult(this.setting, PDocDiffResult.FORMAT_PDF);
		try {
			if (setting.ignorePageBlankArea) {
				this.base = this.cropPDF(this.base);
				this.test = this.cropPDF(this.test);
			}
			
			baselinePDF = PDDocument.load(this.base);
			result.getBaseDocumentInfo().setCategory("base");
			result.getBaseDocumentInfo().setFileName(this.base.getName());
			result.getBaseDocumentInfo().setProperties(getDocumentProperties(this.base, baselinePDF));
					
			testPDF = PDDocument.load(this.test);
			result.getTestDocumentInfo().setCategory("test");
			result.getTestDocumentInfo().setFileName(this.test.getName());
			result.getTestDocumentInfo().setProperties(getDocumentProperties(this.test, testPDF));
			
			this.diffPDoc(baselinePDF, testPDF, result);
		} catch (Exception e) {
			throw new DiffException("Diff error: ", e);
		} finally {
			if (baselinePDF != null) {
				try {
					baselinePDF.close();
				} catch (IOException e) {
					throw new DiffException("Diff error: ", e);
				}
			}
			if (testPDF != null) {
				try {
					testPDF.close();
				} catch (IOException e) {
					throw new DiffException("Diff error: ", e);
				}
			}
		}
		return result;
	}
	
	private void diffPDoc(PDDocument base, PDDocument test, PDocDiffResult result) throws DiffException {
		int pageNum_1 = base.getNumberOfPages();
		int pageNum_2 = test.getNumberOfPages();
//		if (pageNum_1 != pageNum_2) {
//			throw new PDFDiffException("Page count is different: base=" + pageNum_1 + ", test=" + pageNum_2);
//		}
		
		result.getBaseDocumentInfo().setPageCount(pageNum_1);
		result.getBaseDocumentInfo().setImageSuffix(setting.previewImageFormat);
		
		result.getTestDocumentInfo().setPageCount(pageNum_2);
		result.getTestDocumentInfo().setImageSuffix(setting.previewImageFormat);
		
		int maxPageNum = pageNum_1 > pageNum_2 ? pageNum_1 : pageNum_2; 
        try {
        	int startPage = 0;
        	int endPage = maxPageNum;
        	if (this.setting.pageNo != -1) {
        		startPage = this.setting.pageNo;
        		endPage = startPage + 1;
        	}
        	
            for (int i = startPage; i < endPage; i++) {
            	logger.info("Comparing page " + (i + 1) + " in " + (endPage - startPage));
            	
                PDPage page_1 = i < pageNum_1 ? base.getPage(i) : null;
                PDPage page_2 = i < pageNum_2 ? test.getPage(i) : null;
                
                BufferedImage baseBitmap = null;
                BufferedImage testBitmap = null;
                PageInfo basePageInfo = null;
                List<PageContent> baseContentList = null;
                List<PageContent> testContentList = null;
                if (page_1 != null) {
                	basePageInfo = new PageInfo(i);

                	baseContentList = new ArrayList<PageContent>();
                	baseBitmap = this.renderPage(i, base, baseContentList);
                	basePageInfo.setPreviewImage(this.writeOutImage(i, baseBitmap), baseBitmap);
                	this.setPageInfo(page_1, basePageInfo);
            		result.getBaseDocumentInfo().setPageInfo(i, basePageInfo);	
                }
                
                PageInfo testPageInfo = null;
                if (page_2 != null) {
                	testPageInfo = new PageInfo(i);
                	
                	testContentList = new ArrayList<PageContent>();
                	testBitmap = this.renderPage(i, test, testContentList);
                	testPageInfo.setPreviewImage(this.writeOutImage(i, testBitmap), testBitmap);
                	this.setPageInfo(page_2, testPageInfo);
            		result.getTestDocumentInfo().setPageInfo(i, testPageInfo);
                }
                
                String xorImage = this.compareBitmap(baseBitmap, testBitmap, i);
                if (xorImage != null) {
//                	this.diffPage(i, page_1, page_2, result);
                	
                	this.diffPage(i, baseContentList, basePageInfo, testContentList, testPageInfo, result);
                	result.addPageXORImage(i, xorImage, result.getPageDiffResult(i).isAcceptWithDiffs());
                }
                if (basePageInfo != null) {
                	basePageInfo.cleanImageData();	
                }
            	if (testPageInfo != null) {
            		testPageInfo.cleanImageData();            		
            	}
            }
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Can't render page: ", e);
        	throw new DiffException("Can't render page: " + e);
        }
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
	
	private BufferedImage renderPage(int pageNo, PDDocument pdoc, List<PageContent> contentList) throws Exception {
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
	
	private String writeOutImage(int pageNo, BufferedImage image) throws Exception {
        File temp = File.createTempFile("pdf_diff", "." + setting.previewImageFormat);
        if (ImageIOUtil.writeImage(image, temp.getAbsolutePath(), (int) this.setting.resolution)) {
        	return temp.getAbsolutePath();
        }
        throw new DiffException("Can't render page: " + pageNo);
	}
	
	private String compareBitmap(BufferedImage baseBitmap, BufferedImage testBitmap, int pageNo) throws Exception {
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
	
	/*private void diffPage(int pageNo, PDPage base, PDPage test, PDocDiffResult result) throws PDFDiffException {
		try {
			List<PageContent> basePageContents = null;
			if (base != null) {
				PageContentExtractor extractor_1 = new PageContentExtractor(base);
				extractor_1.extract();
				basePageContents = extractor_1.getPageContentList();	
			}

			List<PageContent> testPageContents = null;
			if (test != null) {
				PageContentExtractor extractor_2 = new PageContentExtractor(test);
				extractor_2.extract();
				testPageContents = extractor_2.getPageContentList();	
			}
			
			PageContentComparator pageComparator = new PageContentComparator(this.setting.compSetting);
			PageDiffResult pageDiffResult = pageComparator.compare(basePageContents, testPageContents);
			
			result.add(pageNo, pageDiffResult);
		} catch (Exception e) {
			throw new PDFDiffException("Page content extract failure: " + pageNo, e);
		}
	}*/
	
	private void diffPage(int pageNo, List<PageContent> basePageContents, PageInfo basePageInfo, 
			List<PageContent> testPageContents, PageInfo testPageInfo, PDocDiffResult result) throws DiffException {
		try {
			PageContentComparator pageComparator = new PageContentComparator(this.setting.compSetting);
			PageDiffResult pageDiffResult = pageComparator.compare(basePageContents, basePageInfo, testPageContents, testPageInfo);
			
			result.add(pageNo, pageDiffResult);
		} catch (Exception e) {
			throw new DiffException("Page content extract failure: " + pageNo, e);
		}
	}
	
	private static DocumentProperties getDocumentProperties(File file, PDDocument pdf) {
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
		props.location = file.getAbsolutePath();
		props.fileSize = file.length() + " Bytes";
		return props;
	}
}
