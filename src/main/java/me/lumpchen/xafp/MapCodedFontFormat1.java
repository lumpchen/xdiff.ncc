package me.lumpchen.xafp;

import java.io.IOException;

import me.lumpchen.xafp.sf.StructureField;

public class MapCodedFontFormat1  extends AFPObject {

	public MapCodedFontFormat1(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	public void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			int RGLength = in.readUBin(1);
			in.readBytes(3);
			
			while (in.remain() > 0) {
				int CFLid = in.readUBin(1);
				in.readByte();
				int Sectid = in.readCode();
				in.readByte();
				byte[] CFName = in.readBytes(8);
				String s = AFPConst.ebcdic2Ascii(CFName);
				byte[] CPName = in.readBytes(8);
				String s1 = AFPConst.ebcdic2Ascii(CPName);
				byte[] FCSName = in.readBytes(8);
				String s2 = AFPConst.ebcdic2Ascii(FCSName);

				if (RGLength == 30) {
					int CharRot = in.readCode(2);	
				}
			}
		} finally {
			in.close();
		}
	}
}
