package me.lumpchen.xdiff;

import java.io.IOException;

import org.junit.Test;

public class TestCaseMisc {
	
	private static final String subFolder = "misc";
	
	@Test
	public void test_simple() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "simple");
	}
}
