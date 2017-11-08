package me.lumpchen.xafp.ioca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class IDEStructure {

	public static final int ID = 0x9B;
	
	private int FLAGS;
	
	/**
	 * Additive or Subtractive: 
	 * 	B'0' Additive
	 * 	B'1' Subtractive
	 * */
	public static final int Additive = 0x00;
	public static final int Subtractive = 0x01;
	private int ASFLAG;
	
	/**
	 * Gray coding:
	 * 	B'0' Off
	 * 	B'1' On
	 * */
	private int GRAYCODE;
	
	/**
	 * Color model:
	 * 	X'01' RGB
	 * 	X'02' YCrCb
	 * 	X'04' CMYK
	 * 	X'12' YCbCr
	 * */
	public static final int RGB = 0x01;
	public static final int YCrCb = 0x02;
	public static final int CMYK = 0x04;
	public static final int YCbCr = 0x12;
	private int FORMAT;
	private int SIZE1, SIZE2, SIZE3, SIZE4;
	
	public IDEStructure() {
	}
	
	public int getFormat() {
		return this.FORMAT;
	}
	
	public boolean isAdditive() {
		return this.ASFLAG == Additive;
	}
	
	public void read(AFPInputStream in) throws IOException {
		int length = in.readUBin(1); // 2 or 3
		this.FLAGS = in.readByte();
		this.ASFLAG = this.FLAGS & 0x01;
		this.GRAYCODE = (this.FLAGS >> 1) & 0x01;
		length -= 1;
		
		this.FORMAT = in.readCode();
		length -= 1;
		in.readBytes(3);
		length -= 3;
		
		this.SIZE1 = in.readUBin(1);
		length -= 1;
		if (length > 0) {
			this.SIZE2 = in.readUBin(1);
			length -= 1;
		}
		
		if (length > 0) {
			this.SIZE3 = in.readUBin(1);
			length -= 1;
		}
		
		if (length > 0) {
			this.SIZE4 = in.readUBin(1);
			length -= 1;
		}
	}
}
