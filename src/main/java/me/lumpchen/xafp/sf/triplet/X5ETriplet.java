package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X5ETriplet extends Triplet {

	public static final int ID = 0x5E;
	
	public X5ETriplet() {
		super();
		this.identifier = ID;
		this.name = "Object Count";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
