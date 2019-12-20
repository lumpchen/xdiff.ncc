package me.lumpchen.xdiff;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class XORBitmapComparator extends BitmapComparator {
	
	private Color backgroundColor;
	
	public XORBitmapComparator(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		if (this.backgroundColor == null) {
			this.backgroundColor = Color.BLACK;
		}
	}

	@Override
	public CompareResult compare(BufferedImage bim1, BufferedImage bim2) {
		if (bim1 == null) {
			return new CompareResult(1, bim2);
		}
		
		if (bim2 == null) {
			return new CompareResult(1, bim1);
		}
		
		int minWidth = Math.min(bim1.getWidth(), bim2.getWidth());
		int minHeight = Math.min(bim1.getHeight(), bim2.getHeight());
		int maxWidth = Math.max(bim1.getWidth(), bim2.getWidth());
		int maxHeight = Math.max(bim1.getHeight(), bim2.getHeight());
		BufferedImage bim3 = null;
		if (minWidth != maxWidth || minHeight != maxHeight) {
			bim3 = createEmptyDiffImage(minWidth, minHeight, maxWidth, maxHeight, this.backgroundColor);
		}
		int pxDiff = 0;
		int total = 0;
		for (int x = 0; x < minWidth; ++x) {
			for (int y = 0; y < minHeight; ++y) {
				int rgb1 = bim1.getRGB(x, y);
				int rgb2 = bim2.getRGB(x, y);
				if (rgb1 != rgb2
						&& (Math.abs((rgb1 & 0xFF) - (rgb2 & 0xFF)) > 1
								|| Math.abs(((rgb1 >> 8) & 0xFF) - ((rgb2 >> 8) & 0xFF)) > 1
								|| Math.abs(((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF)) > 1)) {
					if (bim3 == null) {
						bim3 = createEmptyDiffImage(minWidth, minHeight, maxWidth, maxHeight, this.backgroundColor);
					}
					int r = Math.abs((rgb1 & 0xFF) - (rgb2 & 0xFF));
					int g = Math.abs((rgb1 & 0xFF00) - (rgb2 & 0xFF00));
					int b = Math.abs((rgb1 & 0xFF0000) - (rgb2 & 0xFF0000));
					bim3.setRGB(x, y, 0xFFFFFF - (r | g | b));
					pxDiff++;
				}
				total++;
			}
		}
		
		float pecentage = (float) pxDiff / total;
		return new CompareResult(pecentage, bim3);
	}
	
	private static BufferedImage createEmptyDiffImage(int minWidth, int minHeight, int maxWidth, int maxHeight, Color backgroundColor) {
		BufferedImage bim3 = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = bim3.getGraphics();
		if (minWidth != maxWidth || minHeight != maxHeight) {
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, maxWidth, maxHeight);
		}
		graphics.setColor(backgroundColor);
		graphics.fillRect(0, 0, minWidth, minHeight);
		graphics.dispose();
		return bim3;
	}
	

}
