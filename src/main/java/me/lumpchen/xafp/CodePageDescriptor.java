package me.lumpchen.xafp;

import java.io.IOException;

import me.lumpchen.xafp.sf.StructureField;

public class CodePageDescriptor extends AFPObject {

	private String desc;
	private int GCGIDLen;
	private int numCdPts;
	private int GCSGID;
	private int CPGID;
	
	/**
	 * X'0000' No encoding scheme specified 
	 * X'0100' Single-byte, encoding not specified 
	 * X'0200' Double-byte, encoding not specified 
	 * X'2100' Single-byte IBM-PC Data 
	 * X'6100' Single-byte EBCDIC Presentation 
	 * X'6200' Double-byte EBCDIC Presentation 
	 * X'8200' Double-byte UCS Presentation
	 * */
	public static final int ENCODING_SCHEME_NO_SPECIFIED = 0x0000;
	public static final int ENCODING_SCHEME_SINGLE_BYTE_NO_SPECIFIED = 0x0100;
	public static final int ENCODING_SCHEME_DOUBLE_BYTE_NO_SPECIFIED = 0x0200;
	public static final int ENCODING_SCHEME_SINGLE_BYTE_IBM_PC = 0x2100;
	public static final int ENCODING_SCHEME_SINGLE_BYTE_EBCDIC = 0x6100;
	public static final int ENCODING_SCHEME_DOUBLE_BYTE_EBCDIC = 0x6200;
	public static final int ENCODING_SCHEME_DOUBLE_BYTE_UCS = 0x8200;
	private int encSchema;
	
	public CodePageDescriptor(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}

	public String getDesc() {
		return this.desc;
	}
	
	public int getEncodeSchema() {
		return this.encSchema;
	}
	
	public int getGCGIDLength() {
		return 8;
	}
	
	public int getNumCdPts() {
		return this.numCdPts;
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			this.desc = AFPConst.ebcdic2Ascii(in.readBytes(32));
			this.GCGIDLen = in.readUBin(2);
			if (this.GCGIDLen != 8) {
				throw new IOException("Invalid GID length (could only be 8): " + this.GCGIDLen);
			}
			this.numCdPts = (int) in.readUnsignedInt();
			this.GCSGID = in.readUBin(2);
			this.CPGID = in.readUBin(2);
			
			if (in.remain() > 0) {
				this.encSchema = in.readUBin(2);	
			}
			
		} finally {
			in.close();
		}
	}
}
