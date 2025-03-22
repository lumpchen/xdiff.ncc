package me.lumpchen.xafp.sf;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class Introducer {

	// Introducer length;
	private int length;
	
	// Total length of the structured field including the length of the introducer
	private int structureFieldLength;
	
	// A three-byte code that uniquely identifies the structured field
	private Identifier identifier;
	
	/**
	 * Used to indicate whether an extension, segmentation, or padding is in use
	 * Bit 0 ExtFlag B'0', B'1'
			B'0' No SFI extension exists B'1' SFI extension is present
	  Bit 1 Reserved; must be zero
	  Bit 2 SegFlag B'0', B'1'
			B'0' Data is not segmented B'1' Data is segmented
	  Bit 3 Reserved; must be zero
	  Bit 4 PadFlag B'0', B'1'
			B'0' No padding data exists B'1' Padding data is present
	  Bits 5�C7 Reserved; must be zero
	 * */
	private int flag;

	// The following optional extension appears only if bit 0 of FlagByte is B'1':
	// Length of the extension including the length of ExtLength itself
	private int extLength;
	private byte[] extData;
	
	/** B'0' Data is not segmented 
	 *  B'1' Data is segmented
	 *  */
	private int segFlag;
	
	private int sequenceNumber;
	
	/**	B'0' No padding data exists 
	 * B'1' Padding data is present
	 * */
	private int padFlag;
	
	public Introducer() {
	}
	
	public Introducer(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public int getStructureFieldLength() {
		return structureFieldLength;
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}
	
	public int getFlag() {
		return flag;
	}
	
	public int getExtLength() {
		return extLength;
	}
	
	public byte[] getExtData() {
		return extData;
	}
	
	public boolean isDataSegmented() {
		return this.segFlag == 1;
	}
	
	public boolean hasPaddingData() {
		return this.padFlag == 1;
	}
	
	public int read(AFPInputStream in) throws IOException {
		
		int count = 0;
		this.structureFieldLength = in.readUBin(2);
		count += 2;
		
		byte[] code = in.readBytes(3);
		this.identifier = Identifier.instance(code);
		count += 3;
		
		this.flag = in.readUnsignedByte();
		count += 1;
		
		this.sequenceNumber = in.readUBin(2); // reserved 2 bytes
		count += 2;
		
		if (AFPInputStream.checkBit(this.flag, 0) == 1) {
			this.extLength = in.readUBin(1);
			count += 2;
			
			if (this.extLength > 1) {
				this.extData = in.readBytes(this.extLength - 1);
				count += this.extLength - 1;
			}
		}
		
		this.segFlag = AFPInputStream.checkBit(this.flag, 2);
		this.padFlag = AFPInputStream.checkBit(this.flag, 4);
		
		if (this.padFlag == 1) {
			/**
			 * If padding is indicated, the length of the padding is specified in the following manner: 
			 * v For 1 or 2 bytes of padding, the length is specified in the last padding byte. 
			 * v For 256 to 32,759 bytes of padding, the length is specified in the last three bytes of the padding data. 
			 * 	     The last byte must be X'00' and the two preceding bytes specify the padding length.
			 * v For 3 to 255 bytes of padding, the length can be specified by either method. 
			 * 
			 * When padding is indicated: v The structured field length value specifies the total length of the structured field, 
			 * including the padding data. v The padding length value specifies the total length of the padding data, 
			 * including the padding length byte(s).
			 */
		}
		
		this.length = count;
		return count;
	}
}


