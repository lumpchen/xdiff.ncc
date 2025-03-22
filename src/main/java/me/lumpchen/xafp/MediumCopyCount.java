package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;

public class MediumCopyCount extends AFPObject {

	private List<Copy> group = new ArrayList<Copy>();
	
	public MediumCopyCount(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		
		while (in.remain() > 0) {
			Copy copy = new Copy();
			copy.Startnum = in.readUBin(2);
			copy.Stopnum = in.readUBin(2);
			in.read();
			copy.MMCid = in.readCode();
			this.group.add(copy);
		}
	}
	
	public static class Copy {
		private int Startnum;
		private int Stopnum;
		private int MMCid;
	}
}
