package me.lumpchen.xafp;

import java.io.IOException;

import me.lumpchen.xafp.sf.StructureField;

public class GraphicsDataDescriptor extends AFPObject {

	public static final int DrawingOrderSubset = 0xF7;
	public static final int WindowSpecification = 0xF6;
	
	private int UBASE;
	private int XRESOL;
	private int YRESOL;
	private int IMXYRES;
	
	private int XLWIND;
	private int XRWIND;
	private int YBWIND;
	private int YTWIND;
	
	public GraphicsDataDescriptor(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	
	public int[] measureUnit(int[] p) {
		int h = this.YTWIND - this.YBWIND;
		int w = this.XRWIND - this.XLWIND;
		
		return null;
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			int CODE = in.readCode();
			if (CODE == DrawingOrderSubset) {
				int LENGTH = in.readUBin(1); 	// Length of following data
				in.readCode();					// Drawing order subset
				in.readBytes(2);
				
				int SUBLEV = in.readCode();		// Drawing order subset level 2.0
				int VERSION = in.readCode();	// Version 0
				in.readUBin(1);					// Length of following field
				in.readCode();					// Coordinate formats in data
			} else if (CODE == WindowSpecification) {
				int LENGTH = in.readUBin(1);	// Length of following data
				int FLAGS = in.read();
				in.read();
				int CFORMAT = in.readCode(); 	// Picture frame size coordinate format
				int UBASE = in.readCode(); 		// Unit Base for GPS
				int XRESOL = in.readCode(2);	// Number of Xg units/UBASE; must be the same as YRESOL
				int YRESOL = in.readCode(2);	// Number of Yg units/UBASE; must be the same as XRESOL
				int IMXYRES = in.readUBin(2);
				
				int XLWIND = in.readSBin(2);	// Xg coordinate for left edge of GPS window
				int XRWIND = in.readSBin(2);	// Xg coordinate for right edge of GPS window
				int YBWIND = in.readSBin(2);	// Yg coordinate for bottom edge of GPSwindow
				int YTWIND = in.readSBin(2);	// Yg coordinate for top edge of GPS window
				
			}
		} finally {
			in.close();
		}
	}
	
}
