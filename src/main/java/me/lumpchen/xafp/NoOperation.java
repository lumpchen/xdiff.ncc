package me.lumpchen.xafp;

import java.io.IOException;

import me.lumpchen.xafp.sf.StructureField;

public class NoOperation extends AFPObject {

	private byte[] UndfData;
	
	public NoOperation(StructureField structField) throws IOException {
		super(structField);
		this.UndfData = this.structField.getData();
	}

	public byte[] getData() {
		return this.UndfData;
	}
	
	public String getString() {
		if (this.UndfData == null || this.UndfData.length <= 0) {
			return "";
		}
		
		// how to determine unicode or EBCDIC ? 
		String s = AFPConst.ebcdic2Ascii(this.UndfData);
		return s;
	}
}
