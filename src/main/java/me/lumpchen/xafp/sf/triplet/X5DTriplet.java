package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X5DTriplet extends Triplet {

	public static final int ID = 0x5D;
	
	public X5DTriplet() {
		super();
		this.identifier = ID;
		this.name = "Font Horizontal Scale Factor";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
