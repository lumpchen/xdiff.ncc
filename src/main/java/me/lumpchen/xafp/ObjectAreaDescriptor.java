package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;
import me.lumpchen.xafp.sf.triplet.X43Triplet;
import me.lumpchen.xafp.sf.triplet.X4BTriplet;
import me.lumpchen.xafp.sf.triplet.X4CTriplet;

public class ObjectAreaDescriptor extends AFPObject {

	private List<Triplet> triplets;
	
	private int desPosID;
	
	private int XoaBase;
	private int YoaBase;
	private int XoaUnits;
	private int YoaUnits;
	
	private int XoaSize;
	private int YoaSize;
	
	private float width;
	private float height;
	
	public ObjectAreaDescriptor(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	public int getXoaSize() {
		return XoaSize;
	}

	public int getYoaSize() {
		return YoaSize;
	}

	public float getWdith() {
		return this.width;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
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
		
		for (Triplet triplet : this.triplets) {
			if (triplet instanceof X43Triplet) {
				X43Triplet x43 = (X43Triplet) triplet;
				this.desPosID = x43.getDesPosID();
			} else if (triplet instanceof X4BTriplet) {
				X4BTriplet x4b = (X4BTriplet) triplet;
				this.XoaBase = x4b.getXoaBase();
				this.YoaBase = x4b.getYoaBase();
				this.XoaUnits = x4b.getXoaUnits();
				this.YoaUnits = x4b.getYoaUnits();
			} else if (triplet instanceof X4CTriplet) {
				X4CTriplet x4c = (X4CTriplet) triplet;
				this.XoaSize = x4c.getXoaSize();
				this.YoaSize = x4c.getYoaSize();
			}
		}
		
		this.width = (float) this.XoaSize / this.XoaUnits;
		this.height = (float) this.YoaSize / this.YoaUnits;
	}
}
