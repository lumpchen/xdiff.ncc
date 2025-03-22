package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X8CTriplet extends Triplet {

	public static final int ID = 0x8C;
	
	public X8CTriplet() {
		super();
		this.identifier = ID;
		this.name = "Locale Selector";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
