package me.lumpchen.xdiff;

import java.io.IOException;

import org.junit.Test;

public class TestCaseGraphics {

	private static final String subFolder = "graphics";
	
	@Test
	public void test_simple() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "simple");
	}
	
	@Test
	public void test_complex() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "complex");
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
