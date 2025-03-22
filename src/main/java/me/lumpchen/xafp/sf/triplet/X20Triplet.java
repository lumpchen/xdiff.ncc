package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X20Triplet extends Triplet {

	public static final int ID = 0x20;
	
	public X20Triplet() {
		super();
		this.identifier = ID;
		this.name = "Font Coded Graphic Character Set Global Identifier";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
