package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X1FTriplet extends Triplet {

	public static final int ID = 0x1F;
	
	/**
	 * Specifies character stroke thickness: 
	 * X'00' Not specified 
	 * X'01' Ultra-light 
	 * X'02' Extra-light 
	 * X'03' Light
	 * X'04' Semi-light 
	 * X'05' Medium (normal) 
	 * X'06' Semi-bold 
	 * X'07' Bold 
	 * X'08' Extra-bold 
	 * X'09' Ultra-bold
	 * */
	private int FtWtClass;
	
	/**
	 * Specifies character width-to-height ratio: 
	 * X'00' Not specified 
	 * X'01' Ultra-condensed 
	 * X'02' Extra-condensed 
	 * X'03' Condensed 
	 * X'04' Semi-condensed 
	 * X'05' Medium (normal) 
	 * X'06' Semi-expanded 
	 * X'07' Expanded 
	 * X'08' Extra-expanded 
	 * X'09' Ultra-expanded
	 * */
	private int FtWdClass;
	
	/**
	 * Specifies vertical font size in 1440ths of an inch (20ths of a point) or in world coordinate values
	 * */
	private int FtHeight;
	
	/**
	 * Specifies horizontal font size in 1440ths of an inch (20ths of a point) or in world coordinate values.
	 * */
	private int FtWidth;
	
	private int FtDsFlags;
	
	private int FtUsFlags;
	
	public X1FTriplet() {
		super();
		this.identifier = ID;
		this.name = "Font Descriptor Specification";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		this.FtWtClass = in.readCode();
		remain -= 1;
		
		this.FtWdClass = in.readCode();
		remain -= 1;
		
		this.FtHeight = in.readUBin(2);
		remain -= 2;
		
		this.FtWidth = in.readUBin(2);
		remain -= 2;
		
		this.FtDsFlags = in.read();
		remain -= 1;
		
		if (remain > 0) {
			in.readBytes(18 - 9 + 1);
			remain -= (18 - 9 + 1);
		}

		if (remain > 0) {
			this.FtUsFlags = in.read();
			remain -= 1;
		}
		
		if (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}

	public int getFtWtClass() {
		return FtWtClass;
	}

	public void setFtWtClass(int ftWtClass) {
		FtWtClass = ftWtClass;
	}

	public int getFtWdClass() {
		return FtWdClass;
	}

	public void setFtWdClass(int ftWdClass) {
		FtWdClass = ftWdClass;
	}

	public int getFtHeight() {
		return FtHeight;
	}

	public void setFtHeight(int ftHeight) {
		FtHeight = ftHeight;
	}

	public int getFtWidth() {
		return FtWidth;
	}

	public void setFtWidth(int ftWidth) {
		FtWidth = ftWidth;
	}

	public int getFtDsFlags() {
		return FtDsFlags;
	}

	public void setFtDsFlags(int ftDsFlags) {
		FtDsFlags = ftDsFlags;
	}

	public int getFtUsFlags() {
		return FtUsFlags;
	}

	public void setFtUsFlags(int ftUsFlags) {
		FtUsFlags = ftUsFlags;
	}

}
