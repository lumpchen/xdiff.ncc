package me.lumpchen.xdiff;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;

import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReaderSpi;

import me.lumpchen.xdiff.afp.AFPDiffTool;
import me.lumpchen.xdiff.pdf.PDFDiffTool;
import me.lumpchen.xdiff.ps.PSDiffTool;

public class XDiff {
	
	private static Logger logger = Logger.getLogger(PDFDiffTool.class.getName());
	public static enum FILE_FORMAT{PDF, PS, AFP};
	
	static {
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		
		IIORegistry registry = IIORegistry.getDefaultInstance();
		ImageReaderSpi twelvemonkeysJPEGProvider = new com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReaderSpi();
		
		TIFFImageReaderSpi twelvemonkeysTIFFProvider = new com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReaderSpi();
		twelvemonkeysTIFFProvider.onRegistration(registry, ImageReaderSpi.class);
		
		registry.registerServiceProvider(twelvemonkeysTIFFProvider);
		registry.registerServiceProvider(twelvemonkeysJPEGProvider);
	}

	public static void main(String[] args) {
		run(args);
	}
	
	private static void showUsage() {
/*        String usage = "Usage: java -jar xdiff.jar [options] <baseline-pdf> <test-pdf> <result-folder>\n"
                + "\nOptions:\n"
                + "  -config                            : Comparison configuration file path.\n"
        		+ "  -from_page                         : Start page to compare, begin page 1.\n"
        		+ "  -to_page                           : End page to compare.\n";*/
        
        String usage = "Usage: java -jar xdiff.jar [options] <baseline-pdf> <test-pdf> <result-folder>\n"
                + "\nOptions:\n"
                + "  -config                            : Comparison configuration file path.\n"
        		+ "  -control_start_page                : Start page of control document, default start page is 1.\n"
        		+ "  -test_start_page                	: Start page of test document, default start page is 1.\n"
        		+ "  -compare_count                     : Total pages to compare.default to end of document.\n";
		System.err.println(usage);
        System.exit(1);
	}
	
	
	
	private static void run(String args[]) {

		if (args == null || args.length < 3) {
			showUsage();
		}
		
		boolean folderCompare = false;
		String base = null;
		String test = null;
		String result = null;
		String config = null;
		int control_start_page = -1;
		int test_start_page = -1;
		int compare_count = -1;
		
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			
			if (arg.equals("-folder")) {
				folderCompare = true;
			} else if (arg.equals("-config")) {
				config = args[++i];
			} else if (arg.equals("-control_start_page")) {
				String pageStr = args[++i];
				try {
					control_start_page = Integer.parseInt(pageStr.trim());	
				} catch (Exception e) {
					System.err.println("Invalid -control_start_page: " + pageStr + "\n");
				}
			} else if (arg.equals("-test_start_page")) {
				String pageStr = args[++i];
				try {
					test_start_page = Integer.parseInt(pageStr.trim());	
				} catch (Exception e) {
					System.err.println("Invalid -test_start_page: " + pageStr + "\n");
				}
			} else if (arg.equals("-compare_count")) {
				String pageStr = args[++i];
				try {
					compare_count = Integer.parseInt(pageStr.trim());	
				} catch (Exception e) {
					System.err.println("Invalid -compare_count: " + pageStr + "\n");
				}
/*			}
			
			else if (arg.equals("-from_page")) {
				String pageStr = args[++i];
				try {
					fromPage = Integer.parseInt(pageStr);	
				} catch (Exception e) {
					System.err.println("Invalid page number: " + pageStr + "\n");
				}
			} else if (arg.equals("-to_page")) {
				String pageStr = args[++i];
				try {
					toPage = Integer.parseInt(pageStr);	
				} catch (Exception e) {
					System.err.println("Invalid page number: " + pageStr + "\n");
				}*/
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
		
//		if (fromPage > toPage) {
//			System.err.println("Invalid page range "
//					+ "from " + fromPage + " to " + toPage + " \n");
//			showUsage();
//		}

		int diff = 0;
		if (folderCompare) {
			diff = diff_folder(base, test, result, config);
		} else {
			diff = diff(base, test, result, config, control_start_page, test_start_page, compare_count, null);
		}
		
		System.exit(diff);
	}
	
	public static int diff_folder(String base, String test, String report, String config) {
		return diff(base, test, report, config, -1, -1, -1, null);
	}
	
	public static int diff(String base, String test, String reportDir, String config, 
			int control_start_page, int test_start_page, int compare_count, ProgressListener progressListener) {
		if (progressListener == null) {
			progressListener = new DefaultProgressListener();
		}
		
		File baseFile = new File(base);
		File testFile = new File(test);
		
		boolean invalid = false;
		if (!baseFile.exists()) {
			invalid = true;
			logger.severe("Control path is not exist: " + base);
		}
		if (!testFile.exists()) {
			invalid = true;
			logger.severe("Test path is not exist: " + base);
		}
		if (invalid) {
			logger.severe("Invalid parameters, please check parameters!");
			return -1;
		}
		
		if (baseFile.isFile() && testFile.isFile()) {
			return diff(new File(base), new File(test), new File(reportDir), config, 
					control_start_page, test_start_page, compare_count, progressListener);
		} else if (baseFile.isDirectory() && testFile.isDirectory()) {
			return diff_folder(base, test, reportDir, config, progressListener);
		} else {
			logger.severe("Invalid parameters, please check parameters!");
			return -1;
		}
	}
	
	public static int diff_folder(String base, String test, String report, String config, ProgressListener progressListener) {
		File baseDir = new File(base);
		File testDir = new File(test);
		File reportDir = new File(report);
		
		int count = 0;
		File[] baseFiles = baseDir.listFiles();
		for (File baseFile : baseFiles) {
			String name = baseFile.getName();
//			if (!name.toLowerCase().endsWith(".pdf")) {
//				continue;
//			}
			File testFile = new File(testDir, name);
			if (!testFile.exists()) {
				continue;
			}
			
			File reportFile = new File(reportDir, name);
			count += diff(baseFile, testFile, reportFile, config, -1, -1, -1, progressListener);
		}
		
		return count;
	}
	
	public static int diff(File base, File test, File reportDir, String config, 
			int control_start_page, int test_start_page, int compare_count, ProgressListener progressListener) {
		DiffSetting setting;
		if (config == null) {
			setting = DiffSetting.getDefaultSetting();
		} else {
			try {
				setting = DiffSettingLoader.load(config);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "", e);
				return -1;
			}
		}
		
		if (progressListener == null) {
			progressListener = new DefaultProgressListener();
		}
		setting.progressListener = progressListener;
		setting.startPageOfControl = control_start_page;
		setting.startPageOfTest = test_start_page;
		setting.compareCount = compare_count;
		
		return diff(base, test, reportDir, setting);
	}
	
	public static int diff(File base, File test, File reportDir, DiffSetting setting) {
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
		
		logger.info("Control file: " + base.getAbsolutePath() + "\n"
						+ "Test file: " + test.getAbsolutePath() + "\n"
						+ "Report folder: " + reportDir.getAbsolutePath());
		
		FILE_FORMAT baseFormat = checkSuffix(base);
		FILE_FORMAT testFormat = checkSuffix(test);
		if (baseFormat == null || testFormat == null || baseFormat != testFormat) {
			logger.log(Level.SEVERE, "Baseline and test file have different format!");
			return -1;
		}
		
		int diff = -1;
		if (baseFormat == FILE_FORMAT.PDF) {
			logger.info("---PDF comparison begin---");
			diff = PDFDiffTool.diffPDF(base, test, reportDir, setting);
			logger.info("---PDF comparison end---");
		} else if (baseFormat == FILE_FORMAT.PS) {
			try {
				logger.info("---PostScript comparison begin---");
				diff = PSDiffTool.diffPS(base, test, reportDir, setting);
				logger.info("---PostScript comparison end---"); 
			} catch (Exception e) {
				logger.log(Level.SEVERE, "PS comparing fail!");
			}
		} else if (baseFormat == FILE_FORMAT.AFP) {
			logger.info("---AFP comparison begin---");
			diff = AFPDiffTool.diffAFP(base, test, reportDir, setting);
			logger.info("---AFP comparison end---");
		}
		
		if (handler != null) {
			handler.close();
		}
		return diff;
	}
	
	private static FILE_FORMAT checkSuffix(File file) {
		String name = file.getName();
		if (name == null || name.indexOf(".") <= 0) {
			return null;
		}
		String suffix = name.substring(name.lastIndexOf("." ) + 1);
		
		if ("pdf".equalsIgnoreCase(suffix)) {
			return FILE_FORMAT.PDF;
		} else if ("ps".equalsIgnoreCase(suffix)) {
			return FILE_FORMAT.PS;
		} else if ("afp".equalsIgnoreCase(suffix)) {
			return FILE_FORMAT.AFP;
		}
		
		return null;
	}

	public static class DefaultProgressListener implements ProgressListener {

		@Override
		public void progress(float progress) {
			logger.info("Comparing progress: " + progress);
		}
	}
}


