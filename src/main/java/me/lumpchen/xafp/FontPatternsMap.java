package me.lumpchen.xafp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;

public class FontPatternsMap extends AFPObject {

	private ByteArrayOutputStream buffer;
	
	private List<Pattern> patternList;
	
	public static class Pattern {
		public int charBoxWd;
		public int charBoxHt;
		public long patDOset;
	}
	
	public FontPatternsMap(StructureField structField) {
		super(structField);
		this.appendData(super.getStructureData());
	}
	
	public void appendData(byte[] data) {
		try {
			if (this.buffer == null) {
				this.buffer = new ByteArrayOutputStream();
				this.buffer.write(data);
			} else {
				this.buffer.write(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Pattern getPattern(int index) {
		if (index < 0 || index >= this.patternList.size()) {
			throw new java.lang.IllegalArgumentException("Out of range: " + index);
		}
		return this.patternList.get(index);
	}
	
	public int getPatterCount() {
		return this.patternList.size();
	}
	
	public void parseData(int repeatGroupLen) throws IOException {
		byte[] data = this.buffer.toByteArray();
		AFPInputStream in = new AFPInputStream(data);
		
		try {
			if (data.length % repeatGroupLen != 0) {
				throw new IOException("Not a valid length of repeat group length: " + data.length + "/" + repeatGroupLen);
			}
			int count = data.length / repeatGroupLen;
			this.patternList = new ArrayList<Pattern>(count);
			for (int i = 0; i < count; i++) {
				Pattern pattern = new Pattern();
				pattern.charBoxWd = in.readUBin(2);
				pattern.charBoxHt = in.readUBin(2);
				pattern.patDOset = in.readUnsignedInt();
				this.patternList.add(pattern);
			}
		} finally {
			in.close();
		}
	}
}
