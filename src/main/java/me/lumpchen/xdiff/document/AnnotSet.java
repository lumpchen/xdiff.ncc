package me.lumpchen.xdiff.document;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.lumpchen.xdiff.document.AnnotContent.Link;
import me.lumpchen.xdiff.document.AnnotContent.Widget;
import me.lumpchen.xdiff.document.AnnotContent.Widget.AAction;

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
		private String subType;
		private String appearanceState;
		private String border;
		private String color;

		private Map<String, String> additionalMap;
		
		public static final String fieldType = "Field Type";
		public static final String fieldName = "Field Name";
		public static final String alternateFieldName = "Alternate Field Name";
		
		public static final String action_pre = "Action";
		public static final String actionType = "Action Type";
		public static final String actionDest = "Action Dest";
		
		public static final String fieldFlag = "Field Flag";
		public static final String fieldValue = "Field Value";
		public static final String fieldDefaultValue = "Default Value";
		
		public static final String options = "Options";
		
		private Rectangle2D bBox;
		private PageContent[] appearance;
		
		public AnnotLob(AnnotContent annot) {
			this.subType = annot.getSubType();
			this.appearanceState = annot.getAppearanceState();
			this.border = annot.getBorder();
			this.color = annot.getColor();
			
			this.bBox = annot.getOutlineRect();
			this.appearance = annot.getAppearanceContents();
			
			this.addAdditionalAttributes(annot);
		}
		
		public String getSubType() {
			return this.subType;
		}
		
		public String getAppearanceState() {
			return this.appearanceState;
		}
		
		public String getBorder() {
			return border;
		}
		
		public String getColor() {
			return this.color;
		}
		
		private void addAdditionalAttributes(AnnotContent annot) {
			this.additionalMap = new HashMap<String, String>();
			if (annot.getSubType() == AnnotContent.WIDGET) {
				Widget widget = (Widget) annot;
				this.additionalMap.put(fieldType, widget.getFieldType());
				this.additionalMap.put(fieldName, widget.getFieldName());
				this.additionalMap.put(alternateFieldName, widget.getAlternateFieldName());
				
				if (widget.getActions() != null && widget.getActions().size() > 0) {
					List<AAction> actions = widget.getActions();
					for (AAction action : actions) {
						this.additionalMap.put(action_pre + " " + action.triggerEvent + ": " + action.eventDesc, 
								action.actionType + ": " + action.actionDest);
					}
				}

				this.additionalMap.put(fieldFlag, widget.getFieldFlag() + "");
				this.additionalMap.put(fieldValue, widget.getFieldValue());
				this.additionalMap.put(fieldDefaultValue, widget.getFieldDefaultValue());
				if (widget.getOptions() != null && widget.getOptions().length > 0) {
					StringBuilder opts = new StringBuilder();
					opts.append("[");
					for (String opt : widget.getOptions()) {
						opts.append(opt + ", ");
					}
					opts.deleteCharAt(opts.length() - 1).deleteCharAt(opts.length() - 1);
					opts.append("]");
					this.additionalMap.put(options, opts.toString());
				}
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