package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X71Triplet extends Triplet {

	public static final int ID = 0x71;
	
	public X71Triplet() {
		super();
		this.identifier = ID;
		this.name = "Presentation Space Mixing Rules";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

