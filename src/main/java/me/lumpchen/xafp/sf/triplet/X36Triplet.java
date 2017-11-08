package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X36Triplet extends Triplet {

	public static final int ID = 0x36;
	
	private byte[] AttVal;
	
	public X36Triplet() {
		super();
		this.identifier = ID;
		this.name = "Attribute Value";
	}

	public byte[] getAttVal() {
		return this.AttVal;
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		in.readBytes(2);
		this.remain -= 2;
		
		if (this.remain > 0) {
			this.AttVal = in.readBytes(this.remain);
		}
		
		remain = 0;
	}

}
