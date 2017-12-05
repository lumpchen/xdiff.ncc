package me.lumpchen.xdiff.document;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.lumpchen.xdiff.document.AnnotContent.Link;
import me.lumpchen.xdiff.document.AnnotContent.Widget;

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

		private Map<String, String> additionalMap;
		
		public static final String fieldType = "Field Type";
		public static final String fieldName = "Field Name";
		public static final String alternateFieldName = "Alternate Field Name";
		
		public static final String actionType = "Action Type";
		public static final String actionDest = "Action Destination";
		
		private Rectangle2D bBox;
		private PageContent[] appearance;
		
		public AnnotLob(AnnotContent annot) {
			this.subType = annot.getSubType();
			this.bBox = annot.getOutlineRect();
			this.appearance = annot.getAppearanceContents();
			
			this.addAdditionalAttributes(annot);
		}
		
		private void addAdditionalAttributes(AnnotContent annot) {
			this.additionalMap = new HashMap<String, String>();
			if (annot.getSubType() == AnnotContent.WIDGET) {
				Widget widget = (Widget) annot;
				this.additionalMap.put(fieldType, widget.getFieldType());
				this.additionalMap.put(fieldName, widget.getFieldName());
				this.additionalMap.put(alternateFieldName, widget.getAlternateFieldName());
			} else if (annot.getSubType() == AnnotContent.LINK) {
				Link link = (Link) annot;
				this.additionalMap.put(actionType, link.getActionType());
				this.additionalMap.put(actionDest, link.getActionDest());
			}
		}
		
		public Set<String> getAdditionalKeySet() {
			return this.additionalMap.keySet();
		}
		
		public String getAttribute(String attrName) {
			return this.additionalMap.get(attrName);
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