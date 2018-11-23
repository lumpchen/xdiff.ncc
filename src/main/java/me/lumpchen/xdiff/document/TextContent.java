package me.lumpchen.xdiff.document;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TextContent extends PageContent {

	private StringBuilder text;
	private List<Integer> cidArray;
	private Point2D origin;
	private double baseline = -1;
	private AffineTransform at;
	private double averageFontWidth = -1;
	private boolean isSymbol = false;
	
	public TextContent() {
		super();
		this.type = Type.Text;
		this.text = new StringBuilder();
		this.cidArray = new ArrayList<Integer>();
	}	
	
	public void appendText(String unicode, Integer cid, Point2D origin, AffineTransform at, Shape glyphBBox) {
		this.text.append(unicode);
		this.cidArray.add(cid);
		
		this.addOutlineShape(glyphBBox);
		
		if (this.at == null) {
			this.at = at;
		}
		
		if (this.origin == null) {
			this.origin = origin;
			this.baseline = origin.getY();
		}
		
		if (this.averageFontWidth == -1) {
			this.averageFontWidth = glyphBBox.getBounds2D().getWidth();
		}
	}
	
	public double getBaseline() {
		return this.baseline;
	}
	
	public boolean onBaseline(double y) {
		return Math.abs(this.baseline - y) < 0.01; 
	}

	public String getText() {
		return this.text.toString();
	}
	
	public String getSymbolText() {
		if (this.isSymbol) {
			StringBuilder buf = new StringBuilder();
			buf.append("Symbol( ");
			for (Integer i : this.cidArray) {
				buf.append("0x" + Integer.toHexString(i));
				buf.append(" ");
			}
			buf.append(")");
			return buf.toString();
		}
		return null;
	}
	
	public AffineTransform getTransform() {
		return this.at;
	}
	
	public Point2D getOrigin() {
		return this.origin;
	}
	
	@Override
	public String showString() {
		return this.getText();
	}
	
	public int cidAt(int index) {
		if (index < 0 || index >= this.cidArray.size()) {
			throw new IllegalArgumentException("Out of CID array range.");
		}
		return this.cidArray.get(index);
	}

	@Override
	public String getTypeString() {
		return "Text";
	}
	
	@Override
	public Map<String, String> getAttrMap() {
		return null;
	}

	public Rectangle2D getBBox(int begin, int end) {
		if (begin < 0 || end > this.outline.size()) {
			return new Rectangle(0, 0);
		}
		Area area = new Area();
    	if (this.outline != null) {
    		for (int i = begin; i < end; i++) {
    			Shape s = this.outline.get(i);
    			if (s.getBounds().isEmpty()) {
    				continue;
    			}
    			if (s instanceof GeneralPath) {
    				area.add(new Area(((GeneralPath) s).getBounds2D()));
    			} else {
    				area.add(new Area(s));    				
    			}
        	}
    	}
    	return area.getBounds2D();
	}
	
	public void merge(TextContent content) {
		this.text.append(content.getText());
		this.cidArray.addAll(content.cidArray);
		
		this.outline.addAll(content.outline);
		this.calcArea();
	}
	
	public double getWCharWidth() {
		if (this.averageFontWidth == -1) {
			return 0;
		}
		return this.averageFontWidth;
	}
	
	public Integer[] getCIDArray() {
		if (this.cidArray == null) {
			return new Integer[0];
		}
		return this.cidArray.toArray(new Integer[this.cidArray.size()]);
	}
	
	public void setSymbol(boolean isSymbol) {
		this.isSymbol = isSymbol;
	}
	
	public boolean isSymbol() {
		return this.isSymbol;
	}
}



