package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X68Triplet extends Triplet {

	public static final int ID = 0x68;
	
	public X68Triplet() {
		super();
		this.identifier = ID;
		this.name = "Medium Orientation";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

