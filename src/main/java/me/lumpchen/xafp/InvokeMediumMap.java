package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class InvokeMediumMap extends AFPObject {

	private byte[] MMPName;
	private List<Triplet> Triplets = new ArrayList<Triplet>();
	
	public InvokeMediumMap(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	public String getMediumMapName() {
		return AFPConst.ebcdic2Ascii(this.MMPName);
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			this.MMPName = in.readBytes(8);
			while (in.remain() > 0) {
				// read triplets
				Triplet triplet = Triplet.readTriple(in);
				this.Triplets.add(triplet);
			}
		} finally {
			in.close();
		}
	}
}
