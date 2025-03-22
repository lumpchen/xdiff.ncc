package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class MediumDescriptor extends AFPObject {

	private int XmBase;
	private int YmBase;
	private int XmUnits;
	private int YmUnits;
	private int XmSize;
	private int YmSize;
	private int MDDFlgs;
	private List<Triplet> triplets;
	
	public MediumDescriptor(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		this.XmBase = in.readCode();
		this.YmBase = in.readCode();
		this.XmUnits = in.readUBin(2);
		this.YmUnits = in.readUBin(2);
		this.XmSize = in.readUBin(3);
		this.YmSize = in.readUBin(3);
		this.MDDFlgs = in.read();
		
		while (in.remain() > 0) {
			Triplet triplet = Triplet.readTriple(in);
			if (this.triplets == null) {
				this.triplets = new ArrayList<Triplet>();
			}
			this.triplets.add(triplet);
		}
	}
}
