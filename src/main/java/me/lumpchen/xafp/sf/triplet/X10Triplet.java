package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

/**
 * Object Classification Triplet
 * */
public class X10Triplet extends Triplet {

	public static final int ID = 0x10;
	
	public static final int CLASS_TIME_INVARIANT     = 0x01;
	public static final int CLASS_TIME_VARIANT       = 0x10;
	public static final int CLASS_EXECUTABLE         = 0x20;
	public static final int CLASS_SETUP_FILE         = 0x30;
	public static final int CLASS_SECONDARY_RESOURCE = 0x40;
	public static final int CLASS_DATA_OBJECT_FONT   = 0x41;
	private int ObjClass;
	
	public static final int STRFL_OBJ_CONTAINER     = 0xdc;
	public static final int STRFL_OBJ_CONTAINER_OEG = 0xfc;
	public static final int STRFL_MDR_ENTRY         = 0xa8;
	private int StrucFlgs;
	
	public static final int CID_EPS                      = 13;  // X'0D'
	public static final int CID_TIFF                     = 14;  // X'0E'
	public static final int CID_COM_SETUP                = 15;  // X'0F'
	public static final int CID_TAPE_LABEL_SETUP         = 16;  // X'10'
	public static final int CID_DIB_WINDOWS              = 17;  // X'11'
	public static final int CID_DIB_OS2                  = 18;  // X'12'
	public static final int CID_PCX                      = 19;  // X'13'
	public static final int CID_COLOR_MAPPING_TABLE      = 20;  // X'14'
	public static final int CID_GIF                      = 22;  // X'16'
	public static final int CID_JFIF                     = 23;  // X'17'
	public static final int CID_ANASTAK_CONTROL_RECORD   = 24;  // X'18'
	public static final int CID_PDF_SINGLEPAGE_OBJECT    = 25;  // X'19'
	public static final int CID_PDF_RESOURCE_OBJECT      = 26;  // X'1A'
	public static final int CID_PCL_PAGE_OBJECT          = 34;  // X'22'
	public static final int CID_RESIDENT_COLOR_PROFILE   = 46;  // X'2E'
	public static final int CID_IOCA_FS45_TILE_RESOURCE  = 47;  // X'2F'
	public static final int CID_EPS_WITH_TRANSPARENCY    = 48;  // X'30'
	public static final int CID_PDF_WITH_TRANSPARENCY    = 49;  // X'31'
	public static final int CID_TRUETYPE_FONT            = 51;  // X'33'
	public static final int CID_TRUETYPE_FONT_COLLECTION = 53;  // X'35'
	public static final int CID_RESOURCE_ACCESS_TABLE    = 54;  // X'36
	private byte[] RegObjId;
	
	private byte[] ObjTpName;
	private byte[] ObjLev;
	private byte[] CompName;
	
	
	public X10Triplet() {
		super();
		this.identifier = ID;
		this.name = "Object Classification";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		int read = 2;
		
		in.read(); // reserverd
		read += 1;
		
		this.ObjClass = in.readCode();
		read += 1;
		
		in.readBytes(2); // reserved
		read += 2;
		this.StrucFlgs = in.readSBin(2);
		read += 2;
		this.RegObjId = in.readBytes(23 - 8 + 1);
		read += (23 - 8 + 1);
		
		if (this.length - read > 0 && in.remain() > 0) {
			this.ObjTpName = in.readBytes(55 - 24 + 1);
			read += (55 - 24 + 1);
		}
		if (this.length - read > 0 && in.remain() > 0) {
			this.ObjLev = in.readBytes(63 - 56 + 1);
			read += (63 - 56 + 1);
		}
		if (this.length - read > 0 && in.remain() > 0) {
			this.CompName = in.readBytes(95 - 64 + 1);
			read += (95 - 64 + 1);
		}
		this.remain -= (read - 2);
	}

	public int getObjClass() {
		return ObjClass;
	}

	public int getStrucFlgs() {
		return StrucFlgs;
	}

	public byte[] getRegObjId() {
		return RegObjId;
	}

	public byte[] getObjTpName() {
		return ObjTpName;
	}

	public byte[] getObjLev() {
		return ObjLev;
	}

	public byte[] getCompName() {
		return CompName;
	}

}
