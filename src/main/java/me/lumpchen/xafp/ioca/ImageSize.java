package me.lumpchen.xafp.ioca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class ImageSize {

	public static final int ID = 0x94;
	
	private int UNITBASE;
	private int HRESOL;
	private int VRESOL;
	private int HSIZE;
	private int VSIZE;
	
	public ImageSize() {
		
	}
	
	public int getRow() {
		return this.VSIZE;
	}
	
	public int getCol() {
		return this.HSIZE;
	}
	
	public void read(AFPInputStream in) throws IOException {
		int length = in.readUBin(1); // must =9
		
		this.UNITBASE = in.readCode();
		this.HRESOL = in.readUBin(2);
		this.VRESOL = in.readUBin(2);
		this.HSIZE = in.readUBin(2);
		this.VSIZE = in.readUBin(2);
	}
}
