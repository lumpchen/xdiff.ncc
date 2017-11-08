package me.lumpchen.xafp;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;

public class CodePageIndex extends AFPObject {
	
	/**
	 * CPI Repeating Group Length: 
	 * X'0A' Single-byte Code Page 
	 * X'0B' Double-byte Code Page 
	 * X'FE' Single-byte Code Page including Unicode scalar values 
	 * X'FF' Double-byte Code Page including Unicode scalar values
	 * */
	private int repeatingGroupLength;
	
	private List<Entry> entries;
	
	public CodePageIndex(StructureField structField) throws IOException {
		super(structField);
		this.entries = new ArrayList<Entry>();
	}
	
	public void parseEntries(int repeatingGroupLength) throws IOException {
		this.repeatingGroupLength = repeatingGroupLength;
		byte[] data = this.structField.getData();
		AFPInputStream in = new AFPInputStream(data);
		try {
			while (in.remain() > 0) {
				Entry entry = new Entry();
				entry.GCGID = in.readBytes(8);
				entry.PrtFlags = in.read();
				if (this.repeatingGroupLength == 0x0A || this.repeatingGroupLength == 0xFE) {
					entry.CodePoint = in.readUBin(1);
				} else {
					entry.CodePoint = in.readUBin(2);
				}
				
				this.entries.add(entry);
			}
		} finally {
			in.close();
		}
	}
	
	public Entry[] getEntries() {
		if (this.entries == null) {
			return new Entry[0];
		}
		return this.entries.toArray(new Entry[this.entries.size()]);
	}
	
	class Entry {
		public byte[] GCGID;
		public int PrtFlags;
		public int CodePoint;
		
		public String getGCGIDStr() {
			return AFPConst.ebcdic2Ascii(this.GCGID);
		}
	}
		
}
