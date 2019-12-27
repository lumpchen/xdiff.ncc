package me.lumpchen.xdiff;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class MixdBitmapComparator extends BitmapComparator {

	private int rgbColor1;
	private int rgbColor2;
	
	public MixdBitmapComparator(Color color1, Color color2) {
		if (color1 == null) {
			color1 = Color.MAGENTA;
		}
		if (color2 == null) {
			color2 = Color.CYAN;
		}
		this.rgbColor1 = color1.getRGB();
		this.rgbColor2 = color2.getRGB();
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
		BufferedImage bim3 = createDiffImage(minWidth, minHeight, maxWidth, maxHeight);
		long diffpx = 0, total = 0;
		for (int x = 0; x < minWidth; ++x) {
			for (int y = 0; y < minHeight; ++y) {
				int rgb1 = bim1.getRGB(x, y);
				int rgb2 = bim2.getRGB(x, y);
				if (rgb1 != rgb2
						&& (Math.abs((rgb1 & 0xFF) - (rgb2 & 0xFF)) > 1
								|| Math.abs(((rgb1 >> 8) & 0xFF) - ((rgb2 >> 8) & 0xFF)) > 1
								|| Math.abs(((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF)) > 1)) {
					if (rgb2 == -1) {
						if (rgb1 != -1) {
							int r = (rgb1 & 0xFF);
							int g = ((rgb1 >> 8) & 0xFF);
							int b = ((rgb1 >> 16) & 0xFF);
							
							int grayscale = 255 - Math.round(0.2126f * r + 0.7152f * g + 0.0722f * b);
							bim3.setRGB(x, y, new Color(255, 0, 255, grayscale).getRGB());
						} else {
							bim3.setRGB(x, y, this.rgbColor1);
						}
					} else if (rgb1 == -1) {
						if (rgb2 != -1) {
							int r = (rgb2 & 0xFF);
							int g = ((rgb2 >> 8) & 0xFF);
							int b = ((rgb2 >> 16) & 0xFF);
							
							int grayscale = 255 - Math.round(0.2126f * r + 0.7152f * g + 0.0722f * b);
							bim3.setRGB(x, y, new Color(0, 255, 255, grayscale).getRGB());
						} else {
							bim3.setRGB(x, y, this.rgbColor2);
						}
					}
					diffpx++;
					total++;
				} else {
					bim3.setRGB(x, y, rgb1);
					if (rgb1 == -1) {
						total++;
					}
				}
			}
		}
		
		float pecentage = (float) diffpx / total;
		return new CompareResult(pecentage, bim3);
	}
	
	private static BufferedImage createDiffImage(int minWidth, int minHeight, int maxWidth, int maxHeight) {
		BufferedImage bim3 = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
		return bim3;
	}
}
