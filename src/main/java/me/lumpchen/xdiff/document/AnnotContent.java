package me.lumpchen.xdiff.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnnotContent extends PageContent {
	
	public String subType;
	public String fieldType;
	public String annotName;
	public String annotContents;
	
	private List<PageContent> appearenceContents;
	
	public AnnotContent() {
		super();
		this.type = Type.Annot;
		this.appearenceContents = new ArrayList<PageContent>();
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
}