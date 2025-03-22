package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X01Triplet extends Triplet {

	public static final int ID = 0x01;
	// GCSGID - X'0001'-X'FFFE' Specifies the Graphic Character Set Global Identifier
	//          X'FFFF' Specifies the character set consisting of all characters in the code page
	private int gcsgid;
	
	// CPGID - X'0001'-X'FFFE' Specifies the Code Page Global Identifier
	private int cpgid;
	
	// Coded Character Set Identifier defined by CDRA
	public static final int CCSID_EBCDIC =  500;   // CCSID for Latin 1 EBCDIC
	public static final int CCSID_UTF16  = 1200;   // CCSID for UTF16BE

	private int ccsid;
	
	public X01Triplet() {
		super();
		this.identifier = ID;
		this.name = "Coded Graphic Character Set Global Identifier";
	}
	
	public int getGcsgid() {
		return gcsgid;
	}

	public int getCpgid() {
		return cpgid;
	}
	
	public int getCcsid() {
		return this.ccsid;
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			this.gcsgid = in.readUBin(2);
			remain -= 2;
			
			if (this.gcsgid == 0) {
				this.ccsid = in.readUBin(2);
			} else {
				this.cpgid = in.readUBin(2);
			}
			
			remain -= 2;
		}
	}

}
