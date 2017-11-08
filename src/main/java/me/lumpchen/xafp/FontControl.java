package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class FontControl extends AFPObject {

	/**
	 * X'05': Laser Matrix N-bit Wide 
	 * X'1E': CID Keyed font (Type 0) 
	 * X'1F': PFB (Type 1)
	 * */
	public enum PatTech {
		Laser_Matrix_N_bit_Wide(0x05), CID_Keyed_font_Type0(0x1E), PFB_Type1(0x1F);
		
		private int id;
		private PatTech(int id) {
			this.id = id;
		}
		
		public static final PatTech instance(int id) {
			switch (id) {
			case 0x05:
				return Laser_Matrix_N_bit_Wide;
			case 0x1E:
				return CID_Keyed_font_Type0;
			case 0x01F:
				return PFB_Type1;
			default:
				throw new IllegalArgumentException("Invalid font pattern id: " + id);
			}
		}
		
		public int getID() {
			return this.id;
		}
	};
	private PatTech patTech;
	
	private int FntFlags;
	
	/**
	 * X'00' Base is fixed at 10 inches 
	 * X'02' Base is relative
	 * */
	public enum MeasureUnit {
		Fixed, Relative
	};
	private MeasureUnit xUnitBase;
	private MeasureUnit yUnitBase;
	
	/**
	 * X'0960' 240 pels per inch
	 * X'0BB8' 300 pels per inch
	 * X'03E8' 1000 units per em
	 * */
	private int xUnitsPerUnitBase;
	private int yUnitsPerUnitBase;
	
	private int MaxBoxWd;
	private int MaxBoxHt;
	private int FNORGLen;
	private int FNIRGLen;
	
	/**
	 * Pattern Data Alignment Code:
	 * X'00' 1-Byte Alignment
	 * X'02' 4-Byte Alignment
	 * X'03' 8-Byte Alignment
	 * */
	private int patternDataAlignment;
	private int rasterPatternDataCount;
	
	private int FNPRGLen;
	private int FNMRGLen;
	
	private List<Triplet> triplets;

	private int ResXUBase;

	private int ResYUBase;

	private int xShapeResolution;

	private int yShapeResolution;

	private long OPatDCnt;

	private int FNNRGLen;

	private long FNNDCnt;

	private int FNNMapCnt;
	
	public FontControl(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	public int getFNORGLen() {
		return this.FNORGLen;
	}
	
	public int getFNPRGLen() {
		return this.FNPRGLen;
	}
	
	public int getFNIRGLen() {
		return this.FNIRGLen;
	}
	
	public int getFNMRGLen() {
		return this.FNMRGLen;
	}
	
	public int getFNNMapCnt() {
		return this.FNNMapCnt;
	}
	
	public int getPatternDataAlignment() {
		return this.patternDataAlignment;
	}

	public int getRasterPatternDataCount() {
		return this.rasterPatternDataCount;
	}
	
	public PatTech getPatTech() {
		return this.patTech;
	}
	
	public MeasureUnit getXMeasureUnit() {
		return this.xUnitBase;
	}
	
	public MeasureUnit getYMeasureUnit() {
		return this.yUnitBase;
	}
	
	public int getXUnitsPerUnitBase() {
		return this.xUnitsPerUnitBase;
	}
	
	public int getYUnitsPerUnitBase() {
		return this.yUnitsPerUnitBase;
	}
	
	public int getXShapeResolution() {
		return this.xShapeResolution;
	}
	
	public int getYShapeResolution() {
		return this.yShapeResolution;
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			in.readUBin(1);
			this.patTech = PatTech.instance(in.readCode());
			
			in.readUBin(1);
			
			this.FntFlags = in.readCode();
			
			this.xUnitBase = in.readCode() == 0x00 ? MeasureUnit.Fixed : MeasureUnit.Relative;
			this.yUnitBase = in.readCode() == 0x00 ? MeasureUnit.Fixed : MeasureUnit.Relative;
			int XftUnits = in.readUBin(2);
			int YftUnits = in.readUBin(2);
			if (this.xUnitBase == MeasureUnit.Fixed) {
				this.xUnitsPerUnitBase = XftUnits / 10;
				this.yUnitsPerUnitBase = YftUnits / 10;
			} else {
				this.xUnitsPerUnitBase = XftUnits;
				this.yUnitsPerUnitBase = YftUnits;
			}
			
			this.MaxBoxWd = in.readUBin(2);
			this.MaxBoxHt = in.readUBin(2);
			this.FNORGLen = in.readUBin(1);
			this.FNIRGLen = in.readUBin(1);
			
			int PatAlign = in.readCode();
			if (PatAlign == 0) {
				this.patternDataAlignment = 1;
			} else if (PatAlign == 1) {
				this.patternDataAlignment = 4;
			} else if (PatAlign == 2) {
				this.patternDataAlignment = 8;
			}
			this.rasterPatternDataCount = in.readUBin(3);
			this.FNPRGLen = in.readUBin(1);
			this.FNMRGLen = in.readUBin(1);
			
			if (in.remain() > 0) {
				this.ResXUBase = in.readCode();
			}
			if (in.remain() > 0) {
				this.ResYUBase = in.readCode();
			}
			
			if (in.remain() > 0) {
				int XfrUnits = in.readUBin(2);
				this.xShapeResolution = XfrUnits / 10;
			}
			
			if (in.remain() > 0) {
				int YfrUnits = in.readUBin(2);
				this.yShapeResolution = YfrUnits / 10;
			}
			
			if (in.remain() > 0) {
				this.OPatDCnt = in.readUnsignedInt();
			}
			
			if (in.remain() > 0) {
				in.readBytes(3);
			}
			
			if (in.remain() > 0) {
				this.FNNRGLen = in.readUBin(1);
			}
			
			if (in.remain() > 0) {
				this.FNNDCnt = in.readUnsignedInt();
			}
			
			if (in.remain() > 0) {
				this.FNNMapCnt = in.readUBin(2);
			}
			
			while (in.remain() > 0) {
				Triplet triplet = Triplet.readTriple(in);
				if (this.triplets == null) {
					this.triplets = new ArrayList<Triplet>();
				}
				this.triplets.add(triplet);
			}
			
		} finally {
			in.close();
		}
	}
}
 