package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X97Triplet extends Triplet {

	public static final int ID = 0x97;
	
	public X97Triplet() {
		super();
		this.identifier = ID;
		this.name = "Device Appearance";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

