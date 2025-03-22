package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class MapPageOverlay extends AFPObject {

	private List<Triplet[]> Triplets;
	
	public MapPageOverlay(StructureField structField) throws IOException {
		super(structField);
		this.Triplets = new ArrayList<Triplet[]>();
		this.parseData(this.structField.getData());
	}
	
	public void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			
		} finally {
			in.close();
		}
	}
	
}
