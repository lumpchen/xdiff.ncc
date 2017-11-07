package me.lumpchen.xdiff;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class PageDiffResult {
	
	private List<DiffContent> contentList;
	
	public PageDiffResult() {
		this.contentList = new ArrayList<DiffContent>();
	}

	public int countDiffs() {
		int count = 0;
		for (DiffContent content : this.contentList) {
			if (!(content.getCategory() == DiffContent.Category.AcceptDifference)) {
				count++;
			}
		}
		return count;
	}
	
	public void append(DiffContent entry) {
		this.contentList.add(entry);
	}
	
	public void append(DiffContent[] entries) {
		if (entries == null || entries.length == 0) {
			return;
		}
		for (DiffContent entry : entries) {
			this.append(entry);
		}
	}
	
	public List<DiffContent> getContentList() {
		return this.contentList;
	}
	
	public boolean isAcceptWithDiffs() {
		return this.contentList.size() > 0 && this.countDiffs() == 0;
	}
	
	public void acceptDifferenceArea(List<Rectangle> baseRegion, List<Rectangle> testRegion) {
		if ((baseRegion == null || baseRegion.isEmpty()) 
				&& (testRegion == null || testRegion.isEmpty())) {
			return;
		}
		
		for (DiffContent content : this.contentList) {
			this.inRegion(baseRegion, testRegion, content);
		}
	}
	
	private boolean inRegion(List<Rectangle> baseRegion, List<Rectangle> testRegion, DiffContent content) {
		Rectangle baseRect = null;
		if (baseRegion != null) {
			for (Rectangle rect : baseRegion) {
				if (content.baseBBox != null) {
					if (rect.contains(content.baseBBox)) {
						baseRect = rect;
						break;
					}
				}
			}
		}
		
		Rectangle testRect = null;
		if (testRegion != null) {
			for (Rectangle rect : testRegion) {
				if (content.testBBox != null) {
					if (rect.contains(content.testBBox)) {
						testRect = rect;
						break;
					}
				}
			}
		}
		
		if (baseRect != null || testRect != null) {
			content.setCategory(DiffContent.Category.AcceptDifference);
			content.setAcceptDiffRegion(baseRect, testRect);
			content.putAttr(DiffContent.Key.Attr_Accept_Diff_Region, true, baseRect, testRect);
			return true;
		}
		return false;
	}

	public static class DiffContent {
		public static enum Category {
			Page("Page"), Text("Text"), Image("Image"), Path("Path"), Annot("Annot"), AcceptDifference("Accept_Difference");
			
			public String text;
			
			private Category(String text) {
				this.text = text;
			}
		};
		
		public static class Key {
			public static String Attr_Page_Width = "Page Width";
			public static String Attr_Page_Height = "Page Height";
			public static String Attr_Page_Rotatoin = "Page Rotatoin";
			
			public static String Attr_Pos_X = "X Position";
			public static String Attr_Pos_Y = "Y Position";
			
			public static String Attr_Text = "Text";
			public static String Attr_CID = "CIDs";
			public static String Attr_Font = "Font";
			public static String Attr_Font_size = "Font Size";
			public static String Attr_Stroke_Colorspace = "Stroke Colorspace";
			public static String Attr_Stroke_Color = "Stroke Color";
			public static String Attr_Fill_Colorspace = "Fill Colorspace";
			public static String Attr_Fill_Color = "Fill Color";
			
			public static String Attr_Painting_OP = "Paint Operator";
			public static String Attr_Line_Width = "Line Width";
			public static String Attr_Line_Cap = "Line Cap";
			public static String Attr_Line_Join = "Line Join";
			public static String Attr_Miter_Limit = "Miter Limit";
			
			public static String Attr_Width = "Width";
			public static String Attr_Height = "Height";
			public static String Attr_Byte_count = "Byte Count";
			public static String Attr_Bits_Per_Component = "BitsPerComponent";
			public static String Attr_Position_size = "Position & Size";
			public static String Attr_Decode = "Decode";
			public static String Attr_Suffix = "Suffix";
			public static String Attr_Image_Appearance = "Appearance";
			
			public static String Attr_SubType = "SubType";
			public static String Attr_FieldType = "FieldType";
			public static String Attr_AnnotName = "AnnotName";
			public static String Attr_AnnotContents = "AnnotContents";
			public static String Attr_Annot_Rect = "Rectangle";
			public static String Attr_Annot_Appearance = "Appearance";
			
			public static String Attr_Accept_Diff_Region = "Accept Difference Region";
			
			public static String Attr_Path_Similarity_Ratio = "Path Similarity(percent)";
		}
				
		private Category category;
		private List<ContentAttr> contentAttrList;
		private Area baseOutline;
		private Area testOutline;
		private Rectangle2D baseBBox, testBBox;
		private List<Rectangle2D> baseSubBBox, testSubBBox;
		private Rectangle2D baseAcceptDiffRegion, testAcceptDiffRegion;
		
		public DiffContent(Category category) {
			this.category = category;
			this.contentAttrList = new ArrayList<ContentAttr>();
		}
		
		public Category getCategory() {
			return this.category;
		}
		
		public void setCategory(Category category) {
			this.category = category;
		}
		
		public List<ContentAttr> getAttrList() {
			return this.contentAttrList;
		}
		
		public void setOutline(Area baseOutline, Area testOutline) {
			this.baseOutline = baseOutline;
			this.testOutline = testOutline;
		}
		
		public Rectangle getBaseOutlineRect() {
			if (this.baseOutline != null) {
				return this.baseOutline.getBounds();
			}
			return null;
		}
		
		public Rectangle getTestOutlineRect() {
			if (this.testOutline != null) {
				return this.testOutline.getBounds();
			}
			return null;
		}
		
		public void setBBox(Rectangle2D baseBBox, Rectangle2D testBBox) {
			this.baseBBox = baseBBox;
			this.testBBox = testBBox;
		}
		
		public void setSubBBox(List<Rectangle2D> baseSubBBox, List<Rectangle2D> testSubBBox) {
			this.baseSubBBox = baseSubBBox;
			this.testSubBBox = testSubBBox;
		}
		
		public Rectangle2D getBaseBBox() {
			return this.baseBBox;
		}
		
		public Rectangle2D getTestBBox() {
			return this.testBBox;
		}
		
		public List<Rectangle2D> getBaseSubBBox() {
			return this.baseSubBBox;
		}
		
		public List<Rectangle2D> getTestSubBBox() {
			return this.testSubBBox;
		}
		
		public void setAcceptDiffRegion(Rectangle2D baseAcceptDiffRegion, Rectangle2D testAcceptDiffRegion) {
			this.baseAcceptDiffRegion = baseAcceptDiffRegion;
			this.testAcceptDiffRegion = testAcceptDiffRegion;
		}
		
		public Rectangle2D getBaseAcceptDiffRegion() {
			return this.baseAcceptDiffRegion;
		}
		
		public Rectangle2D getTestAcceptDiffRegion() {
			return this.testAcceptDiffRegion;
		}
		
		private void putAttr(String key, boolean equals, String baseVal, String testVal) {
			ContentAttr attr = new ContentAttr();
			attr.key = key;
			attr.equals = equals;
			attr.baseVal = baseVal;
			attr.testVal = testVal;
			this.contentAttrList.add(attr);
		}
		
		public void putAttr(String key, boolean equals, Object baseVal, Object testVal) {
			String baseStr = asString(baseVal);
			String testStr = asString(testVal);
			this.putAttr(key, equals, baseStr, testStr);
		}
		
		private String asString(Object val) {
			if (val == null) {
				return "";
			}
			if (val instanceof Rectangle2D) {
				return asString((Rectangle2D) val);
			}
			return val.toString();
		}
		
		protected static String asString(Rectangle2D rect) {
			if (rect == null) {
				return "";
			}
			StringBuilder buf = new StringBuilder();
			buf.append("x=" + roundM(rect.getX()));
			buf.append(", ");
			buf.append("y=" + roundM(rect.getY()));
			buf.append(", ");
			buf.append("width=" + roundM(rect.getWidth()));
			buf.append(", ");
			buf.append("height=" + roundM(rect.getHeight()));
			return buf.toString();
		}
		
		protected static double roundM(double d) {
			return Math.round(d * 1000) / 1000d;
		}
		
		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder();
			for (ContentAttr attr : this.contentAttrList) {
				buf.append(attr.key);
				buf.append(" | ");
				buf.append(attr.equals);
				buf.append(" | ");
				buf.append(attr.baseVal == null ? "null" : attr.baseVal);
				buf.append(" | ");
				buf.append(attr.testVal == null ? "null" : attr.testVal);
				
				buf.append("\n");
			}
			
			return buf.toString();
		}
	}
	
	public static class ContentAttr {
		public String key;
		public boolean equals;
		public String baseVal;
		public String testVal;
	}
	
}
