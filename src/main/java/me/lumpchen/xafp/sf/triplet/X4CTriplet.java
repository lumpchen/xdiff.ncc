package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X4CTriplet extends Triplet {

	public static final int ID = 0x4C;
	
	/**
	 * X'02' Object Area Size 
	 * All others Reserved
	 * */
	private int SizeType;
	
	private int XoaSize;
	private int YoaSize;
	
	public X4CTriplet() {
		super();
		this.identifier = ID;
		this.name = "Object Area Size";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		this.SizeType = in.readCode();
		remain -= 1;
		this.XoaSize = in.readUBin(3);
		remain -= 3;
		this.YoaSize = in.readUBin(3);
		remain -= 3;
	}

	public int getSizeType() {
		return SizeType;
	}

	public int getXoaSize() {
		return XoaSize;
	}

	public int getYoaSize() {
		return YoaSize;
	}

}

