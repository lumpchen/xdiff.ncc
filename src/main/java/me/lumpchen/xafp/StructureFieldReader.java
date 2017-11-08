package me.lumpchen.xafp;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import me.lumpchen.xafp.sf.Identifier;
import me.lumpchen.xafp.sf.Introducer;
import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.Identifier.Tag;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class StructureFieldReader {
	
	public static StructureField read(AFPInputStream in) throws IOException {
		Introducer introducer = new Introducer();
		int read = introducer.read(in);

		if (read <= 0) {
			throw new IOException("Error occured when reading Structure Field Introducer.");
		}
		StructureField sf = new StructureField(introducer);
		
		// construct content reader by tag
		SFDataReader sfDataReader = createSFDataReader(sf.getStructureTag());
		
		sf.read(in, sfDataReader);
		return sf;
	}
	
	static SFDataReader createSFDataReader(Tag tag) {
		if (Identifier.isContainerTag(tag)) {
			return new ContainerSFDataReader();
		} else {
			return new ObjectSFDataReader();
		}
	}
	
	public static interface SFDataReader {
		public void read(StructureField sf) throws IOException;
	}
	
	static class ContainerSFDataReader implements SFDataReader {

		@Override
		public void read(StructureField sf) throws IOException {
			if (sf.getData() == null || sf.getData().length == 0) {
				return;
			}
			
			AFPInputStream in = new AFPInputStream(new ByteArrayInputStream(sf.getData()));
			try {
				byte[] name = in.readBytes(8);
				sf.setNameBytes(name);
				
				if (in.remain() <= 0) {
					return;
				}
				
				if (sf.getStructureTag() == Tag.BDT
						|| sf.getStructureTag() == Tag.BRS) {
					in.readBytes(2);
				}
				
				while (in.remain() > 0) {
					Triplet triplet = Triplet.readTriple(in);
					sf.addTriplet(triplet);
//					in.readBytes(in.remain());
				}
			} finally {
				in.close();
			}
		}
	}
	
	static class ObjectSFDataReader implements SFDataReader {

		@Override
		public void read(StructureField sf) throws IOException {
			if (sf.getData() == null || sf.getData().length == 0) {
				return;
			}
			
			AFPInputStream in = new AFPInputStream(new ByteArrayInputStream(sf.getData()));
			try {
				if (in.remain() <= 0) {
					return;
				}
				
				while (in.remain() > 0) {
//					Triplet triplet = Triplet.readTriple(in);
//					sf.addTriplet(triplet);
					in.readBytes(in.remain());
				}
			} finally {
				in.close();
			}
		}
	}
}
