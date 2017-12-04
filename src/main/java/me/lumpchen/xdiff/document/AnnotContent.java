package me.lumpchen.xdiff.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.lumpchen.xdiff.DiffException;

public abstract class AnnotContent extends PageContent {
	
	public static final String LINK = "Link";
	public static final String  WIDGET = "Widget";
	
	protected String subType;
	protected String border;
	protected String name;
	protected String contents;
	protected int flags;
	private List<PageContent> appearenceContents;
	
	AnnotContent() {
		super();
		this.type = Type.Annot;
		this.appearenceContents = new ArrayList<PageContent>();
	}
	
	public static AnnotContent newInstance(String subType) {
		if (LINK.equals(subType)) {
			return new Link();
		} else if (WIDGET.equals(subType)) {
			return new Widget();
		}
		
		throw new DiffException("Annot not implement yet: " + subType);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setBorder(String border) {
		this.border = border;
	}
	
	public String getBorder() {
		return this.border;
	}
	
	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}
	
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
	
	public void addAppearanceContent(PageContent content) {
		this.appearenceContents.add(content);
	}
	
	public PageContent[] getAppearanceContents() {
		return this.appearenceContents.toArray(new PageContent[this.appearenceContents.size()]);
	}
	
	@Override
	public String showString() {
		return "";
	}

	@Override
	public String getTypeString() {
		return "Annot";
	}
	
	@Override
	public Map<String, String> getAttrMap() {
		return null;
	}

	public String getSubType() {
		return subType;
	}
	
	public static class Link extends AnnotContent {
		
		private String actionType;
		private String actionDest;
		
		public Link() {
			super();
			this.subType = LINK;
		}
		
		public void setAction(String actionType, String actionDest) {
			this.actionType = actionType;
			this.actionDest = actionDest;
		}
		
		public String getActionType() {
			return this.actionType;
		}
		
		public String getActionDest() {
			return this.actionDest;
		}
	}
	
	public static class Widget extends AnnotContent {
		private String fieldType;
		private String fieldName;
		private String alternateFieldName;
		
		public Widget() {
			super();
			this.subType = "Widget";
		}

		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getAlternateFieldName() {
			return alternateFieldName;
		}

		public void setAlternateFieldName(String alternateFieldName) {
			this.alternateFieldName = alternateFieldName;
		}
	}

}


