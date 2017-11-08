package me.lumpchen.xafp.ioca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class BandImageData {
	
	public static final int ID_0 = 0xFE;
	public static final int ID_1 = 0x9C;
	
	private int BANDNUM;
	private byte[] data;
	
	public BandImageData() {
	}
	
	public int getBandNum() {
		return this.BANDNUM;
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	public void read(AFPInputStream in) throws IOException {
		int length = in.readUBin(2);
		
		this.BANDNUM = in.readCode();
		length -= 1;
		in.readBytes(2);
		length -= 2;
		
		if (length > 0) {
			this.data = in.readBytes(length);
		}
	}
}
