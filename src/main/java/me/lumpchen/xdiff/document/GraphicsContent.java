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
		
		if (!this.getGraphicsStateDesc().equals(pathContent.getGraphicsStateDesc(), this.isFill())) {
			return false;
		}
		
		Rectangle2D r1 = this.getOutlineRect();
		Rectangle2D r2 = pathContent.getOutlineRect();
		
		if (r1 == null || r2 == null) {	
			return false;
		}

		if (intersects(r1, r2)) {
			this.outline.addAll(pathContent.outline);
			this.calcArea();
			return true;
		}
		return false;
	}
	
    public boolean intersects(Rectangle2D r0, Rectangle2D r1) {
    	if (r0 == null || r1 == null) {
    		return false;
    	}
    	if (r0.isEmpty() || r0.getWidth() <= 0 || r0.getHeight() <= 0) {
    		return false;
    	}
    	if (r1.isEmpty() || r1.getWidth() <= 0 || r1.getHeight() <= 0) {
    		return false;
    	}
    	
        return intersects(r0.getX(), r0.getY(), r0.getWidth(), r0.getHeight(),
        		r1.getX(), r1.getY(), r1.getWidth(), r1.getHeight());
    }
    
    public boolean intersects(double x0, double y0, double w0, double h0,
    		double x1, double y1, double w1, double h1) {
        return (gtWithTor((x1 + w1), x0) &&
        		gtWithTor((y1 + h1), y0) &&
        		gtWithTor((x0 + w0), x1) &&
        		gtWithTor((y0 + h0), y1));
    }

    static boolean gtWithTor(double d0, double d1) {
    	return d0 + 0.1 > d1;
    }

}