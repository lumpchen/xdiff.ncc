package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X70Triplet extends Triplet {

	public static final int ID = 0x70;
	
	public X70Triplet() {
		super();
		this.identifier = ID;
		this.name = "Presentation Space Reset Mixing";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

