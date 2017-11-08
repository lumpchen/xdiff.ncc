package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X72Triplet extends Triplet {

	public static final int ID = 0x72;
	
	public X72Triplet() {
		super();
		this.identifier = ID;
		this.name = "Universal Date and Time Stamp";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
