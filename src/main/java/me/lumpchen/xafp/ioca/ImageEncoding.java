package me.lumpchen.xafp.ioca;

import java.io.IOException;

import me.lumpchen.xafp.AFPConst;
import me.lumpchen.xafp.AFPInputStream;

public class ImageEncoding {

	public static final int ID = 0x95;

	/**
	 * Compression algorithm:
			X'01' IBM MMR Modified Modified READ
			X'03' No compression
			X'06' RL4 (Run-Length 4)
			X'08' ABIC (Bilevel Q-Coder)
			X'0A' Concatenated ABIC
			X'0B' Color compression used by OS/2 Image Support, part number 49F4608
			X'0D' TIFF LZW
			X'20' Solid Fill Rectangle
			X'80' G3 MH Modified Huffman (ITU-TSS T.4 Group 3 one-dimensional coding standard for facsimile)
			X'81' G3 MR Modified READ (ITU-TSS T.4 Group 3 two-dimensional coding option for facsimile)
			X'82' G4 MMR Modified Modified READ (ITU-TSS T.6 Group 4 two-dimensional coding standard for facsimile)
			X'83' JPEG algorithms (See the External Algorithm Specification parameter for detail)
	 * */
	public enum CompressionAlgrithm {
		G4, JPEG, None
	};
	private CompressionAlgrithm compressionAlg;
	
	/**
	 * X'01' RIDIC (Recording Image Data Inline Coding)
		X'03' Bottom-to-Top
		X'04' Unpadded RIDIC
		X'FE' See the External Algorithm Specification parameter for details
	 * */
	public enum RecordingAlgorithm {
		RIDIC, Bottom_to_Top, Unpadded_RIDIC, External
	}
	private RecordingAlgorithm recordingnAlg;
	
	/**
	 * X'00' Left-to-right
		X'01' Right-to-left
	 * */
	public enum BitOrder {
		Left_to_right, Right_to_left
	}
	private BitOrder bitOrder = BitOrder.Left_to_right;
	
	public ImageEncoding() {
	}
	
	public RecordingAlgorithm getRecordingAlgorithm() {
		return this.recordingnAlg;
	}
	
	public CompressionAlgrithm getAlgorhtim() {
		return this.compressionAlg;
	}
	
	public BitOrder getBitOrder() {
		return this.bitOrder;
	}
	
	private void setRecordingAlg(int algID) {
		switch (algID) {
		case 0x01:
			this.recordingnAlg = RecordingAlgorithm.RIDIC;
			break;
		case 0x03:
			this.recordingnAlg = RecordingAlgorithm.Bottom_to_Top;
			break;
		case 0x04:
			this.recordingnAlg = RecordingAlgorithm.Unpadded_RIDIC;
			break;
		case 0xFE:
			this.recordingnAlg = RecordingAlgorithm.External;
			break;
		}
	}

	private void setCompessionAlg(int recid) {
		switch (recid) {
		case 0x82:
			this.compressionAlg = CompressionAlgrithm.G4;
			break;
		case 0x83:
			this.compressionAlg = CompressionAlgrithm.JPEG;
			break;
		case 0x03:
			this.compressionAlg = CompressionAlgrithm.None;
			break;
		default:
			throw new java.lang.IllegalArgumentException("Unspported compression algoritm: " + AFPConst.bytesToHex((byte) (recid & 0xFF)));
		}
	}
	
	private void setBitOrder(int order) {
		switch (order) {
		case 0x01:
			this.bitOrder = BitOrder.Left_to_right;
			break;
		case 0x02:
			this.bitOrder = BitOrder.Right_to_left;
			break;
		}
	}
	
	public void read(AFPInputStream in) throws IOException {
		int length = in.readUBin(1); // 2 or 3
		
		int COMPRID = in.readCode();
		this.setCompessionAlg(COMPRID);
		length--;
		
		int RECID = in.readUBin(1);
		this.setRecordingAlg(RECID);
		length--;
		
		if (length > 0) {
			int BITORDR = in.readUBin(1);
			this.setBitOrder(BITORDR);
			length--;
		}
	}
}
