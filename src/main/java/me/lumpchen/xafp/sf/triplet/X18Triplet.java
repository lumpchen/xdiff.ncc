package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X18Triplet extends Triplet {

	public static final int ID = 0x18;
	
	public X18Triplet() {
		super();
		this.identifier = ID;
		this.name = "MO:DCA Interchange Set";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
