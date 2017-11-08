package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X5ATriplet extends Triplet {

	public static final int ID = 0x5A;
	
	public X5ATriplet() {
		super();
		this.identifier = ID;
		this.name = "Object Offset";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
