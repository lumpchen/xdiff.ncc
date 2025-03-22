package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;

public class PagePosition extends AFPObject {
	
	public static final int Constant = 0x01;
	private List<Position> group;

	public PagePosition(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		
		try {
			int constant = in.readCode();
			
			this.group = new ArrayList<Position>();
			while (in.remain() > 0) {
				int RGLength = in.readUBin(1);
				byte[] repeatBytes = in.readBytes(RGLength - 1);
				AFPInputStream repeatStream = new AFPInputStream(repeatBytes);
				
				Position pos = new Position();
				pos.XmOset = repeatStream.readSBin(3);
				pos.YmOset = repeatStream.readSBin(3);
				pos.PGorient = repeatStream.readCode(2);
				pos.SHside = repeatStream.readCode();
				if (repeatStream.remain() > 0) {
					pos.PgFlgs = repeatStream.read();
				}
				if (repeatStream.remain() > 0) {
					pos.PMCid = repeatStream.readCode();
				}
				
				this.group.add(pos);
				repeatStream.close();
			}
		} finally {
			in.close();
		}
	}
	
	public static class Position {
		public int XmOset;
		public int YmOset;
		public int PGorient;
		public int SHside;
		
		public int PgFlgs;
		public int PMCid;
	}
}
