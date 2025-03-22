package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;

public class FontOrientation extends AFPObject {
	
	/**
	 * (26*[number of repeating groups] bytes), The length of each repeating group (26 bytes) is specified in byte 14 of the FNC
	 * */
	static class FNO {
		public int CharRot;
		public int MaxBOset;
		public int MaxCharInc;
		public int SpCharInc;
		public int MaxBExt;
		public byte OrntFlgs;
		
		public int EmSpInc;
		public int FigSpInc;
		public int NomCharInc;
		public int DefBInc;
		public int MinASp;
	}

	private byte[] data;
	private List<FNO> repeatGroup;
	
	public FontOrientation(StructureField structField) throws IOException {
		super(structField);
		this.data = this.structField.getData();
	}
	
	public void parseData(int repeatGroupLen) throws IOException {
		if (data.length % repeatGroupLen > 0) {
			throw new AFPException("Invalid repeating group length (FNO): " + repeatGroupLen);
		}
		int repeat = data.length / repeatGroupLen;
		this.repeatGroup = new ArrayList<FNO>(repeat);
		AFPInputStream in = new AFPInputStream(data);
		try {
			for (int i = 0; i < repeat; i++) {
				FNO fno = new FNO();
				
				in.readBytes(2);
				fno.CharRot = in.readUBin(2);
				fno.MaxBOset = in.readUBin(2);
				fno.MaxCharInc = in.readUBin(2);
				fno.SpCharInc = in.readUBin(2);
				fno.MaxBExt = in.readUBin(2);
				fno.OrntFlgs = in.readByte();
				in.read();
				
				fno.EmSpInc = in.readUBin(2);
				
				in.readBytes(2);
				
				fno.FigSpInc = in.readUBin(2);
				fno.NomCharInc = in.readUBin(2);
				fno.DefBInc = in.readUBin(2);
				fno.MinASp = in.readSBin(2);
				
				this.repeatGroup.add(fno);
			}
		} finally {
			in.close();
		}
	}
	
}
