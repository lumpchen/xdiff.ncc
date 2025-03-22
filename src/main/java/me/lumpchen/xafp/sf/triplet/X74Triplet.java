package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X74Triplet extends Triplet {

	public static final int ID = 0x74;
	
	public X74Triplet() {
		super();
		this.identifier = ID;
		this.name = "Toner Saver";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
