package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X91Triplet extends Triplet {

	public static final int ID = 0x91;
	
	public X91Triplet() {
		super();
		this.identifier = ID;
		this.name = "Color Management Resource Descriptor";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
