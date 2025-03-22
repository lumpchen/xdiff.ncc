package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPConst;
import me.lumpchen.xafp.AFPInputStream;

public abstract class Triplet {
	
	protected int length;
	
	/**
	 * Identifies the triplet: 
	 * X'01' Coded Graphic Character Set Global Identifier
	 * 
	 * */
	protected int identifier;
	
	protected String name;
	
	protected int remain;
	
	Triplet() {
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getIdentifier() {
		return this.identifier;
	}
	
	public String toString() {
		return this.name + ": " + Integer.toHexString(this.identifier);
	}
	
	abstract protected void readContents(AFPInputStream in) throws IOException;
	
	protected void read(AFPInputStream in) throws IOException {
		this.remain = this.length - 2;
		this.readContents(in);
		if (this.remain != 0) {
			throw new IOException("Triplet reading error: " + this.toString());
		}
	}
	
	public static Triplet readTriple(AFPInputStream in) throws IOException {
		int length = in.readUBin(1);
		if (length < 3 || length > 254) {
			throw new IOException("Invalid triplet length (3-254): " + length);
		}
		
		int identifier = in.readUBin(1);
		
		Triplet triplet;
		switch (identifier) {
		case X01Triplet.ID:
			triplet = new X01Triplet();
			break;
		case X02Triplet.ID:
			triplet = new X02Triplet();
			break;
		case X04Triplet.ID:
			triplet = new X04Triplet();
			break;
		case X10Triplet.ID:
			triplet = new X10Triplet();
			break;
		case X1DTriplet.ID:
			triplet = new X1DTriplet();
			break;
		case X1FTriplet.ID:
			triplet = new X1FTriplet();
			break;
		case X21Triplet.ID:
			triplet = new X21Triplet();
			break;
		case X24Triplet.ID:
			triplet = new X24Triplet();
			break;
		case X25Triplet.ID:
			triplet = new X25Triplet();
			break;
		case X26Triplet.ID:
			triplet = new X26Triplet();
			break;
		case X36Triplet.ID:
			triplet = new X36Triplet();
			break;
		case X4BTriplet.ID:
			triplet = new X4BTriplet();
			break;
		case X4CTriplet.ID:
			triplet = new X4CTriplet();
			break;
		case X5ATriplet.ID:
			triplet = new X5ATriplet();
			break;
		case X62Triplet.ID:
			triplet = new X62Triplet();
			break;
		case X63Triplet.ID:
			triplet = new X63Triplet();
			break;
		case X43Triplet.ID:
			triplet = new X43Triplet();
			break;
		case X8BTriplet.ID:
			triplet = new X8BTriplet();
			break;
		case X68Triplet.ID:
			triplet = new X68Triplet();
			break;
			
		case 0x27:
		case 0x64:
		case 0x73:
			triplet = new RetiredTriplet(identifier);
			break;
		default:
			throw new IllegalArgumentException("Unknown Triplet id: X'" + AFPConst.bytesToHex((byte) identifier) + "'");
		}
		
		triplet.setLength(length);
		triplet.read(in);
		
		return triplet;
	}
	
}
