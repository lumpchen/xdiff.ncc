package me.lumpchen.xdiff.document;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import me.lumpchen.xdiff.pdf.PDFContentRenderer;

public class TextReflow {

	public static void main(String[] args) throws InvalidPasswordException, IOException {
		PDDocument pdoc = PDDocument.load(new File("C:\\Users\\mikel\\Desktop\\diff_test\\line\\test.pdf"));
		
		int pageIndex = 0;
        PDPage page = pdoc.getPage(pageIndex);
        PDRectangle cropbBox = page.getCropBox();
        float widthPt = cropbBox.getWidth();
        float heightPt = cropbBox.getHeight();
        
		PDFContentRenderer renderer = new PDFContentRenderer(pdoc);
		BufferedImage image = renderer.renderImageWithDPI(pageIndex, 96, PDFContentRenderer.ImageType.RGB);
		TextReflow.relow(renderer.getPageContentList(), widthPt, heightPt);
	}

	public TextReflow() {
		
	}

	public static void relow(List<PageContent> contentList, float pageWidth, float pageHeight) {
		if (contentList == null || contentList.isEmpty()) {
			return;
		}
		
		List<TextContent> bodyContentList = new ArrayList<TextContent>();
		List<TextContent> headerContentList = new ArrayList<TextContent>();
		List<TextContent> footerContentList = new ArrayList<TextContent>();

		for (int i = 0; i < contentList.size(); i++) {
			PageContent content = contentList.get(i);

			if (content.getType() == PageContent.Type.Text) {
				TextContent textContent = (TextContent) content;
				
				if (pageHeight - content.getY() < 72) {
					headerContentList.add(textContent);
				} else if (content.getY() < 72) {
					footerContentList.add(textContent);
				} else {
					bodyContentList.add(textContent);
				}
			}
		}

		System.out.println("------- header --------");
		List<TextBlock> blocks = buildBlock(headerContentList);
		for (TextBlock tb : blocks) {
			System.out.println(tb.toString());
		}

		System.out.println("------- footer --------");
		blocks = buildBlock(footerContentList);
		for (TextBlock tb : blocks) {
			System.out.println(tb.toString());
		}

		System.out.println("------- body --------");
		blocks = buildBlock(bodyContentList);
		for (TextBlock tb : blocks) {
			System.out.println(tb.toString());
		}
	}
	
	static List<TextBlock> buildBlock(List<TextContent> textContentList) {
		List<TextBlock> blocks = new ArrayList<TextBlock>();
		TextBlock block = new TextBlock();
		for (TextContent content : textContentList) {
			int x = (int) Math.round(content.getX());
			int baseline = (int) Math.round(content.getBaseline());

			if (block.getContentList().isEmpty()) {
				block.getContentList().add(content);
			} else {
				TextContent last = block.getContentList().get(block.getContentList().size() - 1);
				if (content.getGraphicsStateDesc().equals(last.getGraphicsStateDesc())) {
					block.getContentList().add(content);
				} else {
					blocks.add(block);
					block = new TextBlock();
					block.getContentList().add(content);
				}
			}
		}
		if (!block.getContentList().isEmpty()) {
			blocks.add(block);
		}
		
		return blocks;
	}
	
}
