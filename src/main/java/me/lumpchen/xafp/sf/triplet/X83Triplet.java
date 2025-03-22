package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X83Triplet extends Triplet {

	public static final int ID = 0x83;
	
	public X83Triplet() {
		super();
		this.identifier = ID;
		this.name = "Presentation Control";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
