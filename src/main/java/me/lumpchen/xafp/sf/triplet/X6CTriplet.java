package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X6CTriplet extends Triplet {

	public static final int ID = 0x6C;
	
	public X6CTriplet() {
		super();
		this.identifier = ID;
		this.name = "Resource Object Include";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

