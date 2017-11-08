package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X25Triplet extends Triplet {

	public static final int ID = 0x25;
	
	public X25Triplet() {
		super();
		this.identifier = ID;
		this.name = "Resource Section Number";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
