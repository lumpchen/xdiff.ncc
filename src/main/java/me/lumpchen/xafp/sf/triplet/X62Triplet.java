package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X62Triplet extends Triplet {

	public static final int ID = 0x62;
	
	public X62Triplet() {
		super();
		this.identifier = ID;
		this.name = "Local Date and Time Stamp";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

