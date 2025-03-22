package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;

public class MediumModificationControl extends AFPObject {

	private int MMCid;
	private List<Integer> controlKeywords = new ArrayList<Integer>();

	public MediumModificationControl(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}

	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);

		this.MMCid = in.readCode();
		in.readCode();

		while (in.remain() > 0) {
			this.controlKeywords.add(in.read());
		}
	}
}
