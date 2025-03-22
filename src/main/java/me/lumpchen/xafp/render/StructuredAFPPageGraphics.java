package me.lumpchen.xafp.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Stack;
import java.util.logging.Logger;

import me.lumpchen.xafp.AFPColor;
import me.lumpchen.xafp.AFPException;
import me.lumpchen.xafp.GCGIDDatabase;
import me.lumpchen.xafp.font.AFPBitmapFont;
import me.lumpchen.xafp.font.AFPFont;
import me.lumpchen.xafp.font.AFPOutlineFont;
import me.lumpchen.xafp.render.GraphicsState.TextState;

public abstract class StructuredAFPPageGraphics implements StructuredAFPGraphics {

	private static Logger logger = Logger.getLogger(StructuredAFPPageGraphics.class.getName());
	
	protected Graphics2D g2;
	protected float width;
	protected float height;
	
	protected GraphicsState state;
	
	protected Matrix textMatrix;
	protected Matrix textLineMatrix;
	
	protected GeneralPath currPath;
	
	protected Stack<Graphics2D> g2Stack = new Stack<Graphics2D>();
	protected Stack<GraphicsState> gsStack = new Stack<GraphicsState>();
	
	public StructuredAFPPageGraphics(Graphics2D g2, float width, float height) {
		this.g2 = g2;
		this.width = width;
		this.height = height;
		
		this.state = new GraphicsState();
		this.textMatrix = new Matrix();
		this.textLineMatrix = new Matrix();
	}
	
	@Override
	public void clip(Shape s) {
		this.g2.clip(s);
	}
	
	@Override
	public void scale(double sx, double sy) {
		this.state.getCTM().scale(sx, sy);
	}

	@Override
	public void setBackground(Color color) {
		this.state.backgroundColor = color;
		this.g2.setBackground(this.state.backgroundColor);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		this.g2.clearRect(x, y, width, height);
	}

	@Override
	public void translate(double tx, double ty) {
		this.state.getCTM().translate(tx, ty);
		this.g2.translate(tx, ty);
	}

	@Override
	public void rotate(double theta) {
		this.state.getCTM().rotate(theta);
		this.g2.rotate(theta);
	}

	@Override
	public void setColor(AFPColor c) {
		this.state.afpColor = c;
		
		Color awtColor = c.toJavaColor();
		this.state.color = awtColor;
		this.state.afpColor = c;
	}
	
	@Override
	public void drawImage(BufferedImage img, float x, float y, float w, float h) {
		this.g2.drawImage(img, Math.round(x), Math.round(y), Math.round(w), Math.round(h), null);
		
		this.markImage(img, x, y, w, h);
	}

	@Override
	public void setLineWidth(float w) {
		this.state.textState.ruleWidth = w;
	}

	@Override
	public void setAFPFont(AFPFont afpFont, float fontSize) {
		this.state.textState.font = afpFont;
		this.state.textState.fontSize = fontSize;
	}
	
	@Override
	public void setTextRotation(float degree) {
		this.state.textState.rotation = degree;
	}

	@Override
	public void setTextColor(AFPColor c) {
		this.state.textState.afpColor = c;
		
		Color awtColor = c.toJavaColor();
		this.state.textState.color = awtColor;
		this.state.textState.afpColor = c;
	}

	@Override
	public void setTextPosX(float tx) {
		this.state.textState.posX = tx;
	}

	@Override
	public void setTextPosY(float ty) {
		this.state.textState.posY = ty;
	}
	
	private void setTextState(TextState tstate) {
		this.textLineMatrix = new Matrix();
		this.textLineMatrix.concatenate(Matrix.getTranslateInstance(tstate.posX, 0));
		
		this.textMatrix = new Matrix();
		this.textMatrix.concatenate(Matrix.getTranslateInstance(0, tstate.posY));
		
		this.g2.setColor(tstate.color == null ? Color.black : tstate.color);
		
		if (tstate.rotation == 0) {
			this.g2.translate(0, 0);
		}  else if (tstate.rotation == 90) {
			this.g2.translate(this.width, 0);	
		}  else if (tstate.rotation == 180) {
			this.g2.translate(this.width, this.height);	
		} else if (tstate.rotation == 270) {
			this.g2.translate(0, this.height);	
		}
		this.g2.rotate(Math.toRadians(tstate.rotation));
	}
	
	@Override
	public void drawString(String str, float x, float y) {
	}
	
	@Override
	public void drawString(char[] text, float x, float y) {
		this.save();
		
		this.markCurrentGraphicsState(this.state);
		
		this.setTextState(this.state.textState);
		
		if (this.state.textState.font == null) {
			logger.severe("Can't render text {" + new String(text) + "} without font!");
			return;
		}
		
		float fontSize = this.state.textState.fontSize;
        Matrix parameters = new Matrix(fontSize, 0, 0, fontSize, 0, 0);
        float xUnitScale = 1 / this.state.textState.font.getXUnitPerEm();
        float yUnitScale = 1 / this.state.textState.font.getYUnitPerEm();
        
//        StringBuilder sb = new StringBuilder();
		for (char b : text) {
			int cid = (int) (b & 0xFFFF);
			int unicode = this.state.textState.font.getEncoding().getUnicode(b & 0xFFFF);
			String gcgid = this.state.textState.font.getEncoding().getCharacterName(b & 0xFFFF);
			
			String unicodeString = "";
    		if (unicode != 0xFFFF) {
    			unicodeString = new String(Character.toChars(unicode));	
    		} else {
    			unicodeString = new String(Character.toChars(GCGIDDatabase.EBCDICToASCII(cid)));
    		}
    		
//			sb.append(unicodeString);
			try {
				Matrix ctm = this.state.getCTM();
				Matrix textRenderingMatrix = parameters.multiply(this.textLineMatrix).multiply(this.textMatrix).multiply(ctm);
				
				AffineTransform at = textRenderingMatrix.createAffineTransform();

				Matrix fm = new Matrix(xUnitScale, 0, 0, yUnitScale, 0, 0);
				at.concatenate(fm.createAffineTransform());
				
        		Point2D.Float dst = new Point2D.Float();
        		at.transform(new Point2D.Float(0, 0), dst);
        		
		        if (this.state.textState.font instanceof AFPOutlineFont) {
		        	AFPOutlineFont font = (AFPOutlineFont) this.state.textState.font;
					GeneralPath glyph = font.getPath(gcgid);
					if (glyph == null) {
						continue;
					}
					
					Matrix gm = new Matrix(1, 0, 0, -1, 0, 0);
					Shape gp = gm.createAffineTransform().createTransformedShape(glyph);
					
					Shape s = at.createTransformedShape(gp);
					this.g2.fill(s);
					
					double advance = this.state.textState.font.getWidth(gcgid) * xUnitScale;
					this.textLineMatrix.concatenate(Matrix.getTranslateInstance(advance * fontSize, 0));
					
					Rectangle rect = this.toPageBottom(s.getBounds());
					this.appendChar(unicodeString, cid, dst, at, rect);
		        } else if (this.state.textState.font instanceof AFPBitmapFont) {
		        	AFPBitmapFont font = (AFPBitmapFont) this.state.textState.font;
		        	font.setPointSize(fontSize);
		        	int codePoint = (int) (b & 0xFFFF);
		        	BufferedImage glyphBitmap = font.getBitmap(codePoint, this.state.textState.color);
		        	if (glyphBitmap != null) {
		        		float charW = font.getWidth(codePoint);
		        		float charH = font.getHeight(codePoint);
		        		
		        		Matrix dm = new Matrix(1, 0, 0, 1, 0, 0);
		        		dm.translate(dst.x, dst.y - font.getAscenderHeight(codePoint));
		        		dm.scale(charW / glyphBitmap.getWidth(), charH / glyphBitmap.getHeight());
		        		
		        		this.g2.drawImage(glyphBitmap, dm.createAffineTransform(), null);
		        		
		        		float advance = font.getCharacterIncrement(codePoint);
		        		this.textLineMatrix.translate(advance, 0);
		        		
		        		this.appendChar(unicodeString, cid, dst, at, dm.createAffineTransform().createTransformedShape(
		        				new Rectangle2D.Float(0, 0, glyphBitmap.getWidth(), glyphBitmap.getHeight())));
		        	}
		        }
				
			} catch (IOException e) {
				throw new AFPException("AFP rendering error: ", e);
			}
		}
//		System.out.println(sb.toString());
		this.restore();
	}
	
	@Override
	public void drawRule(float x1, float y1, float x2, float y2, boolean horizon) {
		this.save();
		this.antialiasOff();
		this.setTextState(this.state.textState);
		
		Matrix ctm = this.state.getCTM();
		Matrix m;
		if (horizon) {
			m = new Matrix(1, 0, 0, 1, 0, this.state.textState.ruleWidth / 2);
		} else {
			m = new Matrix(1, 0, 0, 1, this.state.textState.ruleWidth / 2, 0);
		}
		
		Matrix textRenderingMatrix = m.multiply(this.textLineMatrix).multiply(this.textMatrix).multiply(ctm);
		AffineTransform at = textRenderingMatrix.createAffineTransform();
		
		Line2D.Float line = new Line2D.Float(x1, y1, x2, y2);
		Shape s = at.createTransformedShape(line);
		
		this.g2.setStroke(new BasicStroke(this.state.textState.ruleWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		this.g2.draw(s);
		
		this.antialiasOn();
		
		this.markCurrentGraphicsState(this.state);
		this.markOutline(this.toPageBottom(s.getBounds()));
		
		this.restore();
	}
	
	@Override
	public void draw(Shape s) {
		this.g2.setColor(this.state.color);
		this.g2.setStroke(new BasicStroke(this.state.textState.ruleWidth));

		if (this.state.stroke) {
			this.g2.draw(s);
		}
		if (this.state.fill) {
			this.g2.fill(s);
		}
	}
	
	@Override
	public void beginPath(boolean stroke, boolean fill) {
		this.currPath = new GeneralPath();
		this.state.stroke = stroke;
		this.state.fill = fill;
		
		this.beginGraphics(stroke, fill);
	}
	
	@Override
	public GeneralPath getCurrentPath() {
		return this.currPath;
	}

	@Override
	public void fill(Shape s) {
		this.g2.fill(s);
	}

	@Override
	public void endPath() {
		if (this.currPath != null) {
			this.currPath.closePath();
			
			AffineTransform at = this.getGraphicsState().getSegmentCTM().createAffineTransform();
			Shape ts = at.createTransformedShape(this.currPath);
			this.draw(ts);
			
			this.markPath(ts);
			this.currPath = null;
			this.markCurrentGraphicsState(this.state);
			this.endGraphics();
		}
	}
	
	@Override
	public void save() {
		Graphics2D savedGraphics = this.g2;
		GraphicsState savedState = this.state;
		this.g2Stack.push(savedGraphics);
		this.gsStack.push(savedState);
		
		this.g2 = (Graphics2D) this.g2.create();
		this.state = this.state.clone();
	}

	@Override
	public void restore() {
		this.g2.dispose();
		this.g2 = this.g2Stack.pop();
		this.state = this.gsStack.pop();
	}
	
	@Override
    public void antialiasOn() {
    	this.g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    	this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    
	@Override
    public void antialiasOff() {
    	this.g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    	this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }
	
	private Rectangle toPageBottom(Rectangle rect) {
		rect.y = (int) (this.height - rect.y - rect.height);
		return rect;
	}
	
    public GraphicsState getGraphicsState() {
    	return this.state;
    }
    
    public void setGraphicsState(GraphicsState gstate) {
    	this.state = gstate;
    }
	
	abstract protected void markImage(BufferedImage img, float x, float y, float w, float h);
	abstract protected void markCurrentGraphicsState(GraphicsState state);
	abstract protected void appendChar(String unicode, Integer cid, Point2D origin, AffineTransform at, Shape glyph);
	abstract protected void markOutline(Shape outline);
	abstract protected void markPath(Shape path);
}

