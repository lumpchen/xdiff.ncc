package me.lumpchen.xafp.tool;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import me.lumpchen.xafp.AFPFileReader;
import me.lumpchen.xafp.PrintFile;
import me.lumpchen.xafp.render.AFPRenderer;
import me.lumpchen.xafp.render.RenderParameter;
import me.lumpchen.xafp.tool.ui.XAFPViewer;

public class Main {

	private static Logger logger = Logger.getLogger(Main.class.getName());
	
	private static <T> T lookupProviderByName(final ServiceRegistry registry, final String providerClassName) {
	    try {
	        return (T) registry.getServiceProviderByClass(Class.forName(providerClassName));
	    } catch (ClassNotFoundException ignore) {
	        return null;
	    }
	}
	
	public static void main(String[] args) {
		Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
		while (readers.hasNext()) {
		    System.out.println("reader: " + readers.next());
		}
		
		IIORegistry registry = IIORegistry.getDefaultInstance();
		ImageReaderSpi sunProvider = lookupProviderByName(registry, "com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi");
		ImageReaderSpi twelvemonkeysProvider = lookupProviderByName(registry, "com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReaderSpi");
		
		registry.setOrdering(ImageReaderSpi.class, sunProvider, twelvemonkeysProvider);
		
		readers = ImageIO.getImageReadersByFormatName("JPEG");
		System.out.println("reader: " + readers.next());
		
		if (args.length == 0) {
			showViewer();
			return;
		}
		
		if (args.length == 1) {
			render(args);
			return;
		}
		
		if (args.length == 4) {
			renderPage(args);
			return;
		}
		
		if (args.length == 3 && args[0].equalsIgnoreCase("-dumpNop")) {
			String afpFile = args[1];
			String dumpFile = args[2];
			dumpNop(afpFile, dumpFile);
			return;
		}
		
		if (args.length == 3 && args[0].equalsIgnoreCase("-dumpTLE")) {
			String afpFile = args[1];
			String dumpFile = args[2];
			dumpTLE(afpFile, dumpFile);
			return;
		}

		System.err.println("Invalid parameters!");
	}
	
	static void dumpNop(String afpPath, String dumpPath) {
		File afpFile = new File(afpPath);
		if (!afpFile.exists()) {
			System.err.println("Invalid AFP file path: " + afpPath);
			return;
		}
		File dumpFile = new File(dumpPath);
		if (!dumpFile.exists()) {
			try {
				dumpFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!dumpFile.exists()) {
			System.err.println("Can't write dump file path: " + dumpPath);
			return;
		}
		try {
			AFPTool.dumpNoOperation(afpFile, dumpFile);
			System.out.println("All NoOperation texts are dump into " + dumpPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	static void dumpTLE(String afpPath, String dumpPath) {
		File afpFile = new File(afpPath);
		if (!afpFile.exists()) {
			System.err.println("Invalid AFP file path: " + afpPath);
			return;
		}
		File dumpFile = new File(dumpPath);
		if (!dumpFile.exists()) {
			try {
				dumpFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!dumpFile.exists()) {
			System.err.println("Can't write dump file path: " + dumpPath);
			return;
		}
		try {
			AFPTool.dumpTLE(afpFile, dumpFile);
			System.out.println("All TLEs are dump into " + dumpPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	static void render(String[] args) {
		String path = args[0];
		File afpFile = new File(path);
		
		RenderParameter para = new RenderParameter();
		
//		para.usePageResolution = true;
		para.usePageResolution = false;
		para.resolution = 600f;
		
		try {
			if (afpFile.isDirectory()) {
				File[] files = afpFile.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						if (pathname.isFile() && pathname.getName().toLowerCase().endsWith(".afp")) {
							return true;
						}
						return false;
					}
				});
				for (File f : files) {
					logger.info("Start rendering " + f.getAbsolutePath());
					String s = f.getParentFile().getAbsolutePath() + "/" + f.getName().substring(0, f.getName().length() - 4);
					File outputFolder = new File(s);
					outputFolder.mkdirs();
					
					try {
						AFPTool.render(f, outputFolder, para, "jpg");
					} catch (Exception e) {
						logger.log(Level.SEVERE, f.getAbsolutePath(), e);
						String error = f.getParentFile().getAbsolutePath() + "/fail";
						File errorFolder = new File(error);
						boolean res = f.renameTo(new File(errorFolder.getAbsolutePath() + "/" + f.getName()));
						if (res) {
							f.delete();
						}
					}
				}
			} else {
				File outputFolder = afpFile.getParentFile();
				logger.info("Start rendering: " + afpFile.getAbsolutePath());
				AFPTool.render(afpFile, outputFolder, para, "jpg");
				logger.info("Complete rendering: " + afpFile.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void renderPage(String[] args) {
		String file = args[0];
		int docIndex = Integer.parseInt(args[2]);
		int pageIndex = Integer.parseInt(args[3]);
		
		AFPFileReader reader = new AFPFileReader();
		try {
			reader.read(new File(file));
			PrintFile pf = reader.getPrintFile();
			
			AFPRenderer render = new AFPRenderer(null, pf);
			Image image = render.getPageImage(docIndex, pageIndex);
			File temp = new File(args[1]);
	        
			logger.info("Start rendering " + temp.getAbsolutePath());
			
	        temp.createNewFile();
	        ImageIO.write((BufferedImage) image, "jpg",temp);
	        reader.close();
	        
	        logger.info("Done! " + temp.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void showViewer() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		XAFPViewer viewer = new XAFPViewer();
	}
}
