package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class PageDescriptor extends AFPObject {

	private int XpgBase;
	private int YpgBase;
	private int XpgUnits;
	private int YpgUnits;
	private int XpgSize;
	private int YpgSize;
	private List<Triplet> Triplets;
	
	public PageDescriptor(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
		this.Triplets = new ArrayList<Triplet>();
	}
	
	public void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			this.XpgBase = in.readCode();
			this.YpgBase = in.readCode();
			
			this.XpgUnits = in.readUBin(2);
			this.YpgUnits = in.readUBin(2);
			this.XpgSize = in.readUBin(3);
			this.YpgSize = in.readUBin(3);
			
			in.readBytes(3); // Reserved; must be binary zero
			
			while (in.remain() > 0) {
				// read triplets
				Triplet triplet = Triplet.readTriple(in);
				this.Triplets.add(triplet);
			}
		} finally {
			in.close();
		}
	}
	
	public double getXpgBase() {
		return AFPConst.toBaseScale(this.XpgBase);
	}

	public double getYpgBase() {
		return AFPConst.toBaseScale(this.YpgBase);
	}

	public int getXpgUnits() {
		return XpgUnits;
	}

	public int getYpgUnits() {
		return YpgUnits;
	}

	public int getXpgSize() {
		return XpgSize;
	}

	public int getYpgSize() {
		return YpgSize;
	}

	public List<Triplet> getTriplets() {
		return Triplets;
	}
}


