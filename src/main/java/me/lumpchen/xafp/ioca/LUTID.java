package me.lumpchen.xafp.ioca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class LUTID {
	public static final int ID = 0x97;
	
	private int LUTID;
	
	public LUTID() {
	}
	
	public int getLUTID() {
		return this.LUTID;
	}
	
	public void read(AFPInputStream in) throws IOException {
		int length = in.readUBin(2);
		
		this.LUTID = in.readCode();
	}
}
