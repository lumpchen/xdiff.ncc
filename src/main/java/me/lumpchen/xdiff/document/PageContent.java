package me.lumpchen.xdiff.document;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;

public abstract class PageContent {

	public static enum Type {
		Text, Path, Annot, xObject, Image
	};

	protected Type type;
	protected List<Shape> outline;
	private Rectangle2D outlineRect;
	
	private GraphicsStateDesc gstate;

	public static class GraphicsStateDesc {
		public TextStateDesc textState;
		
		public ColorDesc nonStrokingColor;
		public ColorDesc strokingColor;
		
		public float lineWidth;
		public int lineCap;
		public int lineJoin;
		public float miterLimit;
		
		@Override
	    public boolean equals(Object obj) {
	        if (this == obj) {
	        	return true;
	        }
	        if (obj == null) {
	        	return false;
	        }
	        
	        GraphicsStateDesc aObj = (GraphicsStateDesc) obj;
	        if (this.textState != null && aObj.textState != null) {
	        	if (!this.textState.equals(aObj.textState)) {
	        		return false;
	        	}
	        } else if (this.textState != aObj.textState) {
	        	return false;
	        }
	        
	        if (this.nonStrokingColor != null && aObj.nonStrokingColor != null) {
	        	if (!this.nonStrokingColor.equals(aObj.nonStrokingColor)) {
	        		return false;
	        	}
	        } else if (this.nonStrokingColor != aObj.nonStrokingColor) {
	        	return false;
	        }
	        
	        if (this.strokingColor != null && aObj.strokingColor != null) {
	        	if (!this.strokingColor.equals(aObj.strokingColor)) {
	        		return false;
	        	}
	        } else if (this.strokingColor != aObj.strokingColor) {
	        	return false;
	        }
	        
	        if (this.lineWidth != aObj.lineWidth
	        		|| this.lineCap != aObj.lineCap
	        		|| this.lineJoin != aObj.lineJoin
	        		|| this.miterLimit != aObj.miterLimit) {
	        	return true;
	        }
	        return true;
	    }
		
		public boolean equals(Object obj, boolean fill) {
			return this.equals(obj, fill, true);
		}
		
		public boolean equals(Object obj, boolean fill, boolean compareTextState) {
			if (this == obj) {
	        	return true;
	        }
	        if (obj == null) {
	        	return false;
	        }
	        
	        GraphicsStateDesc aObj = (GraphicsStateDesc) obj;
	        
	        if (compareTextState) {
		        if (this.textState != null && aObj.textState != null) {
		        	if (!this.textState.equals(aObj.textState)) {
		        		return false;
		        	}
		        } else if (this.textState != aObj.textState) {
		        	return false;
		        }
	        }
	        
	        if (fill) {
		        if (this.nonStrokingColor != null && aObj.nonStrokingColor != null) {
		        	if (!this.nonStrokingColor.equals(aObj.nonStrokingColor)) {
		        		return false;
		        	}
		        } else if (this.nonStrokingColor != aObj.nonStrokingColor) {
		        	return false;
		        }
	        } else {
		        if (this.strokingColor != null && aObj.strokingColor != null) {
		        	if (!this.strokingColor.equals(aObj.strokingColor)) {
		        		return false;
		        	}
		        } else if (this.strokingColor != aObj.strokingColor) {
		        	return false;
		        }
		        
		        if (this.lineWidth != aObj.lineWidth
		        		|| this.lineCap != aObj.lineCap
		        		|| this.lineJoin != aObj.lineJoin
		        		|| this.miterLimit != aObj.miterLimit) {
		        	return true;
		        }
	        }

	        return true;
		}
		
//        private Matrix currentTransformationMatrix = new Matrix();
//        private PDColor strokingColor = PDDeviceGray.INSTANCE.getInitialColor();
//        private PDColor nonStrokingColor = PDDeviceGray.INSTANCE.getInitialColor();
//        private PDColorSpace strokingColorSpace = PDDeviceGray.INSTANCE;
//        private PDColorSpace nonStrokingColorSpace = PDDeviceGray.INSTANCE;
//        private PDTextState textState = new PDTextState();
//        private float lineWidth = 1;
//        private int lineCap = BasicStroke.CAP_BUTT;
//        private int lineJoin = BasicStroke.JOIN_MITER;
//        private float miterLimit = 10;
//        private PDLineDashPattern lineDashPattern = new PDLineDashPattern();
//        private RenderingIntent renderingIntent;
//        private boolean strokeAdjustment = false;
//        private BlendMode blendMode = BlendMode.COMPATIBLE;
//        private PDSoftMask softMask;
//        private double alphaConstant = 1.0;
//        private double nonStrokingAlphaConstant = 1.0;
//        private boolean alphaSource = false;
//        private boolean overprint = false;
//        private double overprintMode = 0;
//        private COSBase transfer = null;
//        private double flatness = 1.0;
//        private double smoothness = 0;
	}
	
	public static class TextStateDesc {
		public float characterSpacing = 0;
		public float wordSpacing = 0;
		public float horizontalScaling = 100;
		public float leading = 0;
        public float fontSize;
        public RenderingMode renderingMode;
        public float rise = 0;
        public boolean knockout = true;
        public String fontName;
        
		@Override
	    public boolean equals(Object obj) {
	        if (this == obj) {
	        	return true;
	        }
	        if (obj == null) {
	        	return false;
	        }
	        
	        TextStateDesc aObj = (TextStateDesc) obj;
	        if (this.characterSpacing == aObj.characterSpacing
	        		&& this.wordSpacing == aObj.wordSpacing
	        		&& this.horizontalScaling == aObj.horizontalScaling
	        		&& this.leading == aObj.leading
	        		&& this.fontSize == aObj.fontSize
	        		&& this.renderingMode == aObj.renderingMode
	        		&& this.rise == aObj.rise
	        		&& this.knockout == aObj.knockout
	        		&& this.fontName == aObj.fontName) {
	        	return true;
	        }
	        return false;
		}
	}
	
	public static class ColorDesc {
	    public float[] components;
	    public String patternName;
	    public String colorSpace;
	    public int rgb;
	    
	    public static ColorDesc newInstance(Color c) {
	    	ColorDesc desc = new ColorDesc();
	    	desc.components = c.getColorComponents(new float[3]);
	    	desc.patternName = "";
	    	desc.colorSpace = c.getColorSpace().toString();
	    	desc.rgb = c.getRGB();
	    	return desc;
	    }
	    
		@Override
	    public boolean equals(Object obj) {
	        if (this == obj) {
	        	return true;
	        }
	        if (obj == null) {
	        	return false;
	        }
	        
	        ColorDesc aObj = (ColorDesc) obj;
	        if (this.patternName != aObj.patternName
	        		&& this.colorSpace != aObj.colorSpace) {
	        	return false;
	        }
	        
	        if (this.components != null && aObj.components != null) {
	        	if (this.components.length != aObj.components.length) {
	        		return false;
	        	}
	        	
	        	for (int i = 0; i < this.components.length; i++) {
	        		if (this.components[i] != aObj.components[i]) {
	        			return false;
	        		}
	        	}
	        }
	        return true;
		}
	}
	
	public PageContent() {
	}

	public void addOutlineShape(Shape shape) {
		if (this.outline == null) {
			this.outline = new ArrayList<Shape>();
		}
		this.outline.add(shape);
	}
	
	public List<Shape> getOutlineShapeList() {
		return this.outline;
	}
	
    public Rectangle2D getOutlineRect() {
    	if (this.outlineRect != null) {
    		return this.outlineRect;
    	}
    	this.calcArea();
    	return this.outlineRect;
    }
    
    protected void calcArea() {
    	if (this.outline != null) {
    		for (Shape s : this.outline) {
    			if (s.getBounds().isEmpty()) {
    				continue;
    			}
    			
    			if (this.outlineRect == null) {
    				this.outlineRect = s.getBounds2D();
    			} else {
    				this.outlineRect.add(s.getBounds2D());    				
    			}
        	}
    	}
    }
    
    public double getY() {
    	Rectangle2D outline = this.getOutlineRect();
    	if (outline == null) {
    		return 0;
    	}
    	return outline.getY();
    }
    
    public double getX() {
    	Rectangle2D outline = this.getOutlineRect();
    	if (outline == null) {
    		return 0;
    	}
    	return outline.getX();
    }
    
    public double getHeight() {
    	Rectangle2D outline = this.getOutlineRect();
    	if (outline == null) {
    		return 0;
    	}
    	return outline.getHeight();
    }

	public Type getType() {
		return this.type;
	}
	
	public void setGraphicsStateDesc(GraphicsStateDesc gstate) {
		this.gstate = gstate;
	}
	
	public GraphicsStateDesc getGraphicsStateDesc() {
		return this.gstate;
	}
	
	public String getNonStrokingColorspace() {
		if (this.getGraphicsStateDesc() == null
				|| this.getGraphicsStateDesc().nonStrokingColor == null) {
			return null;
		}
		return this.getGraphicsStateDesc().nonStrokingColor.colorSpace;
	}
	
	public float[] getNonStrokingColor() {
		if (this.getGraphicsStateDesc() == null
				|| this.getGraphicsStateDesc().nonStrokingColor == null) {
			return null;
		}
		return this.getGraphicsStateDesc().nonStrokingColor.components;
	}
	
	public String getNonStrokingColorValue() {
		if (this.getGraphicsStateDesc() == null
				|| this.getGraphicsStateDesc().nonStrokingColor == null) {
			return null;
		}
		float[] components = this.getGraphicsStateDesc().nonStrokingColor.components;
		StringBuilder buf = new StringBuilder("");
		if (components != null) {
			for (float c : components) {
				buf.append(c + " ");
			}
		}
		return buf.toString();
	}
	
	public String getStrokingColorspace() {
		if (this.getGraphicsStateDesc() == null
				|| this.getGraphicsStateDesc().strokingColor == null) {
			return null;
		}
		return this.getGraphicsStateDesc().strokingColor.colorSpace;
	}
	
	public float[] getStrokingColor() {
		if (this.getGraphicsStateDesc() == null
				|| this.getGraphicsStateDesc().strokingColor == null) {
			return null;
		}
		return this.getGraphicsStateDesc().strokingColor.components;
	}
	
	public String getStrokingColorValue() {
		if (this.getGraphicsStateDesc() == null
				|| this.getGraphicsStateDesc().strokingColor == null) {
			return null;
		}
		float[] components = this.getGraphicsStateDesc().strokingColor.components;
		StringBuilder buf = new StringBuilder("");
		if (components != null) {
			for (float c : components) {
				buf.append(c + " ");
			}
		}
		return buf.toString();
	}
	
	public String getFontName() {
		if (this.getGraphicsStateDesc() == null 
				|| this.getGraphicsStateDesc().textState == null) {
			return null;
		}
		return this.getGraphicsStateDesc().textState.fontName;
	}
	
	public Float getFontSize() {
		if (this.getGraphicsStateDesc() == null 
				|| this.getGraphicsStateDesc().textState == null) {
			return null;
		}
		return this.getGraphicsStateDesc().textState.fontSize;
	}
	
	@Override
	public String toString() {
		return this.showString();
	}

	abstract public String showString();
	
	abstract public String getTypeString();
	
	abstract public Map<String, String> getAttrMap();

}

