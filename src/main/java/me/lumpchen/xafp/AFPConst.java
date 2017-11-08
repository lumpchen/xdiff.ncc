package me.lumpchen.xafp;

import java.nio.charset.Charset;

public class AFPConst {
	
	/**
	 * Page unit base for the X axis: 
	 * 			X'00' 10 inches 
	 * 			X'01' 10 centimeters
	 * */
	public static final int TenInch = 0x00;
	public static final int TenCM = 0x01;
	
	public static final double cm2Inch(double cm) {
		return cm / 2.54;
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
	
}
