package me.lumpchen.xdiff;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

import me.lumpchen.xdiff.document.TextChunkBuilder;
import me.lumpchen.xdiff.pdf.PDFContentRenderer;
import me.lumpchen.xdiff.pdf.PageContentExtractorTest;

public class TextReflow {

	public static void main(String[] args) throws InvalidPasswordException, IOException {
//		testPDFTextStripper();
//		test();
		testPageContentExtractor();
	}

	public static void testPageContentExtractor() {
		PageContentExtractorTest.extract(new File("C:\\\\Users\\\\mikel\\\\Desktop\\\\diff_test\\\\t\\\\A Genetic Algorithm to Solve a Real 2-D Cutting Stock.pdf"));
	}
	
	public static void testPDFTextStripper() throws InvalidPasswordException, IOException {
		PDDocument pdoc = PDDocument.load(new File("C:\\Users\\mikel\\Desktop\\diff_test\\cn\\control.pdf"));
		PDFTextStripper stripper = new PDFTextStripper();
		stripper.setStartPage(1);
		stripper.setEndPage(1);
		stripper.setParagraphStart("<p>");
		Writer output = new StringWriter();
		stripper.writeText(pdoc, output);
		System.out.println(output.toString());
	}

	public static void test() throws InvalidPasswordException, IOException {
		PDDocument pdoc = PDDocument.load(new File("C:\\Users\\mikel\\Desktop\\diff_test\\t\\A Genetic Algorithm to Solve a Real 2-D Cutting Stock.pdf"));

		int pageIndex = 1;
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
