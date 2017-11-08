package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X22Triplet extends Triplet {

	public static final int ID = 0x22;
	
	public X22Triplet() {
		super();
		this.identifier = ID;
		this.name = "Extended Resource Local Identifier";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
