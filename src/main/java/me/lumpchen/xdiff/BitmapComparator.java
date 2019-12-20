package me.lumpchen.xdiff;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public abstract class BitmapComparator {

	public enum Mode {
		XOR, Mixed
	};

	public static class CompareResult {
		public CompareResult(float pecentage, BufferedImage diffImage) {
			this.pecentage = pecentage;
			this.diffImage = diffImage;
		}
		
		public float pecentage;
		public BufferedImage diffImage;
	}

	public static BitmapComparator getComparator(Mode type) {
		if (type == Mode.XOR) {
			return new XORBitmapComparator(Color.BLACK);
		} else if (type == Mode.Mixed) {
			return new MixdBitmapComparator(Color.MAGENTA, Color.CYAN);
		} else {
			return new MixdBitmapComparator(Color.MAGENTA, Color.CYAN);
		}
	}
	
	public static BitmapComparator getComparator(Mode mode, String color1, String color2) {
		if (mode == Mode.XOR) {
			return new XORBitmapComparator(getColor(color1));
		} else if (mode == Mode.Mixed) {
			return new MixdBitmapComparator(getColor(color1), getColor(color2));
		} else {
			return new MixdBitmapComparator(getColor(color1), getColor(color2));
		}
	}
	
	public static BitmapComparator getComparator(String mode, String color1, String color2) {
		if ("xor".equalsIgnoreCase(mode)) {
			return new XORBitmapComparator(getColor(color1));
		} else if ("mixed".equalsIgnoreCase(mode)) {
			return new MixdBitmapComparator(getColor(color1), getColor(color2));
		} else {
			return new MixdBitmapComparator(getColor(color1), getColor(color2));
		}
	}

	public abstract CompareResult compare(BufferedImage bim1, BufferedImage bim2);
	
	static Color getColor(String colorName) {
		if (colorName == null) {
			return null;
		}
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
			Logger.getLogger(BitmapComparator.class.getName()).warning("Invalid color name: " + colorName + ". Using default color BLACK.");
			c = Color.BLACK;
		} 
		return c;
	}
}
