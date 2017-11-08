package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X45Triplet extends Triplet {

	public static final int ID = 0x45;
	
	public X45Triplet() {
		super();
		this.identifier = ID;
		this.name = "Media Eject Control";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

