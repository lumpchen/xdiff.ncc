package me.lumpchen.xdiff.document;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class AnnotSet {
	
	private PageThread pageThread;
	private List<AnnotContent> annotList;
	
	public AnnotSet(PageThread pageThread) {
		this.pageThread = pageThread;
		this.annotList = new ArrayList<AnnotContent>();
	}

	public List<AnnotLob> getAnnotLobList() {
		List<AnnotLob> list = new ArrayList<AnnotLob>(this.annotList.size());
		for (AnnotContent content : this.annotList) {
			AnnotLob lob = new AnnotLob(content);
			list.add(lob);
		}
		return list;
	}
	
	public void addAnnotContent(AnnotContent annotContent) {
		this.annotList.add(annotContent);
	}
	
	public static class AnnotLob {
		public String subType;
		public String fieldType;
		public String annotName;
		public String annotContents;
		
		private Rectangle2D bBox;
		private PageContent[] appearance;
		
		public AnnotLob(AnnotContent annot) {
			this.subType = annot.subType;
			this.fieldType = annot.fieldType;
			this.annotName = annot.annotName;
			this.annotContents = annot.annotContents;
			
			this.bBox = annot.getOutlineRect();
			this.appearance = annot.getAppearanceContents();
		}
		
		public Rectangle2D getBBox() {
			return this.bBox;
		}

		public List<PageContent> getAppearance() {
			List<PageContent> contentList = new ArrayList<PageContent>(this.appearance.length);
			for (PageContent content : this.appearance) {
				contentList.add(content);
			}
			return contentList;
		}
	}
}