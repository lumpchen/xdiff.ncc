package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X88Triplet extends Triplet {

	public static final int ID = 0x88;
	
	public X88Triplet() {
		super();
		this.identifier = ID;
		this.name = "Finishing Fidelity";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
