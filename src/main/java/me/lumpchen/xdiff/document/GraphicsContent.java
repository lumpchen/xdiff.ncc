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
		
		if (this.isFill() != pathContent.isFill()) {
			return false;
		}
		
		if (!this.getGraphicsStateDesc().equals(pathContent.getGraphicsStateDesc(), this.isFill())) {
			return false;
		}
		
		Rectangle2D r1 = this.getOutlineRect();
		Rectangle2D r2 = pathContent.getOutlineRect();
		
		if (r1 == null || r2 == null) {
			return false;
		}

		Line l1 = this.asLine();
		Line l2 = pathContent.asLine();
		boolean enableLineJoin = false;
		if (l1 != null && l2 != null) {
			enableLineJoin = l1.enableJoin(l2);
		}
		
		if (intersects(r1, r2) || enableLineJoin) {
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
    
    public Line asLine() {
    	if (this.isFill()) {
    		return null;
    	}
    	if (this.getGraphicsStateDesc() == null || this.getGraphicsStateDesc().lineWidth <= 0) {
    		return null;
    	}
    	Rectangle2D r = this.getOutlineRect();
    	if (r.getWidth() > 0 && r.getHeight() > 0) {
    		return null;
    	}
    	Line line = new Line();
    	line.x = r.getX();
    	line.y = r.getY();
    	if (r.getWidth() == 0) {
    		line.vertical = true;
    		line.length = r.getHeight();
    	} else {
    		line.vertical = false;
    		line.length = r.getWidth();
    	}
    	line.width = this.getGraphicsStateDesc().lineWidth;
    	return line;
    }
    
    public static class Line {
    	public double x;
    	public double y;
    	public double width;
    	public double length;
    	public boolean vertical;
    	
    	boolean enableJoin(Line aLine) {
    		if (this.vertical != aLine.vertical) {
    			return false;
    		}
    		double delta = this.width;
    		if (this.vertical) {
    			if (Math.abs(this.x - aLine.x) > delta / 2) {
    				return false;
    			}
    			if (this.y <= aLine.y) {
    				if (this.y + this.length - aLine.y < -delta) {
    					return false;
    				}
    			} else {
    				if (aLine.y + aLine.length - this.y < -delta) {
    					return false;
    				}
    			} 
    		} else {
    			if (Math.abs(this.y - aLine.y) > delta / 2) {
    				return false;
    			}
    			if (this.x <= aLine.x) {
    				if (this.x + this.length - aLine.x < -delta) {
    					return false;
    				}
    			} else {
    				if (aLine.x + aLine.length - this.x < -delta) {
    					return false;
    				}
    			}
    		}
    		
    		return true;
    	}
    }

    static boolean gtWithTor(double d0, double d1) {
    	return d0 + 0.1 > d1;
    }

}