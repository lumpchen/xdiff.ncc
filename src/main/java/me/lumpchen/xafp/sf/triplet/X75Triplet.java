package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X75Triplet extends Triplet {

	public static final int ID = 0x75;
	
	public X75Triplet() {
		super();
		this.identifier = ID;
		this.name = "Color Fidelity";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

