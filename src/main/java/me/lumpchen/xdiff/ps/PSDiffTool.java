package me.lumpchen.xdiff.ps;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.lumpchen.xdiff.DiffSetting;
import me.lumpchen.xdiff.pdf.PDFDiffTool;

public class PSDiffTool {
	
	private static Logger logger = Logger.getLogger(PSDiffTool.class.getName());
	
	private static String[] osshell = new String[2];
	static {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName != null) {
			if (osName.indexOf("win") >= 0) {
				osshell[0] = "cmd.exe";
	            osshell[1] = "/C";
			} else if (osName.indexOf("nix") >= 0
					|| osName.indexOf("nux") >= 0
					|| osName.indexOf("aix") >= 0
					|| osName.indexOf("mac") >= 0
					|| osName.indexOf("sunos") >= 0) {
				osshell[0] = "/bin/sh";
	            osshell[1] = "-c";
			} else {
				throw new RuntimeException("Unknown os name: " + osName + ", cannot initial GhostScript.");
			}
		}
	}
	
	public static int diffPS(File base, File test, File reportDir, DiffSetting setting) throws Exception {
		String basePdf = genPDFPath(base.getAbsolutePath());
		String testPdf = genPDFPath(test.getAbsolutePath());
		
		try {
			int ret = prepare(base.getAbsolutePath(), basePdf, setting.GS_Path, setting.GS_PS2PDF_Para);
			if (ret != 0) {
				logger.log(Level.SEVERE, "GS conversion fail!");
				return -1;
			}
			
			ret = prepare(test.getAbsolutePath(), testPdf, setting.GS_Path, setting.GS_PS2PDF_Para);
			if (ret != 0) {
				logger.log(Level.SEVERE, "GS conversion fail!");
				return -1;
			}
			
			int diff = PDFDiffTool.diffPDF(new File(basePdf), new File(testPdf), reportDir, setting);
			return diff;
		} catch (Exception e) {
			throw e;
		}
	}
	
	private static String genPDFPath(String psPath) {
		return psPath + ".pdf";
	}
	
	private static int prepare(String input, String output, String gsPath, String para) throws Exception {
		if (gsPath == null || gsPath.isEmpty()) {
			logger.log(Level.SEVERE, "Not found GhostScript installatoin!", new IOException());
		}
		
		List<String> cmdList = new ArrayList<String>();
		cmdList.add(osshell[0]);
		cmdList.add(osshell[1]);
		cmdList.add(gsPath);
		cmdList.add(para);
		cmdList.add("-o");
		cmdList.add(output);
		cmdList.add(input);
		String[] runCmd = cmdList.toArray(new String[cmdList.size()]);
		
		StringBuilder cmd = new StringBuilder(gsPath);
		cmd.append(" ");
		cmd.append(para);
		cmd.append(" ");
		cmd.append("-o");
		cmd.append(" ");
		cmd.append("\"" + output + "\"");
		cmd.append(" ");
		cmd.append("\"" + input + "\"");
		String shellCmd = osshell[0] + " " + osshell[1] + " " + cmd.toString();
		logger.log(Level.INFO, shellCmd);
		
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ProcessBuilder pb = new ProcessBuilder(runCmd);
			Map<String,String> env = pb.environment();
			Process process = pb.start();
			
			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), errorStream);
			StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), outputStream);
			errorGobbler.start();
			outputGobbler.start();
			
			int ret = process.waitFor();
			errorGobbler.join();
			outputGobbler.join();
			if (ret == 0) {
				logger.info("Ghostscript command: " + shellCmd + " run successfully.");
				if (outputStream.size() > 0) {
					logger.info(outputStream.toString());
				}
			} else {
				File outputFile = new File(output);
				if (outputFile != null && outputFile.exists() && outputFile.length() > 0) {
					// command run success with warning
					logger.info("Ghostscript command: " + shellCmd + " run successfully with warning.");
					if (errorStream.size() > 0) {
						logger.warning(errorStream.toString());
					}
					return 0;
				} else {
					logger.warning("Ghostscript command: " + shellCmd + " failed to run.");
					if (errorStream.size() > 0) {
						logger.warning(errorStream.toString());
					}
				}
			}
			return ret;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Ghostscript command: " + shellCmd + " failed to run.",  e);
			throw e;
		}
	}
	
	static class StreamGobbler extends Thread {

		private InputStream in;
		private OutputStream out;
		
		public StreamGobbler(InputStream in, OutputStream redirect) {
			this.in = in;
			this.out = redirect;
		}
		
		public void run() {
			PrintWriter writer = new PrintWriter(this.out);
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.in));
			String line = null;
			try {
				while ( (line = reader.readLine()) != null) {
					writer.println(line);
				}
				writer.flush();
			} catch (IOException e) {
				logger.log(Level.WARNING, "StreamGobbler error", e);
			}
		}
	}
}
