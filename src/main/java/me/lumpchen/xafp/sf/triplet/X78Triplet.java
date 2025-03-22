package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X78Triplet extends Triplet {

	public static final int ID = 0x78;
	
	public X78Triplet() {
		super();
		this.identifier = ID;
		this.name = "Font Fidelity";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

