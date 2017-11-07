package me.lumpchen.xdiff;

import java.io.IOException;

import org.junit.Test;

public class TestCaseText {

	private static final String subFolder = "text";
	
	@Test
	public void test_enableTextPositionCompare() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "enableTextPositionCompare");
	}
	
	@Test
	public void test_ignorePageBlankArea() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "ignorePageBlankArea");
	}
	
	@Test
	public void test_text_insert() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "text_insert");
	}

}
