package me.lumpchen.xafp;

import java.io.IOException;

import me.lumpchen.xafp.sf.StructureField;

public class GraphicsDataDescriptor extends AFPObject {

	public static final int DrawingOrderSubset = 0xF7;
	public static final int WindowSpecification = 0xF6;
	
	private int CFORMAT;
	private int UBASE;
	private int XRESOL;
	private int YRESOL;
	private int IMXYRES;
	
	private int XLWIND;
	private int XRWIND;
	private int YBWIND;
	private int YTWIND;
	
	private float scaleRatio;
	
	public GraphicsDataDescriptor(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	public float left() {
		return this.XLWIND * this.scaleRatio * 72;
	}
	
	public float right() {
		return this.XRWIND * this.scaleRatio * 72;
	}
	
	public float top() {
		return this.YTWIND * this.scaleRatio * 72;
	}
	
	public float bottom() {
		return this.YBWIND * this.scaleRatio * 72;
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			// In obsolete AFP Data Stream Reference, S544-3202, allowed the Drawing Order Subset Parameter to be optional.
			int CODE = in.readCode();
			boolean hasDrawingOrderSubset = false;
			if (CODE == DrawingOrderSubset) {
				hasDrawingOrderSubset = true;
				int LENGTH = in.readUBin(1); 	// Length of following data
				in.readCode();					// Drawing order subset
				in.readBytes(2);
				
				int SUBLEV = in.readCode();		// Drawing order subset level 2.0
				int VERSION = in.readCode();	// Version 0
				in.readUBin(1);					// Length of following field
				in.readCode();					// Coordinate formats in data
			} 
			
			if (hasDrawingOrderSubset) {
				CODE = in.readCode();	
			}
			if (CODE == WindowSpecification) {
				int LENGTH = in.readUBin(1);	// Length of following data
				int FLAGS = in.read();
				in.read();
				this.CFORMAT = in.readCode(); 	// Picture frame size coordinate format
				this.UBASE = in.readCode(); 		// Unit Base for GPS
				this.XRESOL = in.readCode(2);	// Number of Xg units/UBASE; must be the same as YRESOL
				this.YRESOL = in.readCode(2);	// Number of Yg units/UBASE; must be the same as XRESOL
				this.IMXYRES = in.readUBin(2);
				
				this.XLWIND = in.readSBin(2);	// Xg coordinate for left edge of GPS window
				this.XRWIND = in.readSBin(2);	// Xg coordinate for right edge of GPS window
				this.YBWIND = in.readSBin(2);	// Yg coordinate for bottom edge of GPSwindow
				this.YTWIND = in.readSBin(2);	// Yg coordinate for top edge of GPS window
				
				this.scaleRatio = (float) (1.0 / this.XRESOL * AFPConst.toBaseScale(this.UBASE));
			}
		} finally {
			in.close();
		}
	}
	
}
