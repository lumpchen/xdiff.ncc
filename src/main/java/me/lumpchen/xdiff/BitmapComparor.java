package me.lumpchen.xdiff;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.GrayFilter;

import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class BitmapComparor {
	
	public static BufferedImage diffImages(BufferedImage bim1, Rectangle2D rect1, BufferedImage bim2, Rectangle2D rect2) throws IOException {
		BufferedImage dest1 = cropImage(bim1, rect1);
		BufferedImage dest2 = cropImage(bim2, rect2);
		
		BufferedImage xor = diffImages(dest1, dest2, "black");
		
		if (xor != null) {
			File temp = File.createTempFile("pdf_diff", "." + "png");
	        if (ImageIOUtil.writeImage(xor, temp.getAbsolutePath(), (int) 96)) {
	        	temp.getAbsolutePath();
	        }			
		}
		
        return xor;
	}
	
	public static BufferedImage cropImage(BufferedImage src, Rectangle2D rect) {
		if (src == null) {
			return null;
		}
		
		try {
			float res = 96;
			int x = toPixel(res, rect.getX());
			int y = toPixel(res, rect.getY());
			int w = toPixel(res, rect.getWidth()) - 1;
			int h = toPixel(res, rect.getHeight()) - 1;
			BufferedImage dest = src.getSubimage(x, y, w, h);
			
			Image grayImage = GrayFilter.createDisabledImage(src);
//			ImageFilter filter = new GrayFilter(true, 50);  
//			ImageProducer producer = new FilteredImageSource(dest.getSource(), filter);  
//			Image grayImage = Toolkit.getDefaultToolkit().createImage(producer);  
			
			dest = toBufferedImage(grayImage);
			
			File temp = File.createTempFile("mid_", "." + "png");
	        if (ImageIOUtil.writeImage(dest, temp.getAbsolutePath(), (int) 96)) {
	        	temp.getAbsolutePath();
	        }	
	        
			return dest;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static BufferedImage toBufferedImage(Image img) {
	    if (img instanceof BufferedImage) {
	        return (BufferedImage) img;
	    }
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
	    return bimage;
	}
	
	public static int toPixel(float resolution, double pt) {
		int pixel = (int) Math.round((pt / 72) * resolution);
		return pixel;
	}
	
	public static BufferedImage diffImages(BufferedImage bim1, BufferedImage bim2, String background) throws IOException {
		if (bim1 == null) {
			return bim2;
		}
		
		if (bim2 == null) {
			return bim1;
		}
		
		int minWidth = Math.min(bim1.getWidth(), bim2.getWidth());
		int minHeight = Math.min(bim1.getHeight(), bim2.getHeight());
		int maxWidth = Math.max(bim1.getWidth(), bim2.getWidth());
		int maxHeight = Math.max(bim1.getHeight(), bim2.getHeight());
		BufferedImage bim3 = null;
		if (minWidth != maxWidth || minHeight != maxHeight) {
			bim3 = createEmptyDiffImage(minWidth, minHeight, maxWidth, maxHeight, background);
		}
		int pxDiff = 0;
		int lineDiff = 0;
		for (int x = 0; x < minWidth; ++x) {
			int lastPxDiff = pxDiff;
			for (int y = 0; y < minHeight; ++y) {
				int rgb1 = bim1.getRGB(x, y);
				int rgb2 = bim2.getRGB(x, y);
				if (rgb1 != rgb2
						&& (Math.abs((rgb1 & 0xFF) - (rgb2 & 0xFF)) > 1
								|| Math.abs(((rgb1 >> 8) & 0xFF) - ((rgb2 >> 8) & 0xFF)) > 1
								|| Math.abs(((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF)) > 1)) {
					if (bim3 == null) {
						bim3 = createEmptyDiffImage(minWidth, minHeight, maxWidth, maxHeight, background);
					}
					int r = Math.abs((rgb1 & 0xFF) - (rgb2 & 0xFF));
					int g = Math.abs((rgb1 & 0xFF00) - (rgb2 & 0xFF00));
					int b = Math.abs((rgb1 & 0xFF0000) - (rgb2 & 0xFF0000));
					bim3.setRGB(x, y, 0xFFFFFF - (r | g | b));
					pxDiff++;
				} else {
//					if (bim3 != null) {
//						bim3.setRGB(x, y, Color.BLACK.getRGB());
//					}
				}
			}
			if (pxDiff != lastPxDiff) {
				lineDiff++;
			}
		}
		
//		int total = minWidth * minHeight;
//		float percent = (float) pxDiff / total;
//		System.out.println("Rendering bitmap different percentage: " + percent * 100);
		return bim3;
	}

	private static BufferedImage createEmptyDiffImage(int minWidth, int minHeight, int maxWidth, int maxHeight, String background) {
		BufferedImage bim3 = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = bim3.getGraphics();
		if (minWidth != maxWidth || minHeight != maxHeight) {
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, maxWidth, maxHeight);
		}
		graphics.setColor(getColor(background));
		graphics.fillRect(0, 0, minWidth, minHeight);
		graphics.dispose();
		return bim3;
	}
	
	private static Color getColor(String colorName) {
		Color c = null;
		if (colorName.equalsIgnoreCase("black")) {
			c = Color.BLACK;
		} else if (colorName.equalsIgnoreCase("white")) {
			c = Color.WHITE;
		} else if (colorName.equalsIgnoreCase("red")) {
			c = Color.RED;
		} else if (colorName.equalsIgnoreCase("green")) {
			c = Color.GREEN;
		} else if (colorName.equalsIgnoreCase("blue")) {
			c = Color.BLUE;
		} else if (colorName.equalsIgnoreCase("CYAN")) {
			c = Color.CYAN;
		} else if (colorName.equalsIgnoreCase("MAGENTA")) {
			c = Color.MAGENTA;
		} else if (colorName.equalsIgnoreCase("yellow")) {
			c = Color.YELLOW;
		} else {
			Logger.getLogger(BitmapComparor.class.getName()).warning("Invalid color name: " + colorName + ". Using color BLACK.");
			c = Color.BLACK;
		} 
		return c;
	}
	
}
