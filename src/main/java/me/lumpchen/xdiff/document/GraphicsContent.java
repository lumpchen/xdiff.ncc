package me.lumpchen.xdiff.document;

import java.awt.geom.Rectangle2D;
import java.util.Map;

public class GraphicsContent extends PageContent {

	private boolean fill;
	
	public GraphicsContent(boolean fill) {
		super();
		this.type = Type.Path;
		this.fill = fill;
	}
	
	@Override
	public String showString() {
		return "";
	}
	
	@Override
	public String getTypeString() {
		return "Graphics";
	}
	
	@Override
	public Map<String, String> getAttrMap() {
		return null;
	}

	public boolean isFill() {
		return this.fill;
	}
	
	public boolean merge(GraphicsContent pathContent) {
		// For solve a NOP problem, the NULL value in here shouldn't happen
		if (pathContent == null) {
			return false;
		}
		if (this.getGraphicsStateDesc() == null && pathContent.getGraphicsStateDesc() == null) {
			return true;
		}
		
		if (!this.getGraphicsStateDesc().equals(pathContent.getGraphicsStateDesc())) {
			return false;
		}
		
		Rectangle2D r1 = this.getOutlineRect();
		Rectangle2D r2 = pathContent.getOutlineRect();
		
		if (r1 == null || r2 == null) {
			return false;
		}

/*			if (r1.contains(r2)) {
			return true;
		}
		if (r2.contains(r1)) {
			this.outline.clear();
			this.outline.addAll(pathContent.outline);
			return true;
		}*/
		
		if (r1.intersects(r2)) {
			this.outline.addAll(pathContent.outline);
			this.calcArea();
			return true;
		}
		return false;
	}
}