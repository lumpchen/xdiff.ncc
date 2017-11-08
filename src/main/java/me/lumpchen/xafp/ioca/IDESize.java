package me.lumpchen.xafp.ioca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class IDESize {
	public static final int ID = 0x96;
	
	private int IDESZ;
	
	public IDESize() {
	}
	
	public int getSize() {
		return this.IDESZ;
	}
	
	public void read(AFPInputStream in) throws IOException {
		int length = in.readUBin(1);
		
		this.IDESZ = in.readUBin(1);
	}
}
