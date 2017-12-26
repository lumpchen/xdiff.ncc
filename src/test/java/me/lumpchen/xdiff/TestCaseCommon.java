package me.lumpchen.xdiff;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

import junit.framework.Assert;

public class TestCaseCommon {

	private static final File root = new File("src/test/resources/testcases/xdiff/");
	
	private static Logger logger = Logger.getLogger(TestCaseCommon.class.getName());

	public static void runTestCase(String subFolder, String name) throws IOException {
		File f = new File(root, subFolder);
		if (!f.exists()) {
			return;
		}

		runTestCase(new File(f, name));
	}

	static void runTestCase(File folder) throws IOException {
		if (!folder.exists() || !folder.isDirectory()) {
			Assert.fail("Not found testcase: " + folder.getName());
		}
		logger.info("Run testcase: " + folder.getName());

		File control = getFile(folder, "control.pdf");
		File test = getFile(folder, "test.pdf");
		File config = getFile(folder, "config.properties");
		File report = getFile(folder, "report");

		File newReport = getFile(folder, "report_test");
		if (newReport == null) {
			newReport = new File(folder, "report_test");
		} else {
			if (newReport.exists()) {
				deleteFilesInFolder(newReport);
			}
		}

		boolean res = runTestCase(control, test, config, report, newReport);
		Assert.assertTrue(folder.getAbsolutePath(), res);
	}

	static void deleteFilesInFolder(File folder) {
		Path dir = folder.toPath();
		try {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					if (exc == null) {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					} else {
						throw exc;
					}
				}

			});
			
		} catch (IOException e) {
		}

	}

	static boolean runTestCase(File control, File test, File config, File report, File newReport) {
		int res = XDiff.diff(control, test, newReport, config.getAbsolutePath(), -1, -1);

		File baselineReprotFile = getReportData(report);
		File newReprotFile = getReportData(newReport);

		try {
			return Util.compareReportData(baselineReprotFile, newReprotFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	static File getReportData(File reportDir) {
		File data = new File(reportDir, "js/diff_report_data.js");
		return data;
	}

	static File getFile(File parent, String fileName) {
		File child = new File(parent, fileName);
		if (child.exists() && child.canRead()) {
			return child;
		}
		return null;
	}
}
