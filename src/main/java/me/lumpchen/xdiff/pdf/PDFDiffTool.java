package me.lumpchen.xdiff.pdf;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import me.lumpchen.xdiff.DiffSetting;
import me.lumpchen.xdiff.DiffSettingLoader;
import me.lumpchen.xdiff.PDocDiffResult;
import me.lumpchen.xdiff.PageDiffResult;
import me.lumpchen.xdiff.PageDiffResult.DiffContent;
import me.lumpchen.xdiff.report.HtmlDiffReport;

public class PDFDiffTool {
/*
	public static void main(String[] args) {
		prepare(args);
	}
	
	private static void showUsage() {
        String usage = "Usage: java -jar xdiff.jar [options] <baseline-pdf> <test-pdf> <result-folder>\n"
                + "\nOptions:\n"
                + "  -config                            : Comparison configuration file path.\n";
        System.err.println(usage);
        System.exit(1);
	}
	
	private static void prepare(String args[]) {

		if (args == null || args.length < 3) {
			showUsage();
		}
		
		boolean folderCompare = false;
		String base = null;
		String test = null;
		String result = null;
		String config = null;
		
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			
			if (arg.equals("-folder")) {
				folderCompare = true;
			} else if (arg.equals("-config")) {
				config = args[++i];
			} else if (base == null) {
				base = args[i];
			} else if (test == null) {
				test = args[i];
			} else if (result == null) {
				result = args[i];
			}
		}
		
		if (base == null || test == null || result == null) {
			System.err.println("Invalid parameters! \n");
			showUsage();
		}

		if (folderCompare) {
			diff_folder(base, test, result, config);
		} else {
			diff(base, test, result, config);
		}
	}
	*/
	
	private static Logger logger = Logger.getLogger(PDFDiffTool.class.getName());
	/*
	public static int diff(String base, String test, String reportDir, String config) {
		File baseFile = new File(base);
		if (baseFile.exists() && baseFile.isFile()) {
			return diff(new File(base), new File(test), new File(reportDir), config);
		} else {
			return diff_folder(base, test, reportDir, config);
		}
	}
	*/
	public static int diff(File base, File test, File reportDir, String config) {
		
		FileHandler handler = null;
		try {
			File workFolder = base.getParentFile();
			File logFile = new File(workFolder, base.getName() + ".diff.log");
			handler = new FileHandler(logFile.getAbsolutePath());
			handler.setFormatter(new SimpleFormatter());
			Logger.getLogger("").addHandler(handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("Base PDF: " + base.getAbsolutePath() + "\n"
						+ "Test PDF: " + test.getAbsolutePath() + "\n"
						+ "Report folder: " + reportDir.getAbsolutePath());
		
		DiffSetting setting;
		try {
			setting = DiffSettingLoader.load(config);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "", e);
			return -1;
		}
		int diff = diffPDF(base, test, reportDir, setting);
		if (handler != null) {
			handler.close();
		}
		return diff;
	}
	
	public static int diffPDF(File base, File test, File reportDir, DiffSetting setting) {
		if (setting == null) {
			setting = DiffSetting.getDefaultSetting();	
		}
		
//		PDFDiff differ = new PDFDiff(base, test, setting);
		SignleThreadPDFDiff differ = new SignleThreadPDFDiff(base, test, setting);
		
		try {
			logger.info("Comparing PDF: " + base.getName() + " To " + test.getName());
			PDocDiffResult result = differ.diff();
			
			int count = result.countOfDiffPages();
			if (count > 0) {
				logger.info("Found " + count + " different " + (count == 1 ? "page" : "pages") + "!");
			} else {
				logger.info("PDFs are same!");
				if (setting.noReportOnSameResult) {
					return 0;
				}
			}
			
			if (!reportDir.exists() && !reportDir.mkdirs()) {
				logger.warning("Fail to create report folder.");
				throw new IOException("Can't create report folder: " + reportDir.getAbsolutePath());
			}
			
			if (setting.printReport) {
				Integer[] nums = result.getDiffPageNums();
				for (int num : nums) {
					PageDiffResult pageDiffResult = result.getPageDiffResult(num);
					List<DiffContent> contentList = pageDiffResult.getContentList();
					
					if (contentList != null) {
						for (DiffContent content : contentList) {
							logger.info(content.toString());
						}
					}
				}				
			}

			HtmlDiffReport report = new HtmlDiffReport(reportDir, "report", result, setting.showDifferentPagesOnly);
			report.toHtml();
			return count;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "", e);
		}
		return -1;
	}
	/*
	public static int diff_folder(String base, String test, String report, String config) {
		File baseDir = new File(base);
		File testDir = new File(test);
		File reportDir = new File(report);
		
		int count = 0;
		File[] baseFiles = baseDir.listFiles();
		for (File baseFile : baseFiles) {
			String name = baseFile.getName();
			if (!name.toLowerCase().endsWith(".pdf")) {
				continue;
			}
			File testFile = new File(testDir, name);
			if (!testFile.exists()) {
				continue;
			}
			
			File reportFile = new File(reportDir, name);
			count += diff(baseFile, testFile, reportFile, config);
		}
		
		return count;
	}*/
}
