package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X85Triplet extends Triplet {

	public static final int ID = 0x85;
	
	public X85Triplet() {
		super();
		this.identifier = ID;
		this.name = "Finishing Operation";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
