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

import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReaderSpi;

import me.lumpchen.xafp.AFPFileReader;
import me.lumpchen.xafp.PrintFile;
import me.lumpchen.xafp.render.AFPRenderer;
import me.lumpchen.xafp.render.RenderParameter;
import me.lumpchen.xafp.tool.ui.XAFPViewer;

public class Main {

	private static Logger logger = Logger.getLogger(Main.class.getName());
	
	static {
		IIORegistry registry = IIORegistry.getDefaultInstance();
		ImageReaderSpi twelvemonkeysJPEGProvider = new com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReaderSpi();
		
		TIFFImageReaderSpi twelvemonkeysTIFFProvider = new com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReaderSpi();
		twelvemonkeysTIFFProvider.onRegistration(registry, ImageReaderSpi.class);
		
		registry.registerServiceProvider(twelvemonkeysTIFFProvider);
		registry.registerServiceProvider(twelvemonkeysJPEGProvider);
		
		ImageReaderSpi sunProvider = lookupProviderByName(registry, "com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi");
		
		if (sunProvider != null) {
			registry.setOrdering(ImageReaderSpi.class, twelvemonkeysJPEGProvider, sunProvider);
		}
	}
	
	private static <T> T lookupProviderByName(final ServiceRegistry registry, final String providerClassName) {
	    try {
	        return (T) registry.getServiceProviderByClass(Class.forName(providerClassName));
	    } catch (ClassNotFoundException ignore) {
	        return null;
	    }
	}
	
	private static void showUsage() {
        String usage = "Usage: java -jar xafp.jar [options] [afp_file_path]\n"
                + "\nOptions:\n"
                + "  -dpi                             : \"-dpi 300\", otherwise, use resolution defined in file\n"
                + "  -page                            : \"-page 1 4\", page number from 1 to n\n"
        		+ "  -dumpNop                         : \"-dumpNop dump_file_path\", dump all NOPs to file\n"
        		+ "  -dumpTLE                         : \"-dumpTLE dump_file_path\", dump all TLEs to file\n";
        System.err.println(usage);
        System.exit(1);
	}
	
	private static void run(String args[]) {

		if (args == null || args.length <= 0) {
			showViewer();
			return;
		}
		
		boolean usePageResolution = true;
		int dpi = -1;
		boolean pageOpt = false;
		int from = -1, to = -1;
		boolean dumpNop = false, dumpTLE = false;
		String dumpNopFile = null;
		String dumpTLEFile = null;
		String afpFile = null;
		try {
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				
				if (arg.equals("-page")) {
					pageOpt = true;
					from = Integer.parseInt(args[++i].trim());
					to = Integer.parseInt(args[++i].trim());
				} else if (arg.equals("-dpi")) {
					usePageResolution = false;
					dpi = Integer.parseInt(args[++i].trim());
				} else if (arg.equals("-dumpNop")) {
					dumpNop = true;
					dumpNopFile = args[++i];
				} else if (arg.equals("-dumpTLE")) {
					dumpTLE = true;
					dumpTLEFile = args[++i];
				} else {
					afpFile = args[i];
				}
			}
		} catch (Exception e) {
			showUsage();
		}
		
		
		if (afpFile == null
				|| (!usePageResolution && dpi < 0)
				|| (pageOpt && (from < 0 || to < 0 || from > to))
				|| (dumpNop && dumpNopFile == null)
				|| (dumpTLE && dumpTLEFile == null)) {
			System.err.println("Invalid parameters! \n");
			showUsage();
		}
		
		if (dumpNop) {
			dumpNop(afpFile, dumpNopFile);
			return;
		}
		
		if (dumpTLE) {
			dumpTLE(afpFile, dumpTLEFile);
			return;
		}
		
		render(afpFile, usePageResolution, dpi, from, to);
	}
	
	public static void main(String[] args) {
		run(args);
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
	
	static void render(String path, boolean usePageResolution, int resolution, int from, int to) {
		File afpFile = new File(path);
		
		RenderParameter para = new RenderParameter();
		
		para.usePageResolution = usePageResolution;
		para.resolution = resolution;
		
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
//						AFPTool.render(f, outputFolder, para, "jpg");
						AFPTool.renderQuick(f, outputFolder, para, "jpg", from, to);
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
//				AFPTool.render(afpFile, outputFolder, para, "jpg");
				AFPTool.renderQuick(afpFile, outputFolder, para, "jpg", from, to);
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
