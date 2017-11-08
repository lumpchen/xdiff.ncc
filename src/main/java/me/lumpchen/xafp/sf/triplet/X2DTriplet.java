package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X2DTriplet extends Triplet {

	public static final int ID = 0x27;
	
	public X2DTriplet() {
		super();
		this.identifier = ID;
		this.name = "Object Byte Offset";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

