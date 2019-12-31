package me.lumpchen.xdiff.document;

import java.util.ArrayList;
import java.util.List;

class TextChunk {
	
	private List<TextContent> contentList;
	
	public TextChunk() {
		this.contentList = new ArrayList<TextContent>();
	}
	
	public void addContent(TextContent content) {
		this.contentList.add(content);
	}
	
	public List<TextContent> getContentList() {
		return this.contentList;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (TextContent c : this.contentList) {
			buf.append(c.toString());
		}
		
		return buf.toString();
	}
}