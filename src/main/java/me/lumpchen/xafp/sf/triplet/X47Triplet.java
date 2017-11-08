package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X47Triplet extends Triplet {

	public static final int ID = 0x47;
	
	public X47Triplet() {
		super();
		this.identifier = ID;
		this.name = "Resource Usage Attribute";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
