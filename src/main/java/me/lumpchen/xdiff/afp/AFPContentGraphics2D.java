package me.lumpchen.xdiff.afp;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import me.lumpchen.xafp.render.GraphicsState;
import me.lumpchen.xafp.render.StructuredAFPPageGraphics;
import me.lumpchen.xdiff.document.GraphicsContent;
import me.lumpchen.xdiff.document.ImageContent;
import me.lumpchen.xdiff.document.PageContent;
import me.lumpchen.xdiff.document.TextContent;
import me.lumpchen.xdiff.document.PageContent.ColorDesc;
import me.lumpchen.xdiff.document.PageContent.TextStateDesc;

public class AFPContentGraphics2D extends StructuredAFPPageGraphics {

	private List<PageContent> contentList;
	private Stack<PageContent> runtimePageContentStack;
	
	public AFPContentGraphics2D(Graphics2D g2, float width, float height) {
		super(g2, width, height);
		
		this.contentList = new ArrayList<PageContent>();
	    this.runtimePageContentStack = new Stack<PageContent>();
	}

	@Override
	public void beginText() {
		TextContent content = new TextContent();
		this.runtimePageContentStack.push(content);		
	}

	@Override
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

	@Override
	public void beginGraphics(boolean stroke, boolean fill) {
		GraphicsContent content = new GraphicsContent(fill);
		this.runtimePageContentStack.push(content);		
	}

	@Override
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

	@Override
	public void beginImage() {
		ImageContent content = new ImageContent();
		this.runtimePageContentStack.push(content);		
	}

	@Override
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

	@Override
	protected void markImage(BufferedImage img, float x, float y, float w, float h) {
		PageContent content = this.peekLastContent();
		ImageContent imageContent = null;;
		if (content != null && content.getType() == PageContent.Type.Image) {
			imageContent = (ImageContent) content;
		}
		
		if (imageContent != null) {
			imageContent.bitsPerComponent = img.getColorModel().getComponentSize(0);
			imageContent.byteCount = -1;
			imageContent.colorSpace = img.getColorModel().getColorSpace().toString();
			imageContent.decode = "NA";
			imageContent.height = img.getHeight();
			imageContent.width = img.getWidth();
			imageContent.suffix = "NA";
			imageContent.imageData = img;
			
			Rectangle2D.Float rect = new Rectangle2D.Float(Math.round(x), Math.round(y), Math.round(w), Math.round(h));
			Shape s = this.state.getCTM().createAffineTransform().createTransformedShape(rect);
			this.markOutline(this.toPageBottom(s.getBounds()));
		}
	}

	@Override
	protected void markPath(Shape path) {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		PageContent content = this.runtimePageContentStack.peek();
		if (content.getType() != PageContent.Type.Path) {
			return;
		}
		content.addOutlineShape(path);
	}
	
	@Override
	protected void markCurrentGraphicsState(GraphicsState state) {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		PageContent content = this.runtimePageContentStack.peek();
		content.setGraphicsStateDesc(convertGraphicsState(state));		
	}

	@Override
	protected void appendChar(String unicode, Integer cid, Point2D origin, AffineTransform at, Shape glyph) {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		
		PageContent content = this.runtimePageContentStack.peek();
		if (content.getType() != PageContent.Type.Text) {
			return;
		}
		
		TextContent textContent = (TextContent) content;
		
		boolean isSymbol = false; // TODO handle symbol font
		textContent.appendText(unicode, cid, origin, at, glyph.getBounds2D(), isSymbol);		
	}

	@Override
	protected void markOutline(Shape outline) {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		
		PageContent content = this.runtimePageContentStack.peek();
		content.addOutlineShape(outline);		
	}

	private PageContent peekLastContent() {
		if (this.runtimePageContentStack.isEmpty()) {
			return null;
		}
		return this.runtimePageContentStack.peek();
	}
	
	private void addToContentList(PageContent content) {
		this.contentList.add(content);
	}
	
	public List<PageContent> getContentList() {
		return this.contentList;
	}
	
	private Rectangle toPageBottom(Rectangle rect) {
		rect.y = (int) (this.height - rect.y - rect.height);
		return rect;
	}
	
	private PageContent.GraphicsStateDesc convertGraphicsState(GraphicsState state) {
        PageContent.GraphicsStateDesc gstate = new PageContent.GraphicsStateDesc();
        gstate.textState = new TextStateDesc();
        
        if (this.state.color != null) {
        	gstate.nonStrokingColor = ColorDesc.newInstance(this.state.color);
        }
        
    	if (this.state.textState.font != null) {
    		gstate.textState.fontName = this.state.textState.font.getName();
    	}
        
        gstate.textState.fontSize = this.state.textState.fontSize;
        if (this.state.textState.color != null) {
        	gstate.nonStrokingColor = ColorDesc.newInstance(this.state.textState.color);
        }
       
        return gstate;
	}

}
