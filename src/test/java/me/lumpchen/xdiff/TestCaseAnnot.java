package me.lumpchen.xdiff;

import java.io.IOException;

import org.junit.Test;

public class TestCaseAnnot {
	private static final String subFolder = "annot";
	
	@Test
	public void test_form_control() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "form_control");
	}
	
	@Test
	public void test_indd_forms() throws IOException {
		TestCaseCommon.runTestCase(subFolder, "indd_forms");
	}
}
