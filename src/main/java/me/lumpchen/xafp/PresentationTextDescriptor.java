package me.lumpchen.xafp;

import java.io.IOException;

import me.lumpchen.xafp.sf.StructureField;

public class PresentationTextDescriptor extends AFPObject {

	private int XPBASE;
	private int YPBASE;
	private int XPUNITVL;
	private int YPUNITVL;
	private int XPEXTENT;
	private int YPEXTENT;
	
	public PresentationTextDescriptor(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}

	public void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			this.XPBASE = in.readCode();
			this.YPBASE = in.readCode();
			
			this.XPUNITVL = in.readUBin(2);
			this.YPUNITVL = in.readUBin(2);
			this.XPEXTENT = in.readUBin(2);
			this.YPEXTENT = in.readUBin(2);
			
		} finally {
			in.close();
		}
	}

	public int getXPBASE() {
		return XPBASE;
	}

	public int getYPBASE() {
		return YPBASE;
	}

	public int getXPUNITVL() {
		return XPUNITVL;
	}

	public int getYPUNITVL() {
		return YPUNITVL;
	}

	public int getXPEXTENT() {
		return XPEXTENT;
	}

	public int getYPEXTENT() {
		return YPEXTENT;
	}
}
