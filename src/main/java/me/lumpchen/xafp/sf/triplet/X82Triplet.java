package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X82Triplet extends Triplet {

	public static final int ID = 0x82;
	
	public X82Triplet() {
		super();
		this.identifier = ID;
		this.name = "Parameter Value";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
