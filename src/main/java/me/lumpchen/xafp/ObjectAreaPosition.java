package me.lumpchen.xafp;

import java.io.IOException;

import me.lumpchen.xafp.sf.StructureField;

public class ObjectAreaPosition extends AFPObject {

	private int OAPosID;
	private int RGLength;
	
	private int XoaOset;
	private int YoaOset;
	
	private int XoaOrent;
	private int YoaOrent;
	
	private int XocaOset;
	private int YocaOset;
	private int XocaOrent;
	private int YocaOrent;
	private int RefCSys;
	
	public ObjectAreaPosition(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			this.OAPosID = in.readCode();
			this.RGLength = in.readUBin(1);
			
			this.XoaOset = in.readSBin(3);
			this.YoaOset = in.readSBin(3);
			
			this.XoaOrent = in.readCode(2);
			this.YoaOrent = in.readCode(2);
			
			in.read();
			
			this.XocaOset = in.readSBin(3);
			this.YocaOset = in.readSBin(3);
			this.XocaOrent = in.readCode(2);
			this.YocaOrent = in.readCode(2);
			this.RefCSys = in.readCode();
			
		} finally {
			in.close();
		}
	}

	public int getOAPosID() {
		return OAPosID;
	}

	public int getRGLength() {
		return RGLength;
	}

	public int getXoaOset() {
		return XoaOset;
	}

	public int getYoaOset() {
		return YoaOset;
	}

	public int getXoaOrent() {
		return XoaOrent;
	}

	public int getYoaOrent() {
		return YoaOrent;
	}

	public int getXocaOset() {
		return XocaOset;
	}

	public int getYocaOset() {
		return YocaOset;
	}

	public int getXocaOrent() {
		return XocaOrent;
	}

	public int getYocaOrent() {
		return YocaOrent;
	}

	public int getRefCSys() {
		return RefCSys;
	}
}