package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X80Triplet extends Triplet {

	public static final int ID = 0x80;
	
	private long SeqNum;
	private long LevNum;
	
	public X80Triplet() {
		super();
		this.identifier = ID;
		this.name = "Attribute Qualifier";
	}
	
	public long getSeqNum() {
		return this.SeqNum;
	}
	
	public long getLevNum() {
		return this.LevNum;
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		if (remain > 0) {
			this.SeqNum = in.readUnsignedInt();
			remain -= 4;
			
			this.LevNum = in.readUnsignedInt();
			remain -= 4;
		}
	}

}

