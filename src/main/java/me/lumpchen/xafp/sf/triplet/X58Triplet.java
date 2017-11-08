package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X58Triplet extends Triplet {

	public static final int ID = 0x58;
	
	public X58Triplet() {
		super();
		this.identifier = ID;
		this.name = "Object Structured Field Offset";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

