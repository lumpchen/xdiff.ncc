package me.lumpchen.xafp;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;
import me.lumpchen.xafp.sf.triplet.X01Triplet;
import me.lumpchen.xafp.sf.triplet.X02Triplet;
import me.lumpchen.xafp.sf.triplet.X10Triplet;
import me.lumpchen.xafp.sf.triplet.X8BTriplet;

public class MapDataResource extends AFPObject {

	private List<Triplet[]> Triplets;
	private List<Attribute> attrList;

	public MapDataResource(StructureField structField) throws IOException {
		super(structField);
		this.Triplets = new ArrayList<Triplet[]>();
		this.parseData(this.structField.getData());
	}
	
	public Attribute getResourceAttributes(int LID) {
		for (Attribute attr : this.attrList) {
			if (attr.intenalResRef == LID) {
				return attr;
			}
		}
		return null;
	}

	public void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			while (in.remain() > 0) {
				int RGLength = in.readUBin(2);
				byte[] repeatBytes = in.readBytes(RGLength - 2);
				AFPInputStream repeatStream = new AFPInputStream(repeatBytes);
				List<Triplet> group = new ArrayList<Triplet>();
				while (repeatStream.remain() > 0) {
					// read triplets
					Triplet triplet = Triplet.readTriple(repeatStream);
					group.add(triplet);
				}
				this.Triplets.add(group.toArray(new Triplet[group.size()]));
			}

			this.attrList = new ArrayList<Attribute>(this.Triplets.size());
			for (Triplet[] triplets : this.Triplets) {
				Attribute attr = new Attribute(triplets);
				this.attrList.add(attr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			in.close();
		}
	}
	
	public static class Attribute {
		public int ccsid;
		public int codePageID;
		public String extResRef;
		public int intenalResRef;
		public int objectClassification;
		
		public String fontTech;
		public int fontSize;
		public String encEnv;
		public String encID;
		
		public Attribute(Triplet[] group) {
			for (Triplet triplet : group) {
				if (triplet instanceof X01Triplet) {
					X01Triplet x01 = (X01Triplet) triplet;
					if (x01.getGcsgid() == 0) {
						this.ccsid = x01.getCcsid();
					} else {
						this.codePageID = x01.getCpgid();						
					}
				} else if (triplet instanceof X02Triplet) {
					X02Triplet x02 = (X02Triplet) triplet;
					int FQNType = x02.getFQNType();
					switch ((byte) FQNType) {
					case (byte) X02Triplet.DataObjectExternalResourceReference:
						if (this.ccsid == X01Triplet.CCSID_UTF16) {
							this.extResRef = new String(x02.getFQName(), Charset.forName("UTF-16BE"));
						}
						break;
					case (byte) X02Triplet.DataObjectInternalResourceReference:
						this.intenalResRef = (int) (x02.getFQName()[0] & 0xFF);
						break;
					}
				} else if (triplet instanceof X10Triplet) {
					this.objectClassification = ((X10Triplet) triplet).getObjClass();
				} else if (triplet instanceof X8BTriplet) {
					X8BTriplet x8b = (X8BTriplet) triplet;
					this.fontTech = x8b.getFontTech();
					this.fontSize = x8b.getVerticalFontSize();
					this.encEnv = x8b.getEncEnv();
					this.encID = x8b.getEncID();
				}
			}
		}
	}

}