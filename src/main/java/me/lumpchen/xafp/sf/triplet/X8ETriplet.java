package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X8ETriplet extends Triplet {

	public static final int ID = 0x8E;
	
	public X8ETriplet() {
		super();
		this.identifier = ID;
		this.name = "UP3i Finishing Operation";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
