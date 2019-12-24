package me.lumpchen.xdiff;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;

import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReaderSpi;

import me.lumpchen.xdiff.XDiff.FILE_FORMAT;
import me.lumpchen.xdiff.afp.AFPDiffTool;
import me.lumpchen.xdiff.pdf.PDFDiffTool;
import me.lumpchen.xdiff.ps.PSDiffTool;

public class XDiff2 {
	
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
		int startPageOfControl = -1;
		int startPageOfTest = -1;
		int compareCount = -1;
		
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			
			if (arg.equals("-folder")) {
				folderCompare = true;
			} else if (arg.equals("-config")) {
				config = args[++i];
			} else if (arg.equals("-control_start_page")) {
				String pageStr = args[++i];
				try {
					startPageOfControl = Integer.parseInt(pageStr.trim());	
				} catch (Exception e) {
					System.err.println("Invalid control_start_page: " + pageStr + "\n");
				}
			} else if (arg.equals("-test_start_page")) {
				String pageStr = args[++i];
				try {
					startPageOfTest = Integer.parseInt(pageStr.trim());	
				} catch (Exception e) {
					System.err.println("Invalid test_start_page: " + pageStr + "\n");
				}
			} else if (arg.equals("-compare_count")) {
				String pageStr = args[++i];
				try {
					compareCount = Integer.parseInt(pageStr.trim());	
				} catch (Exception e) {
					System.err.println("Invalid compare_count: " + pageStr + "\n");
				}
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
		
		int diff = 0;
		if (folderCompare) {
//			diff = diff_folder(base, test, result, config, fromPage, toPage);
		} else {
//			diff = diff(base, test, result, config, fromPage, toPage);
		}
		
		System.exit(diff);
	}

}

