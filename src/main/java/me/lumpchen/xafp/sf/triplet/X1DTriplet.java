package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X1DTriplet extends Triplet {

	public static final int ID = 0x1D;
	
	public X1DTriplet() {
		super();
		this.identifier = ID;
		this.name = "X1DTriplet";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

