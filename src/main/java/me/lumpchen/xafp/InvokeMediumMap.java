package me.lumpchen.xafp;

import java.io.IOException;

import me.lumpchen.xafp.sf.StructureField;

public class InvokeMediumMap extends AFPObject {

	public InvokeMediumMap(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
	}
}
