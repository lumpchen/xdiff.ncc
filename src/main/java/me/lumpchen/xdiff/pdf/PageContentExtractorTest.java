package me.lumpchen.xdiff.pdf;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import me.lumpchen.xdiff.document.AnnotContent;
import me.lumpchen.xdiff.document.GraphicsContent;
import me.lumpchen.xdiff.document.ImageContent;
import me.lumpchen.xdiff.document.PageContent;
import me.lumpchen.xdiff.document.TextContent;

public class PageContentExtractorTest {

	public static void main(String[] args) {
		extract(new File(args[0]));
	}

	public static void extract(File pdf) {
		PDDocument pdfDoc = null;
		try {
			pdfDoc = PDDocument.load(pdf);
			
			PDFContentRenderer render = new PDFContentRenderer(pdfDoc);
			int n = pdfDoc.getNumberOfPages();
			for (int i = 0; i < n; i++) {
				render.renderImage(i);
				List<PageContent> pageContents = render.getPageContentList();
//				showPageContents(pageContents);
				reflow(pageContents);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pdfDoc != null) {
				try {
					pdfDoc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static List<PageContent> extractPage(PDPage page) throws IOException {
		PDFPageContentDrawer extractor = new PDFPageContentDrawer(page);
		return extractor.getPageContentList();
	}
	
	static void showPageContents(List<PageContent> pageContents) {
		if (pageContents == null || pageContents.isEmpty()) {
			return;
		}
		
		System.out.println(pageContents.size());
		for (PageContent content : pageContents) {
			showContent(content);
		}
	}
	
	static void showContent(PageContent content) {
		if (content.getType() == PageContent.Type.Text) {
			TextContent text = (TextContent) content;
			System.out.println(text.getTypeString() + ": " + text.showString());
			
			System.out.println("    FontName: " + text.getGraphicsStateDesc().textState.getFontName()
					+ " " + text.getGraphicsStateDesc().textState.fontSize);
			System.out.println("    Outline: " + text.getOutlineRect());
		} else if (content.getType() == PageContent.Type.Path) {
			GraphicsContent text = (GraphicsContent) content;
			System.out.println(text.getTypeString() + ": " + text.showString());
			
			System.out.println("    ColorSpace: " + text.getGraphicsStateDesc().strokingColor.colorSpace);
			System.out.println("    Outline: " + text.getOutlineRect());
		} else if (content.getType() == PageContent.Type.Image) {
			ImageContent image = (ImageContent) content;
			System.out.println(image.getTypeString() + ": " + image.showString());
			
			System.out.println("    ColorSpace: " + image.colorSpace);
			System.out.println("    Decode: " + image.decode);
			System.out.println("    Height: " + image.height);
			System.out.println("    Width: " + image.width);
			System.out.println("    RasterSize: " + image.byteCount);
			System.out.println("    Suffix: " + image.suffix);
		} else if (content.getType() == PageContent.Type.Annot) {
			AnnotContent annot = (AnnotContent) content;
			System.out.println(annot.getTypeString() + ": " + annot.showString());
			
//			System.out.println("    SubType: " + annot.getSubType());
//			System.out.println("    FieldType: " + annot.getFieldType());
//			System.out.println("    Name: " + annot.getFieldName());
//			System.out.println("    Contents: " + annot.getAlternateFieldName());
		} 
	}
	
	public static void reflow(List<PageContent> pageContents) {
		for (PageContent content : pageContents) {
			if (content.getType() == PageContent.Type.Text) {
				TextContent text = (TextContent) content;
				int baseline = (int) Math.round(text.getOrigin().getY());
				int lineHeight = (int) Math.round(text.getHeight());
				System.out.println(baseline + "  " + text.getText());
			}
			
		}
	}
}

