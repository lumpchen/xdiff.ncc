package me.lumpchen.xdiff.document.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.lumpchen.xdiff.PageDiffResult.DiffContent;
import me.lumpchen.xdiff.document.ImageSet.ImageLob;
import me.lumpchen.xdiff.document.PageThread;
import me.lumpchen.xdiff.document.TextContent;
import me.lumpchen.xdiff.document.TextThread;
import me.lumpchen.xdiff.document.TextThread.TextLob;
import me.lumpchen.xdiff.document.compare.name.fraser.neil.plaintext.diff_match_patch.Diff;
import me.lumpchen.xdiff.document.compare.name.fraser.neil.plaintext.diff_match_patch.Operation;

public class TextComparator extends ContentComparator {
	
	public static final String TAG_INSERT = "#INSERT#";
	public static final String TAG_DELETE = "#DELETE#";
	
	private float basePageHeight;
	private float testPageHeight;
	
	private Map<StringHolder, TextLob> insertMap = new HashMap<StringHolder, TextLob>();
	private Map<StringHolder, TextLob> deleteMap = new HashMap<StringHolder, TextLob>();
	
	public TextComparator(CompareSetting setting) {
		super(setting);
	}
	
	public DiffContent[] compare(TextThread baseTextThread, TextThread testTextThread) {
		this.basePageHeight = baseTextThread.getPageHeight();
		this.testPageHeight = testTextThread.getPageHeight();
		
		String baseText = baseTextThread.getNormalizeString();
		String testText = testTextThread.getNormalizeString();
		LinkedList<Diff> diffs = TextDiffUtil.diffText(baseText, testText);
		if (diffs == null || diffs.isEmpty()) {
			return new DiffContent[0];
		}

		List<DiffContent> result = new ArrayList<DiffContent>();
		int ibase = 0, itest = 0;
		for (Diff diff : diffs) {
			if (diff.operation == Operation.INSERT) {
				if (diff.text.length() == 1 && diff.text.charAt(0) == 0x20) {
					itest += 1;
					continue;
				}
				int from = itest;
				TextLob[] lobs = testTextThread.getTextLob(from, diff.text.length());
				itest += diff.text.length();
				
				List<TextLob> lobList = mergeLobs(lobs);
				for (TextLob lob : lobList) {
					String text = lob.getText();
					if (!text.trim().isEmpty()) {
						this.insertMap.put(new StringHolder(text.trim()), lob);
					}
				}
			} else if (diff.operation == Operation.DELETE) {
				if (diff.text.length() == 1 && diff.text.charAt(0) == 0x20) {
					ibase += 1;
					continue;
				}
				int from = ibase;
				TextLob[] lobs = baseTextThread.getTextLob(from, diff.text.length());
				ibase += diff.text.length();
				
				List<TextLob> lobList = mergeLobs(lobs);
				for (TextLob lob : lobList) {
					String text = lob.getText();
					if (!text.trim().isEmpty()) {
						this.deleteMap.put(new StringHolder(text.trim()), lob);
					}
				}
			} else {
				int baseBegin = 0;
				int testBegin = 0;
				int walk = 0;
				while (true) {
					if (walk >= diff.text.length()) {
						ibase += baseBegin;
						itest += testBegin;
						break;
					}
					int baseLen = baseTextThread.lenToContentEnd(baseBegin + ibase);
					int testLen = testTextThread.lenToContentEnd(testBegin + itest);
					
					int slot = baseLen <= testLen ? baseLen : testLen;
					if (slot > diff.text.length() - walk) {
						slot = diff.text.length() - walk;
					}
					
					String text = diff.text.substring(walk, walk + slot);
					if (text != null && !text.trim().isEmpty()) {
						TextLob baseLob = baseTextThread.getTextLob(baseBegin + ibase, slot)[0];
						TextLob testLob = testTextThread.getTextLob(testBegin + itest, slot)[0];
						DiffContent diffContent = new DiffContent(DiffContent.Category.Text);
						diffContent.putAttr(DiffContent.Key.Attr_Text, true, text, text);
						if (!this.compare(baseLob, testLob, diffContent)) {
							result.add(diffContent);
						}
					}
					
					baseBegin += slot;
					testBegin += slot;
					walk += slot;
				}
			}
		}
		
		List<TextLob> baseFlyContents = baseTextThread.getFlyContents();
		List<TextLob> testFlyContents = testTextThread.getFlyContents();
		this.compareFlyContents(baseFlyContents, testFlyContents, result);
		
		this.match(result);
		return result.toArray(new DiffContent[result.size()]);
	}
	
	public void match(List<DiffContent> result) {
		Iterator<Entry<StringHolder, TextLob>> it = this.insertMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<StringHolder, TextLob> entry = it.next();
			String key = entry.getKey().getText();
			
			Entry<StringHolder, TextLob> findEntry = this.findEntry(key, this.deleteMap);
			if (findEntry != null) {
				DiffContent diffContent = new DiffContent(DiffContent.Category.Text);
				diffContent.putAttr(DiffContent.Key.Attr_Text, true, key, key);
				if (!this.compare(findEntry.getValue(), entry.getValue(), diffContent)) {
					result.add(diffContent);
				}
				it.remove();
				this.deleteMap.remove(findEntry.getKey());
			}
		}
		
		if (this.insertMap.size() > 0) {
			it = this.insertMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<StringHolder, TextLob> entry = it.next();
				String text = entry.getKey().getText();
				TextLob lob = entry.getValue();
				DiffContent diffContent = new DiffContent(DiffContent.Category.Text);
				diffContent.putAttr(DiffContent.Key.Attr_Text, false, TAG_INSERT, text);
				this.setAttributes(lob, diffContent, TAG_INSERT);
				diffContent.setBBox(null, lob.getBoundingBox());
				result.add(diffContent);
			}
		}
		
		if (this.deleteMap.size() > 0) {
			it = this.deleteMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<StringHolder, TextLob> entry = it.next();
				String text = entry.getKey().getText();
				TextLob lob = entry.getValue();
				DiffContent diffContent = new DiffContent(DiffContent.Category.Text);
				diffContent.putAttr(DiffContent.Key.Attr_Text, false, text, TAG_DELETE);
				this.setAttributes(lob, diffContent, TAG_DELETE);
				diffContent.setBBox(lob.getBoundingBox(), null);
				result.add(diffContent);
			}
		}
	}
	
	private Entry<StringHolder, TextLob> findEntry(String findText, Map<StringHolder, TextLob> dstMap) {
		Iterator<Entry<StringHolder, TextLob>> it = dstMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<StringHolder, TextLob> entry = it.next();
			String key = entry.getKey().getText();
			
			if (findText.equals(key)) {
				return entry;
			}
		}
		return null;
	}
	
	private void setAttributes(TextLob lob, DiffContent entry, String tag) {
		TextContent textContent = lob.getContent();
		if (textContent == null) {
			return;
		}
		String val = textContent.getFontName();
		if ("#DELETE#".equals(tag)) {
			entry.putAttr(DiffContent.Key.Attr_Font, false, val, null);	
		} else {
			entry.putAttr(DiffContent.Key.Attr_Font, false, null, val);			
		}
		
		Float size = textContent.getFontSize();
		if ("#DELETE#".equals(tag)) {
			entry.putAttr(DiffContent.Key.Attr_Font_size, false, size == null ? null : size.toString(), null);
		} else {
			entry.putAttr(DiffContent.Key.Attr_Font_size, false, null, size == null ? null : size.toString());		
		}
		
		val = textContent.getNonStrokingColorspace();
		if ("#DELETE#".equals(tag)) {
			entry.putAttr(DiffContent.Key.Attr_Fill_Colorspace, false, val, null);
		} else {
			entry.putAttr(DiffContent.Key.Attr_Fill_Colorspace, false, null, val);			
		}
		
		val = textContent.getNonStrokingColorValue();
		if ("#DELETE#".equals(tag)) {
			entry.putAttr(DiffContent.Key.Attr_Fill_Color, false, val, null);
		} else {
			entry.putAttr(DiffContent.Key.Attr_Fill_Color, false, null, val);			
		}
		
		if (this.setting.enableTextPositionCompare) {
			double x = lob.getBoundingBox().getX();
			if ("#DELETE#".equals(tag)) {
				entry.putAttr(DiffContent.Key.Attr_Pos_X, false, roundM(x), null);
			} else {
				entry.putAttr(DiffContent.Key.Attr_Pos_X, false, null, roundM(x));			
			}
			
			double y = this.basePageHeight - lob.getBoundingBox().getY();
			if ("#DELETE#".equals(tag)) {
				entry.putAttr(DiffContent.Key.Attr_Pos_Y, false, roundM(y), null);
			} else {
				entry.putAttr(DiffContent.Key.Attr_Pos_Y, false, null, roundM(y));		
			}
		}
	}
	
	private void compareFlyContents(List<TextLob> baseFlyContents, List<TextLob> testFlyContents, List<DiffContent> result) {
		for (TextLob baseLob : baseFlyContents) {
			TextLob testLob = this.findTextLob(baseLob, testFlyContents);
			if (testLob != null) {
				DiffContent diffContent = new DiffContent(DiffContent.Category.Text);
				diffContent.putAttr(DiffContent.Key.Attr_Text, true, baseLob.getText(), testLob.getText()); // TODO, toUnicodeHex?
				if (!this.compare(baseLob, testLob, diffContent)) {
					result.add(diffContent);
				}
				testFlyContents.remove(testLob);
			} else {
				this.insertMap.put(new StringHolder(baseLob.getText()), baseLob);
			}
		}
		if (testFlyContents.size() > 0) {
			for (TextLob testLob : testFlyContents) {
				this.deleteMap.put(new StringHolder(testLob.getText()), testLob);
			}
		}

	}
	
	private TextLob findTextLob(TextLob baseContent, List<TextLob> testContentList) {
		for (TextLob test : testContentList) {
			if (baseContent.getBoundingBox() == null || test.getBoundingBox() == null) {
				continue;
			}
			boolean equals = compare(baseContent.getBoundingBox(), test.getBoundingBox(), 
					this.setting.toleranceOfHorPosition, this.setting.toleranceOfVerPosition, 
					this.setting.toleranceOfRectWidth, this.setting.toleranceOfRectHeight);
			if (equals) {
				return test;
			}
		}
		
		for (TextLob test : testContentList) {
			if (baseContent.getBoundingBox() == null || test.getBoundingBox() == null) {
				continue;
			}
			if (baseContent.getBoundingBox().intersects(test.getBoundingBox())) {
				return test;
			}
		}
		return null;
	}
	
	private boolean compare(TextLob baseLob, TextLob testLob, DiffContent entry) {
		boolean result = true;
		
		TextContent textContent_1 = baseLob.getContent();
		TextContent textContent_2 = testLob.getContent();
		
		String val_1 = textContent_1 == null ? null : textContent_1.getFontName();
		String val_2 = textContent_2 == null ? null : textContent_2.getFontName();
		boolean equals = this.compareFontName(removeFontNameSuffix(val_1), removeFontNameSuffix(val_2));
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Font, equals, val_1, val_2);
		
		Float size_1 = textContent_1 == null ? null : textContent_1.getFontSize();
		Float size_2 = textContent_2 == null ? null : textContent_2.getFontSize();
		equals = compare(size_1, size_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Font_size, equals, size_1 == null ? null : size_1.toString(), 
				size_2 == null ? null : size_2.toString());
		
		val_1 = textContent_1 == null ? null : textContent_1.getNonStrokingColorspace();
		val_2 = textContent_2 == null ? null : textContent_2.getNonStrokingColorspace();
		equals = compare(val_1, val_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Fill_Colorspace, equals, val_1, val_2);
		
		float[] color_1 = textContent_1 == null ? null : textContent_1.getNonStrokingColor();
		float[] color_2 = textContent_2 == null ? null : textContent_2.getNonStrokingColor();
		val_1 = textContent_1 == null ? null : textContent_1.getNonStrokingColorValue();
		val_2 = textContent_2 == null ? null : textContent_2.getNonStrokingColorValue();
		equals = compare(color_1, color_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Fill_Color, equals, val_1, val_2);
		
		if (this.setting.enableTextPositionCompare) {
			double x_1 = baseLob.getBoundingBox().getX();
			double x_2 = testLob.getBoundingBox().getX();
			equals = compare(x_1, x_2, this.setting.toleranceOfHorPosition); 
			result &= equals;
			entry.putAttr(DiffContent.Key.Attr_Pos_X, equals, roundM(x_1), roundM(x_2));
			
			double y_1 = this.basePageHeight - baseLob.getBoundingBox().getY();
			double y_2 = this.testPageHeight - testLob.getBoundingBox().getY();
			equals = compare(y_1, y_2, this.setting.toleranceOfVerPosition);
			result &= equals;
			entry.putAttr(DiffContent.Key.Attr_Pos_Y, equals, roundM(y_1), roundM(y_2));
		}
		
		entry.setBBox(baseLob.getBoundingBox(), testLob.getBoundingBox());
		
		return result;
	}
	
	private static List<TextLob> mergeLobs(TextLob[] lobs) {
		List<TextLob> lobList = new ArrayList<TextLob>();
		TextLob last = null;
		for (int i = 0; i < lobs.length; i++) {
			if (i == 0) {
				last = lobs[i];
				lobList.add(last);
				continue;
			}
			if (!last.mergeLob(lobs[i])) {
				last = lobs[i];
				lobList.add(lobs[i]);
			}
		}
		return lobList;
	}
	
	@Override
	public DiffContent[] compare(PageThread basePageThread, PageThread testPageThread) {
		DiffContent[] diffs = this.compare(basePageThread.getTextThread(), testPageThread.getTextThread());
		return diffs;
	}
	
	static class StringHolder {
		private String text;
		
		public StringHolder(String text) {
			this.text = text;
		}
		
		public String getText() {
			return this.text;
		}
	}
}
 