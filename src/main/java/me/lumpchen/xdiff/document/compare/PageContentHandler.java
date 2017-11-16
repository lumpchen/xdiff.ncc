package me.lumpchen.xdiff.document.compare;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import me.lumpchen.xafp.render.StructuredAFPGraphics;
import me.lumpchen.xdiff.document.GraphicsContent;
import me.lumpchen.xdiff.document.ImageContent;
import me.lumpchen.xdiff.document.PageContent;
import me.lumpchen.xdiff.document.TextContent;

public abstract class PageContentHandler implements StructuredAFPGraphics {

	private List<PageContent> contentList;
	private Stack<PageContent> runtimePageContentStack;
	
	protected PageContentHandler() {
        this.contentList = new ArrayList<PageContent>();
        this.runtimePageContentStack = new Stack<PageContent>();
	}
	
	public void beginText() {
		TextContent content = new TextContent();
		this.runtimePageContentStack.push(content);
	}
	
	public void endText() {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		PageContent content = this.runtimePageContentStack.peek();
		if (content.getType() != PageContent.Type.Text) {
			return;
		}
		
		TextContent textContent = (TextContent) content;
		if (textContent.getText().isEmpty()) {
			this.runtimePageContentStack.pop();
		} else {
			this.addToContentList(this.runtimePageContentStack.pop());
		}
	}
	
	public void appendChar(String unicode, Integer cid, Point2D origin, AffineTransform at, Shape glyph) {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		
		PageContent content = this.runtimePageContentStack.peek();
		if (content.getType() != PageContent.Type.Text) {
			return;
		}
		
		TextContent textContent = (TextContent) content;
		textContent.appendText(unicode, cid, origin, at, glyph.getBounds2D());
	}
	
	public void beginGraphics() {
		GraphicsContent content = new GraphicsContent(false);
		this.runtimePageContentStack.push(content);
	}
	
	public void endGraphics() {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		PageContent content = this.runtimePageContentStack.peek();
		if (content.getType() != PageContent.Type.Path) {
			return;
		}
		this.addToContentList(this.runtimePageContentStack.pop());
	}
	
	public void beginImage() {
		ImageContent content = new ImageContent();
		this.runtimePageContentStack.push(content);
	}
	
	public void endImage() {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		PageContent content = this.runtimePageContentStack.peek();
		if (content.getType() != ImageContent.Type.Image) {
			return;
		}
		this.addToContentList(this.runtimePageContentStack.pop());
	}
	
	public PageContent peekLastContent() {
		if (this.runtimePageContentStack.isEmpty()) {
			return null;
		}
		return this.runtimePageContentStack.peek();
	}
	
	public void setOutline(Shape gpath) {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		
		PageContent content = this.runtimePageContentStack.peek();
		content.addOutlineShape(gpath);
	}
	
	public void setGraphicsState(PageContent.GraphicsStateDesc gstate) {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		PageContent content = this.runtimePageContentStack.peek();
		content.setGraphicsStateDesc(gstate);
	}
	
	private void addToContentList(PageContent content) {
		this.contentList.add(content);
	}
	
	public List<PageContent> getContentList() {
		return this.contentList;
	}
}
