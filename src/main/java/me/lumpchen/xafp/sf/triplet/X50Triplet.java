package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X50Triplet extends Triplet {

	public static final int ID = 0x50;
	
	public X50Triplet() {
		super();
		this.identifier = ID;
		this.name = "Encoding Scheme ID";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

