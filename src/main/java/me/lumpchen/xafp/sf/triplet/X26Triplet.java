package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X26Triplet extends Triplet {

	public static final int ID = 0x26;
	
	public X26Triplet() {
		super();
		this.identifier = ID;
		this.name = "Character Rotation";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}

