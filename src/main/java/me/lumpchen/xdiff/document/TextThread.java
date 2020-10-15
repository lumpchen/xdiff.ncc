package me.lumpchen.xdiff.document;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lumpchen.xdiff.PDocDiffResult.PageInfo;
import me.lumpchen.xdiff.document.compare.CompareSetting;
import me.lumpchen.xdiff.document.compare.ContentComparator;

public class TextThread {
	
	private StringBuilder pageText;
	private List<TextSpan> textSpanList;
	private int nextBegin = 0;
	
	private PageInfo pageInfo;
	private CompareSetting setting;
	
	public TextThread(PageThread pageThread) {
		this.pageText = new StringBuilder();
		this.textSpanList = new ArrayList<TextSpan>();
		this.setting = pageThread.getCompareSetting();
		this.pageInfo = pageThread.getPageInfo();
	}
	
	@Override
	public String toString() {
		return this.getText();
	}
	
	public String getText() {
		return this.pageText.toString();
	}
	
	public String getNormalizeString() {
		String s = this.pageText.toString();
		if (this.setting.acceptanceDifferenceCharMap.size() > 0) {
			for (Character[] pair : this.setting.acceptanceDifferenceCharMap) {
				s = s.replace(pair[0], pair[1]);
			}
		}
		return s;
	}
	
	public float getPageHeight() {
		if (this.pageInfo == null) {
			return 0;
		}
		return this.pageInfo.getHeight();
	}
	
	public void addTextContent(List<TextContent> contentList) {
		if (contentList.isEmpty()) {
			return;
		}
		
		if (this.setting.disableLineBreaking) {
			for (TextContent col : contentList) {
				this.addTextSpan(col);	
			}
		} else {
			Map<Integer, Line> lineMap = new HashMap<Integer, Line>();
			List<Integer> lines = new ArrayList<Integer>();
			for (TextContent content : contentList) {
//				int mY = (int) Math.round(content.getOrigin().getY() * 1000);
				int mY = (int) Math.round(content.getOrigin().getY());
				
				if (lineMap.containsKey(mY)) {
					Line line = lineMap.get(mY);
					line.addCol(content);
				} else {
					Line newLine = new Line(mY, this.setting.scaleOfSpaceingBetweenWords);
					newLine.addCol(content);
					lines.add(mY);
					lineMap.put(mY, newLine);
				}
			}
			
			Collections.sort(lines);
			Collections.reverse(lines);
			
			for (int lineV : lines) {
				Line line = lineMap.get(lineV);
				line.sortCols(true);
				
				for (TextContent col : line.getCols()) {
					this.addTextSpan(col);	
				}
			}
		}
	}
	
	public static class Line {
		private int baseline;
		private List<TextContent> colSpans;
		private float scaleOfSpaceingBetweenWords;
		
		public Line(int baseline, float scaleOfSpaceingBetweenWords) {
			this.baseline = baseline;
			this.colSpans = new ArrayList<TextContent>();
			
			this.scaleOfSpaceingBetweenWords = scaleOfSpaceingBetweenWords;
		}
		
		public int getBaseline() {
			return baseline;
		}
		
		public void addCol(TextContent col) {
			this.colSpans.add(col);
		}
		
		public List<TextContent> getCols() {
			return this.colSpans;
		}
		
		public void sortCols(boolean merge) {
			if (this.colSpans.size() <= 1) {
				return;
			}
			Collections.sort(this.colSpans, new Comparator<TextContent>() {
				@Override
				public int compare(TextContent o1, TextContent o2) {
					int x1 = (int) Math.round(o1.getOrigin().getX() * 1000);
					int x2 = (int) Math.round(o2.getOrigin().getX() * 1000);

					if (x1 < x2) {
						return -1;
					} else if (x2 > x1) {
						return 1;
					}
					return 0;
				}
			});
			
			if (merge) {
				this.colSpans = this.mergeCols(this.colSpans);
			}
		}
		
		private List<TextContent> mergeCols(List<TextContent> contents) {
			if (contents.size() <= 1) {
				return contents;
			}
			List<TextContent> mergedContents = new ArrayList<TextContent>();
			for (int i = 0; i < contents.size(); i++) {
				if (mergedContents.isEmpty()) {
					mergedContents.add(contents.get(i));
					continue;
				}
				
				TextContent last = mergedContents.get(mergedContents.size() - 1);
				TextContent next = contents.get(i);

				if (next == null || last == null || last.getOutlineRect() == null) {
					continue;
				}
				double distance = next.getOrigin().getX() - last.getX() - last.getOutlineRect().getWidth();
				
				double spaceW = last.getWCharWidth();
				if (last.getGraphicsStateDesc().equals(next.getGraphicsStateDesc())) {
					if (this.isConnect(last, next) || distance <= spaceW * this.scaleOfSpaceingBetweenWords) {
						last.merge(next);
						continue;
					}
					
					if (distance > spaceW * this.scaleOfSpaceingBetweenWords) {
						last.autoAppendSpace();
						last.merge(next);
						continue;
					}
				}
				mergedContents.add(next);
			}
			return mergedContents;
		}
		
		private boolean isConnect(TextContent prev, TextContent next) {
			Rectangle2D r1 = prev.getOutlineRect();
			Rectangle2D r2 = next.getOutlineRect();
			if (this.intersects(r1, r2, prev.getWCharWidth())) {
				return true;
			}
			return false;
		}
		
		private boolean intersects(Rectangle2D r1, Rectangle2D r2, double wCharWidth) {
			if (r1.intersects(r2)) {
				return true;
			}
			return false;
		}
	}
	
	public void addTextSpan(TextContent textContent) {
		String fontName = ContentComparator.removeFontNameSuffix(textContent.getFontName());
		if (this.setting.symbolFontList.contains(fontName)) {
			textContent.setSymbol(true);
		}
		
		TextSpan span = new TextSpan(textContent, this.nextBegin);
		this.pageText.append(span.getText());
		this.textSpanList.add(span);
		
		nextBegin += span.getText().length();
	}
	
	public int lenToContentEnd(int begin) {
		for (int i = 0; i < this.textSpanList.size(); i++) {
			TextSpan span = this.textSpanList.get(i);
			int[] range = new int[]{span.begin, span.begin + span.length};
			if (begin >= range[0] && begin < range[1]) {
				return range[1] - begin;
			}
		}
		return 0;
	}
	
	public TextLob[] getTextLob(int begin, int length) {
		int end = begin + length;
		int beginContentOffset = 0;
		int beginContentIndex = 0;
		int endContentOffset = 0;
		int endContentIndex = 0;
		
		for (int i = 0; i < this.textSpanList.size(); i++) {
			TextSpan span = this.textSpanList.get(i);
			
			int[] range = new int[] {span.begin, span.begin + span.length};
			if (begin >= range[0] && begin < range[1]) {
				beginContentOffset = begin - range[0];
				beginContentIndex = i;
			}

			if (end > range[0] && end <= range[1]) {
				endContentOffset = end - range[0];
				endContentIndex = i;
				break;
			}
		}
		
		if (beginContentIndex == endContentIndex) {
			StringBuilder buf = new StringBuilder("");
			TextSpan span = this.textSpanList.get(beginContentIndex);
			TextContent run = span.textContent;
			String text = span.text;
			buf.append(text.substring(beginContentOffset, endContentOffset));
			Rectangle2D bbox = span.getBBox(beginContentOffset, endContentOffset);
			return new TextLob[]{new TextLob(buf.toString(), bbox, run)};
		}

		TextLob[] list = new TextLob[endContentIndex - beginContentIndex + 1];
		for (int i = beginContentIndex; i <= endContentIndex; i++) {
			TextSpan span = this.textSpanList.get(i);
			TextContent run = span.textContent;
			if (i == beginContentIndex) {
				String text = span.text.substring(beginContentOffset);
				Rectangle2D rect = span.getBBox(beginContentOffset);
				list[i - beginContentIndex] = new TextLob(text, rect, run);
				continue;
			}
			if (i == endContentIndex) {
				String text = span.text.substring(0, endContentOffset);
				Rectangle2D rect = span.getBBox(0, endContentOffset);
				list[i - beginContentIndex] = new TextLob(text, rect, run);
				continue;
			} else {
				Rectangle2D rect = span.getBBox(0, span.length);
				list[i - beginContentIndex] = new TextLob(run.getText(), rect, run);
			}
		}
		return list;
	}
	
	public static class TextLob {
		private String text;
		private Rectangle2D bBox;
		private TextContent content;
		private double baseline;

		public TextLob(TextContent content) {
			this.content = content;
			this.text = content.getText();
			this.bBox = content.getOutlineRect();
		}
		
		public TextLob(String text, Rectangle2D bBox, TextContent content) {
			this.text = text;
			this.bBox = bBox;
			this.content = content;
			this.baseline = content.getBaseline();
		}

		public String getText() {
			return this.text == null ? "" : this.text;
		}

		public Rectangle2D getBoundingBox() {
			return this.bBox;
		}
		
		public TextContent getContent() {
			return this.content;
		}
		
		public boolean mergeLob(TextLob next) {
			if (next == null) {
				return false;
			}
			
			Rectangle2D rect = next.getBoundingBox();
			if (rect == null || this.getBoundingBox() == null) {
				return false;
			}
			
			int mY = (int) Math.round(this.content.getOrigin().getY());
			int nextMY = (int) Math.round(next.content.getOrigin().getY());
			
			if (mY == nextMY) {
				this.text += next.text;
				this.bBox = this.bBox.createUnion(rect);
				return true;
			}
			return false;
		}
		
		public double getBaseline() {
			return this.baseline;
		}
		
		public boolean isInvisible() {
			if (this.bBox != null && this.bBox.isEmpty()) {
				return true;
			}
			if (this.getText().trim().length() == 0) {
				return true;
			}
			return false;
		}
	}
	
	public static class TextSpan {
		String text;
		int begin;
		int length;
		Shape[] shapeArr;
		TextContent textContent;
		
		Rectangle2D bbox;
		
		public TextSpan(TextContent textContent, int beginIndex) {
			String text = textContent.getText() + Character.valueOf(CompareSetting.REPLACEMENT);
			this.text = text;
			this.begin = beginIndex;
			this.length = text.length();
			this.textContent = textContent;
			this.shapeArr = new Shape[this.length];
			List<Shape> shapeList = textContent.getOutlineShapeList();
			if (shapeList != null) {
				for (int i = 0; i < shapeList.size(); i++) {
					this.shapeArr[i] = shapeList.get(i);
				}
			}
			
			this.bbox = this.getBBox(0);
		}
		
		public String getText() {
			return this.text;
		}
		
		public Rectangle2D getBBox() {
			return this.bbox;
		}
		
		public Rectangle2D getBBox(int begin) {
			if (begin == 0 && this.bbox != null) {
				return this.bbox;
			}
			return this.getBBox(begin, this.length);
		}
		
		public Rectangle2D getBBox(int begin, int end) {
			Area area = new Area();
	    	if (this.shapeArr != null) {
	    		for (int i = begin; i < end; i++) {
	    			Shape s = this.shapeArr[i];
	    			if (s == null) {
	    				continue;
	    			}
	    			if (s instanceof GeneralPath) {
	    				area.add(new Area(((GeneralPath) s).getBounds2D()));
	    			} else {
	    				area.add(new Area(s.getBounds2D()));
	    			}
	        	}
	    	}
	    	return area.getBounds2D();
		}
	}
}