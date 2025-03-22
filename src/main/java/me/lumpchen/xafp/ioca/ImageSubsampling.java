package me.lumpchen.xafp.ioca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class ImageSubsampling {

	public static final int ID = 0xFECE;
	
	public ImageSubsampling() {
		
	}
	
	public void read(AFPInputStream in) throws IOException {
		int length = in.readUBin(2);
		
		if (length > 0) {
			in.readBytes(length);
		}
	}
}
