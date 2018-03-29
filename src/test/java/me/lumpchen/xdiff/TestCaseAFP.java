package me.lumpchen.xdiff;

import java.io.IOException;

import org.junit.Test;

public class TestCaseAFP {
	private static final String subFolder = "afp";
	
	@Test
	public void test_original() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "original", "afp");
	}
}
