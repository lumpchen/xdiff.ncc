package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

/**
 * The Data-Object Font Descriptor triplet is used to specify the parameters 
 * needed to render a data-object font. Data-object fonts are non-FOCA font 
 * resources, such as TrueType and OpenType fonts. An MDR structured field is 
 * used to map a data-object font as a resource.
 * */

public class X8BTriplet extends Triplet {

	public static final int ID = 0x8B;
	
	private int DOFtFlgs;
	
	// X'20' TrueType/ OpenType
	private String fontTech;
	public static final String TrueType_OpenType = "TrueType/ OpenType";
	
	private int verticalFontSize;
	private int horizontalScaleFactor;
	
	/**
	 * Clockwise character rotation in degrees 
	 * X'0000' 0 degrees 
	 * X'2D00' 90 degrees 
	 * X'5A00' 180 degrees 
	 * X'8700' 270 degrees
	 * */
	private int rotationDegree;
	
	public static final String EncodingEnvironmentMicrosoft = "Microsoft";
	private String encEnv;
	
	public static final String EncodingUnicode = "Unicode";
	private String encID;
	
	public X8BTriplet() {
		super();
		this.identifier = ID;
		this.name = "Data-Object Font Descriptor";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		this.DOFtFlgs = in.readByte();
		remain -= 1;
		int FontTech = in.readCode();
		if (FontTech == 0x20) {
			this.fontTech = TrueType_OpenType;
		}
		remain -= 1;
		this.verticalFontSize = in.readUBin(2);
		remain -= 2;
		this.horizontalScaleFactor = in.readUBin(2);
		remain -= 2;
		int CharRot = in.readCode(2);
		if (CharRot == 0x2D00) {
			this.rotationDegree = 90;
		} else if (CharRot == 0x5A00) {
			this.rotationDegree = 180;
		} else if (CharRot == 0x8700) {
			this.rotationDegree = 270;;
		} else {
			this.rotationDegree = 0;
		}
		remain -= 2;
		int EncEnv = in.readCode(2);
		if (EncEnv == 0x03) {
			this.encEnv = EncodingEnvironmentMicrosoft;
		}
		remain -= 2;
		int EncID = in.readCode(2);
		if (EncID == 0x01) {
			this.encID = EncodingUnicode;
		}
		remain -= 2;
		in.readBytes(2);
		remain -= 2;
		
	}

	public static int getId() {
		return ID;
	}

	public int getDOFtFlgs() {
		return DOFtFlgs;
	}

	public String getFontTech() {
		return fontTech;
	}

	public int getVerticalFontSize() {
		return verticalFontSize;
	}

	public int getHorizontalScaleFactor() {
		return horizontalScaleFactor;
	}

	public int getRotationDegree() {
		return rotationDegree;
	}

	public String getEncEnv() {
		return encEnv;
	}

	public String getEncID() {
		return encID;
	}

}
