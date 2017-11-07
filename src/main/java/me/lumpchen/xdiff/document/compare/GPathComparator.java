package me.lumpchen.xdiff.document.compare;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class GPathComparator {

	private static BufferedImage getClippedShapeImage(List<Shape> shapeList, int pageWidth, int pageHeight) {
		BufferedImage pageImage = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D g2 = pageImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, pageWidth, pageHeight);
		g2.setColor(Color.BLACK);
		Rectangle bounds = null;
		for (Shape shape : shapeList) {
			g2.draw(shape);
			if (bounds == null) {
				bounds = shape.getBounds();
			} else {
				bounds.union(shape.getBounds());				
			}
		}
		BufferedImage clipped = pageImage.getSubimage(bounds.x, bounds.y, bounds.width + 1, bounds.height + 1);
		return clipped;
	}
	
	public static float compare(List<Shape> shapeList1, List<Shape> shapeList2, int pageWidth1, int pageHeight1, 
			int pageWidth2, int pageHeight2) {
		if (shapeList1 == null || shapeList1.isEmpty()) {
			return 0;
		}
		
		if (shapeList2 == null || shapeList2.isEmpty()) {
			return 0;
		}

		BufferedImage img1 = getClippedShapeImage(shapeList1, pageWidth1, pageHeight1);
		BufferedImage img2 = getClippedShapeImage(shapeList2, pageWidth2, pageHeight2);
		
//		try {
//			File temp = new File("c:/temp/0/1" + ".png");
//	        if (ImageIOUtil.writeImage(img1, temp.getAbsolutePath(), (int) 96)) {
//	        }
//	        
//			File temp2 = new File("c:/temp/0/2" + ".png");
//	        if (ImageIOUtil.writeImage(img2, temp2.getAbsolutePath(), (int) 96)) {
//	        }	
//		} catch (IOException e) {
//			e.printStackTrace();
//			return 0;
//		}
		
		float similarity = similarity(img1, img2);
		return similarity;
	}
	
	static float similarity(BufferedImage bim1, BufferedImage bim2) {
		if (bim1 == null || bim2 == null) {
			return 0;
		}
		
		int minWidth = Math.min(bim1.getWidth(), bim2.getWidth());
		int minHeight = Math.min(bim1.getHeight(), bim2.getHeight());
		int maxWidth = Math.max(bim1.getWidth(), bim2.getWidth());
		int maxHeight = Math.max(bim1.getHeight(), bim2.getHeight());
		
		int black = Color.BLACK.getRGB();
		int similar = 0;
		int diff = 0;
		for (int x = 0; x < minWidth; ++x) {
			for (int y = 0; y < minHeight; ++y) {
				int rgb1 = bim1.getRGB(x, y);
				int rgb2 = bim2.getRGB(x, y);
				
				if (rgb1 == black && rgb2 == black) {
					similar++;
				}
				if (rgb1 != rgb2) {
					diff++;
				}
			}
		}
		
		diff = diff / 2;
		int count = similar + diff;
		if (count == 0) {
			return 1;
		}
		float diffRatio = diff / (float) count;
		return 1 - diffRatio;
	}
}


