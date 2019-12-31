package me.lumpchen.xdiff;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import me.lumpchen.xdiff.document.TextChunkBuilder;
import me.lumpchen.xdiff.pdf.PDFContentRenderer;

public class TextReflow {

	public static void main(String[] args) throws InvalidPasswordException, IOException {
		PDDocument pdoc = PDDocument.load(new File("C:\\Users\\mikel\\Desktop\\diff_test\\pdf17\\control.pdf"));
		
		int pageIndex = 110;
        PDPage page = pdoc.getPage(pageIndex);
        PDRectangle cropbBox = page.getCropBox();
        float widthPt = cropbBox.getWidth();
        float heightPt = cropbBox.getHeight();
        
		PDFContentRenderer renderer = new PDFContentRenderer(pdoc);
		BufferedImage image = renderer.renderImageWithDPI(pageIndex, 96, PDFContentRenderer.ImageType.RGB);
		TextChunkBuilder builder = new TextChunkBuilder(renderer.getPageContentList(), widthPt, heightPt);
		builder.build();
	}
	
}
