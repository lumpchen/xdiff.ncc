package me.lumpchen.xafp;

import java.io.IOException;

import me.lumpchen.xafp.sf.StructureField;

public class ImagePictureData extends AFPObject {

	private byte[] IOCAdat;
	
	public ImagePictureData(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	public byte[] getIOCAdata() {
		return this.IOCAdat;
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			if (in.remain() > 0) {
				this.IOCAdat = in.readBytes(in.remain());
			}
		} finally {
			in.close();
		}
	}
}