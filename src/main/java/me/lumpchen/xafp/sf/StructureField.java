package me.lumpchen.xafp.sf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.AFPConst;
import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.StructureFieldReader.SFDataReader;
import me.lumpchen.xafp.sf.Identifier.Tag;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class StructureField {

	private Introducer introducer;
	private byte[] sfData;
	private byte[] nameBytes;
	private List<Triplet> triplets;
	
	public StructureField(Introducer introducer) {
		this.introducer = introducer;
	}
	
	@Override
	public String toString() {
		return this.introducer.getIdentifier().getTag().toString();
	}
	
	public byte[] getData() {
		return this.sfData;
	}
	
	public Tag getStructureTag() {
		return this.introducer.getIdentifier().getTag();
	}
	
	public String getNameStr() {
		return AFPConst.ebcdic2Ascii(this.nameBytes);
	}
	
	public byte[] getNameBytes() {
		return this.nameBytes;
	}
	
	public void setNameBytes(byte[] nameBytes) {
		this.nameBytes = nameBytes;
	}
	
	public void addTriplet(Triplet triplet) {
		if (this.triplets == null) {
			this.triplets = new ArrayList<Triplet>();
		}
		this.triplets.add(triplet);
	}
	
	public Triplet[] getAllTriplet() {
		if (this.triplets == null) {
			return new Triplet[0];
		}
		return this.triplets.toArray(new Triplet[this.triplets.size()]);
	}
	
	public void read(AFPInputStream in, SFDataReader dataReader) throws IOException {
		int hasRead = this.introducer.getLength();
		
		int remain = this.introducer.getStructureFieldLength() - hasRead;
		if (remain > 0) {
			this.sfData = in.readBytes(remain);
			dataReader.read(this);
		}
	}
	
	public int totalSize() {
		return this.introducer.getLength() + this.getData().length;
	}
}
