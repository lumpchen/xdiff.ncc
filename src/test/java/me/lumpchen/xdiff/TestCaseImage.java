package me.lumpchen.xdiff;

import java.io.IOException;

import org.junit.Test;

public class TestCaseImage {
	
	private static final String subFolder = "image";
	
	@Test
	public void test_simple() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "simple");
	}
}
