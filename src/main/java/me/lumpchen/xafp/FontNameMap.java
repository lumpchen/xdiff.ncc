package me.lumpchen.xafp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import me.lumpchen.xafp.sf.StructureField;

public class FontNameMap extends AFPObject {

	/**
	 * FNM Repeating Group Length: 
	 *  X'00' No Raster Data  
	 *  X'08' Raster Data
	 * */
	private ByteArrayOutputStream buffer;
	private int IBMFormat;

	/**
	 * character ID format: 
	 * X'03': Font-specific ASCII character name, used with Type 1 PFB fonts 
	 * X'05': CMAP binary code point, used with CID-keyed fonts
	 * */
	public static final int Font_specific_ASCII_character_name = 0x03;
	public static final int CMAP_binary_code_point = 0x05;
	private int TechnologyFormat;
	
	private Map<String, Long> second;
	private Map<Long, String> third;
	
	public FontNameMap(StructureField structField) {
		super(structField);
		this.appendData(super.getStructureData());
	}
	
	public Map<String, String> getNameMap() {
		Map<String, String> nameMap = new HashMap<String, String>(this.second.size());
		
		Iterator<Entry<String, Long>> it = this.second.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Long> next = it.next();
			nameMap.put(next.getKey(), this.third.get(next.getValue()));
		}
		return nameMap;
	}
	
	public int getIBMFormat() {
		return IBMFormat;
	}

	public void setIBMFormat(int iBMFormat) {
		IBMFormat = iBMFormat;
	}

	public int getTechnologyFormat() {
		return TechnologyFormat;
	}

	public void setTechnologyFormat(int technologyFormat) {
		TechnologyFormat = technologyFormat;
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
	
	public void parseData(int repeatGroupLen) throws IOException {
		AFPInputStream in = new AFPInputStream(this.buffer.toByteArray());
		
		try {
			long read = 0;
			
			this.IBMFormat = in.readCode();
			read += 1;
			this.TechnologyFormat = in.readCode();
			read += 1;
			
			this.second = new HashMap<String, Long>(repeatGroupLen);
			if (in.remain() > 0) {
				for (int i = 0; i < repeatGroupLen; i++) {
					byte[] GCGID = in.readBytes(8);
					read += 8;
					
					long TSOffset = in.readUnsignedInt();
					read += 4;
					
					this.second.put(AFPConst.ebcdic2Ascii(GCGID), TSOffset);
				}
			}
			
			this.third = new HashMap<Long, String>(repeatGroupLen);
			if (in.remain() > 0) {
				for (int i = 0; i < repeatGroupLen; i++) {
					long offset = read;
					int tsiLen = in.readUBin(1);
					read += 1;
					byte[] data = in.readBytes(tsiLen - 1);
					read += (tsiLen - 1);
					
					this.third.put(offset, new String(data));
				}
			}
			
		} finally {
			in.close();
		}
	}
}
