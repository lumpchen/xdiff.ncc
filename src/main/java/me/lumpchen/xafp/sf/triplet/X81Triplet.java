package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X81Triplet extends Triplet {

	public static final int ID = 0x81;
	
	public X81Triplet() {
		super();
		this.identifier = ID;
		this.name = "Page Position Information";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
