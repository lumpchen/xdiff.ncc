package me.lumpchen.xafp;

import java.awt.Color;
import java.nio.charset.Charset;

public class AFPConst {
	
	/**
	 * Page unit base for the X axis: 
	 * 			X'00' 10 inches 
	 * 			X'01' 10 centimeters
	 * */
	public static final int TenInch = 0x00;
	public static final int TenCM = 0x01;
	public static final float unitPerInch = 1440;
	
	public static final double cm2Inch(double cm) {
		return cm / 2.54;
	}

	public static final float unit2Point(float units) {
		return (units / unitPerInch) * 72;
	}
	
	public enum ColorSpace {
		RGB("RGB", 3), CMYK("CMYK", 4), Highlight("Highlight color space", 3), 
		CIELAB("CIELAB", 3), OCA("Standard OCA color space", 1);
		
		private String desc;
		private int componentLength;
		
		private ColorSpace(String desc, int componentLength) {
			this.desc = desc;
			this.componentLength = componentLength;
		}
		
		public int getComponentLength() {
			return this.componentLength;
		}
		
		public String toString() {
			return this.desc;
		}
	};
	
	public static int toDegree(int afpRotation) {
		int degree = 0;
		switch (afpRotation) {
		case 0x0000:
		case 0xFFFF:
			degree = 0;
			break;
		case 0x2D00:
			degree = 90;
			break;
		case 0x5A00:
			degree = 180;
			break;
		case 0x8700:
			degree = 270;
			break;
		default:
			throw new java.lang.IllegalArgumentException("Invalid rotation value: " + afpRotation);
		}
		return degree;
	}
	
	public static final byte Carriage_Control_Character = 0x5A;
	
	public static final char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte... bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String ebcdic2Ascii(byte[] ebcdicBytes) {
		if (ebcdicBytes == null) {
			return null;
		}
		String s = new String(ebcdicBytes, Charset.forName("IBM-1047"));
		return s;
	}
	
	public static String toUincode16BEString(byte[] bytes) {
		return new String(bytes, Charset.forName("UTF-16BE"));
	}
	
	/**
  	X'0000' or X'FF00' Device default
	X'0001' or X'FF01' Blue
	X'0002' or X'FF02' Red
	X'0003' or X'FF03' Pink/magenta
	X'0004' or X'FF04' Green
	X'0005' or X'FF05' Turquoise/cyan
	X'0006' or X'FF06' Yellow
	X'0008' Black
	X'0010' Brown
	X'FF07' Device default
	X'FF08' Reset color, also called color of medium
	X'FFFF' Default indicator
	All others Reserved
	 * */
	public static final Color toAWTColor(int flag) {
		switch (flag) {
		case 0x0001:
		case 0xFF01:
			return Color.BLUE;
		case 0x0002:
		case 0xFF02:
			return Color.RED;
		case 0x0003:
		case 0xFF03:
			return Color.MAGENTA;
		case 0x0004:
		case 0xFF04:
			return Color.GREEN;
		case 0x0005:
		case 0xFF05:
			return Color.CYAN;
		case 0x0006:
		case 0xFF06:
			return Color.YELLOW;
		case 0x0010:
			return new Color(139,69,19);
		default:
			return Color.BLACK;
		}
	}
}
