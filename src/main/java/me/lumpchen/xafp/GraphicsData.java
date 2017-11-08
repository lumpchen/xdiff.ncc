package me.lumpchen.xafp;

import java.io.IOException;

import me.lumpchen.xafp.sf.StructureField;

public class GraphicsData extends AFPObject {
	
	private byte[] GOCAdat;
	
	public GraphicsData(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	private void parseData(byte[] data) throws IOException {
		this.GOCAdat = data;
	}

	public byte[] getData() {
		return this.GOCAdat;
	}
}
