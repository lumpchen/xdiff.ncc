package me.lumpchen.xafp;

import java.io.IOException;

import me.lumpchen.xafp.sf.StructureField;

public class CodePageControl extends AFPObject {
	
	private byte[] DefCharID;
	private int PrtFlags;
	private int CPIRGLen;
	private int VSCharSN;
	private int VSChar;
	private int VSFlags;

	public CodePageControl(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			this.DefCharID = in.readBytes(8);
			this.PrtFlags = in.read();
			this.CPIRGLen = in.readCode();
			this.VSCharSN = in.readUBin(1);
			this.VSChar = in.readUBin(1);
			this.VSFlags = in.read();
		} finally {
			in.close();
		}
	}
	
	public byte[] getDefaultCharID() {
		return this.DefCharID;
	}
	
	public int getCPIRGLen() {
		return CPIRGLen;
	}
}
