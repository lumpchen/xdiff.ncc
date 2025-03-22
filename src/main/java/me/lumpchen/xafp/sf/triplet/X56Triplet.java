package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X56Triplet extends Triplet {

	public static final int ID = 0x56;
	
	public X56Triplet() {
		super();
		this.identifier = ID;
		this.name = "Medium Map Page Number";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

