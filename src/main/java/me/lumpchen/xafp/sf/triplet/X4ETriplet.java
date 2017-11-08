package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X4ETriplet extends Triplet {

	public static final int ID = 0x4E;
	
	public X4ETriplet() {
		super();
		this.identifier = ID;
		this.name = "Color Specification";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

