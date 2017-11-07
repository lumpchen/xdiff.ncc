package me.lumpchen.xdiff.afp;

import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.PrintFile;
import me.lumpchen.xafp.TagLogicalElement;

public class TLECompartor {
	
	private PrintFile basePrintFile;
	private PrintFile testPrintFile;
	
	private List<TLECompareEntry> entries;
	public TLECompartor(PrintFile basePrintFile, PrintFile testPrintFile) {
		this.basePrintFile = basePrintFile;
		this.testPrintFile = testPrintFile;
		
		this.entries = new ArrayList<TLECompareEntry>();
	}
	
	public boolean compare() {
		List<String[]> base = getTLEString(this.basePrintFile);
		List<String[]> test = getTLEString(this.testPrintFile);
		boolean equals = true;
		if (base.size() != test.size()) {
			equals &= false;
		}
		
		int max = base.size() > test.size() ? base.size() : test.size();
		for (int i = 0; i < max; i++) {
			TLECompareEntry entry = new TLECompareEntry();
			if (i < base.size()) {
				entry.baseItem = base.get(i);
			} else {
				entry.baseItem = new String[0];
			}
			
			if (i < test.size()) {
				entry.testItem = test.get(i);
			} else {
				entry.testItem = new String[0];
			}
			
			if (entry.baseItem.length != entry.testItem.length) {
				entry.equals = false;
				equals &= false;
			}
			for (int j = 0; j < entry.baseItem.length; j++) {
				if (!entry.baseItem[j].equals(entry.testItem[j])) {
					entry.equals = false;
					equals &= false;
				}
			}
			this.entries.add(entry);
		}
		return equals;
	}
	
	public List<TLECompareEntry> getCompareEntries() {
		return this.entries;
	}
	
	private static List<String[]> getTLEString(PrintFile printFile) {
		List<TagLogicalElement> tleList = printFile.getAllTLEs();
		if (tleList == null || tleList.isEmpty()) {
			return new ArrayList<String[]>(0);
		}

		List<String[]> ret = new ArrayList<String[]>(tleList.size());
		for (TagLogicalElement tle : tleList) {
			String[] item = new String[2];
			item[0] = tle.getAttributeName();
			item[1] = tle.getAttributeValue();
			ret.add(item);
		}
		return ret;
	}
	
	public static class TLECompareEntry {
		public String[] baseItem;
		public String[] testItem;
		public boolean equals = true;
	}
}
