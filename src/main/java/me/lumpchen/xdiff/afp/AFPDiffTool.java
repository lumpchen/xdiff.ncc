package me.lumpchen.xdiff.afp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.lumpchen.xdiff.DiffSetting;
import me.lumpchen.xdiff.PDocDiffResult;
import me.lumpchen.xdiff.PageDiffResult;
import me.lumpchen.xdiff.PageDiffResult.DiffContent;
import me.lumpchen.xdiff.report.HtmlDiffReport;

public class AFPDiffTool {
	
	private static Logger logger = Logger.getLogger(AFPDiffTool.class.getName());
	
	public static int diffAFP(File base, File test, File reportDir, DiffSetting setting) {
		if (setting == null) {
			setting = DiffSetting.getDefaultSetting();
		}
		
		AFPDiff differ = new AFPDiff(base, test, setting);
		
		try {
			logger.info("Comparing AFP: " + base.getName() + " To " + test.getName());
			PDocDiffResult result = differ.diff();
			
			int count = result.countOfDiffPages();
			if (count > 0) {
				logger.info("Found " + count + " different " + (count == 1 ? "page" : "pages") + "!");
			} else {
				logger.info("AFPs are same!");
				if (setting.noReportOnSameResult) {
					return 0;
				}
			}
			
			if (!result.getTLECompareResult()) {
				logger.info("Found some different TLE elements!");
			}
			
			if (!result.getNopCompareResult()) {
				logger.info("Found some different NOP elements!");
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
			if (count == 0) {
				if (!result.getTLECompareResult()) {
					count += 1;
				}
				if (!result.getTLECompareResult()) {
					count += 1;
				}
			}

			return count;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "", e);
		}
		return -1;
	}
	
}
