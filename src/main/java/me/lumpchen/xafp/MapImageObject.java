package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;
import me.lumpchen.xafp.sf.triplet.X04Triplet;
import me.lumpchen.xafp.sf.triplet.X04Triplet.MapOption;

public class MapImageObject extends AFPObject {

	private MapOption mapOption;
	private List<Triplet> triplets;
	
	public MapImageObject(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	public MapOption getMapOption() {
		return this.mapOption;
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			int remain = in.available();
			int group = remain / 5;
			
			for (int i = 0; i < group; i++) {
				int RGLength = in.readUBin(2);
				
				byte[] bytes = in.readBytes(RGLength - 2);
				AFPInputStream triStream = new AFPInputStream(bytes);
				while (triStream.remain() > 0) {
					Triplet triplet = Triplet.readTriple(triStream);
					if (this.triplets == null) {
						this.triplets = new ArrayList<Triplet>();
					}
					this.triplets.add(triplet);
					
					if (triplet instanceof X04Triplet) {
						this.mapOption = ((X04Triplet) triplet).getMapValue();
					}
				}
			}
		} finally {
			in.close();
		}
	}
}