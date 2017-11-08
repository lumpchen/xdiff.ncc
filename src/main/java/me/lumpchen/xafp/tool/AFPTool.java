package me.lumpchen.xafp.tool;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import me.lumpchen.xafp.AFPContainer;
import me.lumpchen.xafp.AFPFileReader;
import me.lumpchen.xafp.AFPObject;
import me.lumpchen.xafp.NoOperation;
import me.lumpchen.xafp.PrintFile;
import me.lumpchen.xafp.Resource;
import me.lumpchen.xafp.ResourceGroup;
import me.lumpchen.xafp.TagLogicalElement;
import me.lumpchen.xafp.render.AFPRenderer;
import me.lumpchen.xafp.render.RenderParameter;

public class AFPTool {

	private static Logger logger = Logger.getLogger(AFPTool.class.getName());
	
	public AFPTool() {
	}
	
	public static void dumpNoOperation(File afpFile, File dumpFile) throws IOException {
		PrintWriter out = null;
		
		AFPFileReader reader = null;
		try {
			out = new PrintWriter(dumpFile);
			reader = new AFPFileReader();
			reader.read(afpFile);
			PrintFile pf = reader.getPrintFile();
			
			AFPObject[] children = pf.getChildren();
			for (AFPObject obj : children) {
				if (obj instanceof ResourceGroup || obj instanceof Resource) {
					continue;
				}
				if (obj instanceof NoOperation) {
					out.println(((NoOperation) obj).getString());
				} else {
					dumpNoOperation(obj, out);
				}
			}
		} finally {
			reader.close();
			if (out != null) {
				out.close();
			}
		}
	}
	
	private static void dumpNoOperation(AFPObject obj, PrintWriter out) {
		if (obj instanceof NoOperation) {
			out.println(((NoOperation) obj).getString());
			return;
		}
		if (obj instanceof AFPContainer) {
			for (AFPObject child : ((AFPContainer) obj).getChildren()) {
				dumpNoOperation(child, out);
			}
		}
	}
	
	public static void dumpTLE(File afpFile, File dumpFile) throws IOException {
		PrintWriter out = null;
		
		AFPFileReader reader = null;
		try {
			out = new PrintWriter(dumpFile);
			reader = new AFPFileReader();
			reader.read(afpFile);
			PrintFile pf = reader.getPrintFile();
			
			AFPObject[] children = pf.getChildren();
			for (AFPObject obj : children) {
				if (obj instanceof ResourceGroup || obj instanceof Resource) {
					continue;
				}
				if (obj instanceof TagLogicalElement) {
					TagLogicalElement tle = (TagLogicalElement) obj;
					out.println(tle.toString());
				} else {
					dumpTLE(obj, out);
				}
			}
		} finally {
			reader.close();
			if (out != null) {
				out.close();
			}
		}
	}
	
	private static void dumpTLE(AFPObject obj, PrintWriter out) {
		if (obj instanceof TagLogicalElement) {
			TagLogicalElement tle = (TagLogicalElement) obj;
			out.println(tle.toString());
			return;
		}
		if (obj instanceof AFPContainer) {
			for (AFPObject child : ((AFPContainer) obj).getChildren()) {
				dumpTLE(child, out);
			}
		}
	}
	
	public static void render(File afpFile, File outputFolder, RenderParameter para, String imageFomat) throws IOException {
		AFPFileReader reader = new AFPFileReader();
		try {
			reader.read(afpFile);
			PrintFile pf = reader.getPrintFile();
			
			AFPRenderer render = new AFPRenderer(para, pf);
			int pageNo = 0;
			int docCount = pf.getDocuments().size();
			for (int i = 0; i < docCount; i++) {
				int pageCount = pf.getDocuments().get(i).getPageList().size();
				
				for (int j = 0; j < pageCount; j++) {
					Image image = render.getPageImage(i, j);
					File f = createImageFile(outputFolder, afpFile, i + 1, j + 1, imageFomat);
					ImageIO.write((BufferedImage) image, imageFomat, f);
					
					logger.info("Rendering page " + ++pageNo);
				}
			}
		} finally {
			reader.close();
		}
	}
	
	private static File createImageFile(File outputFolder, File afpFile, int docIndex, int pageIndex, String suffix) throws IOException {
		StringBuilder buf = new StringBuilder();
		String afpName = afpFile.getName();
		buf.append(afpName.substring(0, afpName.length() - 4));
		buf.append("-" + docIndex + "-" + pageIndex);
		buf.append(".").append(suffix);
		
		File f = new File(outputFolder.getAbsolutePath() + "/" + buf.toString());
		if (f.exists()) {
			return f;
		} else {
			if (f.createNewFile()) {
				return f;
			}
		}
		
		return null;
	}
}
