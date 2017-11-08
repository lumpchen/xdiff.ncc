package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X84Triplet extends Triplet {

	public static final int ID = 0x84;
	
	public X84Triplet() {
		super();
		this.identifier = ID;
		this.name = "Font Resolution and Metric Technology";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

