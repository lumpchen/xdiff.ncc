package me.lumpchen.xdiff;

import java.io.IOException;

import org.junit.Test;

public class TestCaseGraphics {

	private static final String subFolder = "graphics";
	
	@Test
	public void test_simple() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "border");
	}
	
	@Test
	public void test_pixel_compare() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "pixel_compare");
	}
	
	@Test
	public void test_stroke() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "stroke");
	}
	
	@Test
	public void test_table() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "table");
	}
}
