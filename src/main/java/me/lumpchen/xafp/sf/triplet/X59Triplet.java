package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X59Triplet extends Triplet {

	public static final int ID = 0x59;
	
	public X59Triplet() {
		super();
		this.identifier = ID;
		this.name = "Object Structured Field Extent";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

