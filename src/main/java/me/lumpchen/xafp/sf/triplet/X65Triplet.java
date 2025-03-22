package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X65Triplet extends Triplet {

	public static final int ID = 0x65;
	
	public X65Triplet() {
		super();
		this.identifier = ID;
		this.name = "Comment";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

