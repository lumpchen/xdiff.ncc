package me.lumpchen.xdiff.afp;

import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.NoOperation;
import me.lumpchen.xafp.PrintFile;

public class AFPCommentComparator {

	private PrintFile basePrintFile;
	private PrintFile testPrintFile;
	
	private List<NopCompareEntry> entries;
	
	public AFPCommentComparator(PrintFile basePrintFile, PrintFile testPrintFile) {
		this.basePrintFile = basePrintFile;
		this.testPrintFile = testPrintFile;
		
		this.entries = new ArrayList<NopCompareEntry>();		
	}
	
	public boolean compare() {
		List<String> base = getNopString(this.basePrintFile);
		List<String> test = getNopString(this.testPrintFile);
		boolean equals = true;
		if (base.size() != test.size()) {
			equals &= false;
		}
		
		int max = base.size() > test.size() ? base.size() : test.size();
		for (int i = 0; i < max; i++) {
			NopCompareEntry entry = new NopCompareEntry();
			if (i < base.size()) {
				entry.baseItem = base.get(i);
			} else {
				entry.baseItem = "";
			}
			
			if (i < test.size()) {
				entry.testItem = test.get(i);
			} else {
				entry.testItem = "";
			}
			
			if (!(entry.baseItem.equals(entry.testItem))) {
				entry.equals = false;
				equals &= false;
			}
			this.entries.add(entry);
		}
		return equals;
	}
	
	public List<NopCompareEntry> getCompareEntries() {
		return this.entries;
	}
	
	private static List<String> getNopString(PrintFile printFile) {
		List<NoOperation> nopList = printFile.getAllNOPs();
		if (nopList == null || nopList.isEmpty()) {
			return new ArrayList<String>(0);
		}

		List<String> ret = new ArrayList<String>(nopList.size());
		for (NoOperation nop : nopList) {
			ret.add(nop.getString());
		}
		
		return ret;
	}
	
	public static class NopCompareEntry {
		public String baseItem;
		public String testItem;
		public boolean equals = true;
	}
}
