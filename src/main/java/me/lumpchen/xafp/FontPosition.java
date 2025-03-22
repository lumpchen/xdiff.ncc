package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;

public class FontPosition extends AFPObject {

	static class FNP {

		public int LcHeight;
		public int CapMHt;
		public int MaxAscHt;
		public int MaxDesDp;
		public int UscoreWd;
		public int UscoreWdf;
		public int UscorePos;
	}
	
	private byte[] data;
	private List<FNP> repeatGroup;
	
	public FontPosition(StructureField structField) throws IOException {
		super(structField);
		this.data = this.structField.getData();
	}
	
	public void parseData(int repeatGroupLen) throws IOException {
		if (data.length % repeatGroupLen > 0) {
			throw new AFPException("Invalid repeating group length (FNP): " + repeatGroupLen);
		}
		int repeat = data.length / repeatGroupLen;
		this.repeatGroup = new ArrayList<FNP>(repeat);
		AFPInputStream in = new AFPInputStream(data);
		try {
			for (int i = 0; i < repeat; i++) {
				FNP fnp = new FNP();
				
				in.readBytes(2);
				fnp.LcHeight = in.readSBin(2);
				fnp.CapMHt = in.readSBin(2);
				fnp.MaxAscHt = in.readSBin(2);
				fnp.MaxDesDp = in.readSBin(2);
				
				in.readBytes(5);
				in.read();
				in.read();
				fnp.UscoreWd = in.readUBin(2);
				fnp.UscoreWdf = in.readUBin(1);
				fnp.UscorePos = in.readSBin(2);
				this.repeatGroup.add(fnp);
			}
		} finally {
			in.close();
		}
	}
}
