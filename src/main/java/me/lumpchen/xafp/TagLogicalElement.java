package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;
import me.lumpchen.xafp.sf.triplet.X01Triplet;
import me.lumpchen.xafp.sf.triplet.X02Triplet;
import me.lumpchen.xafp.sf.triplet.X36Triplet;
import me.lumpchen.xafp.sf.triplet.X80Triplet;

public class TagLogicalElement extends AFPObject {

	private List<Triplet> triplets;

	private int ccsid = X01Triplet.CCSID_EBCDIC;
	private byte[] attributeName;
	private byte[] attributeValue;
	private byte[] pageGroupName;
	private byte[] pageName;
	private byte[] tleName;
	private int sequenceNumber;
	private int levelNumber;
	
	public TagLogicalElement(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	public int getCcsid() {
		return ccsid;
	}

	public String getAttributeName() {
		if (this.attributeName == null) {
			return "";
		}
		if (this.ccsid == X01Triplet.CCSID_EBCDIC) {
			return AFPConst.ebcdic2Ascii(this.attributeName);
		} else {
			return AFPConst.toUincode16BEString(this.attributeName);			
		}
	}

	public String getAttributeValue() {
		if (this.attributeValue == null) {
			return "";
		}
		if (this.ccsid == X01Triplet.CCSID_EBCDIC) {
			return AFPConst.ebcdic2Ascii(this.attributeValue);
		} else {
			return AFPConst.toUincode16BEString(this.attributeValue);
		}
	}
	
	public String toString() {
		return this.getAttributeName() + ": " + this.getAttributeValue();
	}

	public byte[] getPageGroupName() {
		return pageGroupName;
	}

	public byte[] getPageName() {
		return pageName;
	}

	public byte[] getTleName() {
		return tleName;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public int getLevelNumber() {
		return levelNumber;
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
			
			if (this.triplets != null) {
				for (Triplet triplet : this.triplets) {
					if (triplet.getIdentifier() == X01Triplet.ID) {
						this.ccsid = ((X01Triplet) triplet).getCcsid();
					} else if (triplet.getIdentifier() == X02Triplet.ID) {
						int type = ((X02Triplet) triplet).getFQNType();
						if (type == X02Triplet.AttributeGID) {
							this.attributeName = ((X02Triplet) triplet).getFQName();
						} else if (type == X02Triplet.BeginPageGroupReference) {
							this.pageGroupName = ((X02Triplet) triplet).getFQName();
						} else if (type == X02Triplet.BeginPageReference) {
							this.pageName = ((X02Triplet) triplet).getFQName();
						} else if (type == X02Triplet.ProcessElementGID) {
							this.tleName = ((X02Triplet) triplet).getFQName();
						} 
					} else if (triplet.getIdentifier() == X36Triplet.ID) {
						this.attributeValue = ((X36Triplet) triplet).getAttVal();
					} else if (triplet.getIdentifier() == X80Triplet.ID) {
						this.sequenceNumber = (int) ((X80Triplet) triplet).getSeqNum();
						this.levelNumber = (int) ((X80Triplet) triplet).getLevNum();
					}
				}
			}
			
		} finally {
			in.close();
		}
	}
}
