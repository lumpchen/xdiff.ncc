package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X96Triplet extends Triplet {

	public static final int ID = 0x96;
	
	public X96Triplet() {
		super();
		this.identifier = ID;
		this.name = "CMR Tag Fidelity";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

