package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X86Triplet extends Triplet {

	public static final int ID = 0x86;
	
	public X86Triplet() {
		super();
		this.identifier = ID;
		this.name = "Text Fidelity";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

}
