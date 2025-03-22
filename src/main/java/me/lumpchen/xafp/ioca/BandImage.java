package me.lumpchen.xafp.ioca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class BandImage {
	public static final int ID = 0x98;
	
	private int BCOUNT;
	private int[] bitCount;
	
	public BandImage() {
	}
	
	public int getBandNumber() {
		return this.BCOUNT;
	}
	
	public int getBandsBitCount(int index) {
		if (index < 0 || index > this.BCOUNT - 1) {
			return -1;
		}
		return this.bitCount[index];
	}
	
	public void read(AFPInputStream in) throws IOException {
		int length = in.readUBin(1);
		
		this.BCOUNT = in.readUBin(1);
		length -= 1;
		this.bitCount = new int[this.BCOUNT];
		
		for (int i = 0; i < this.BCOUNT; i++) {
			this.bitCount[i] = in.readUBin(1);
			length -= 1;
		}
		
		if (length != 0) {
			throw new IOException("Band image reading error.");
		}
	}
}
