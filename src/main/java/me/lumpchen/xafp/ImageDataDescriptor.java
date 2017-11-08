package me.lumpchen.xafp;

import java.io.IOException;

import me.lumpchen.xafp.sf.StructureField;

public class ImageDataDescriptor extends AFPObject {
	
	private int UNITBASE;
	private int XRESOL;
	private int YRESOL;
	private int XSIZE;
	private int YSIZE;
	private byte[] SDF;
	
	private float width = 0;
	private float height = 0;
	
	public ImageDataDescriptor(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			if (in.remain() > 0) {
				this.UNITBASE = in.readCode();
				this.XRESOL = in.readUBin(2);
				this.YRESOL = in.readUBin(2);
				this.XSIZE = in.readUBin(2);
				this.YSIZE = in.readUBin(2);
				if (in.remain() > 0) {
					this.SDF = in.readBytes(in.remain());
				}
				
				this.width = (float) this.XSIZE / this.XRESOL;
				this.height = (float) this.YSIZE / this.YRESOL;
			}
		} finally {
			in.close();
		}
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}