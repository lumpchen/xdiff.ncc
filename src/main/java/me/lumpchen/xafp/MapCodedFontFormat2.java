package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;
import me.lumpchen.xafp.sf.triplet.X02Triplet;
import me.lumpchen.xafp.sf.triplet.X1FTriplet;
import me.lumpchen.xafp.sf.triplet.X24Triplet;

public class MapCodedFontFormat2 extends AFPObject {

	private List<Triplet[]> Triplets;
	
	public MapCodedFontFormat2(StructureField structField) throws IOException {
		super(structField);
		this.Triplets = new ArrayList<Triplet[]>();
		this.parseData(this.structField.getData());
	}
	
	public Attribute getResourceAttributes(int LID) {
		for (Triplet[] group : this.Triplets) {
			for (Triplet triplet : group) {
				if (triplet instanceof X24Triplet) {
					int localID = ((X24Triplet) triplet).getResLID();
					if (LID == localID) {
						return new Attribute(group);
					}
				}
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
			
			
			/*int repeat = data.length / RGLength;
			
			List<Triplet> all = new ArrayList<Triplet>();
			while (in.remain() > 0) {
				// read triplets
				Triplet triplet = Triplet.readTriple(in);
				all.add(triplet);
			}
			
			int group = all.size() / repeat;
			int index = 0;
			Triplet[] attrs = null;
			while (true) {
				if (all.isEmpty()) {
					break;
				}
				if (index == 0) {
					attrs = new Triplet[group];
				}
				Triplet next = all.remove(0);
				attrs[index] = next;
				if (index == group - 1) {
					index = 0;
					this.Triplets.add(attrs);
				} else {
					index++;
				}
			}*/
			
		} finally {
			in.close();
		}
	}
	
	public static class Attribute {
		public int localID;
		
		public String codePageName;
		public String characterSetName;
		public float fontSize;
		
		public Attribute(Triplet[] group) {
			for (Triplet triplet : group) {
				if (triplet instanceof X24Triplet) {
					this.localID = ((X24Triplet) triplet).getResLID();
				} else if (triplet instanceof X02Triplet) {
					X02Triplet x02 = (X02Triplet) triplet;
					int FQNType = x02.getFQNType();
					switch ((byte) FQNType) {
					case (byte) X02Triplet.CodePageNameReference:
						this.codePageName = AFPConst.ebcdic2Ascii(x02.getFQName());
						break;
					case (byte) X02Triplet.FontCharacterSetNameReference:
						this.characterSetName = AFPConst.ebcdic2Ascii(x02.getFQName());
						break;
					}
				} else if (triplet instanceof X1FTriplet) {
					this.fontSize = AFPConst.unit2Point(((X1FTriplet) triplet).getFtHeight());
				}
			}
		}
	}
	
}
