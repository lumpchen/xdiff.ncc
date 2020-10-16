package me.lumpchen.xdiff.pdf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType0;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDShadingPattern;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDSoftMask;
import org.apache.pdfbox.pdmodel.graphics.state.PDTextState;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

import me.lumpchen.xdiff.PageContentDrawer;
import me.lumpchen.xdiff.document.AnnotContent;
import me.lumpchen.xdiff.document.GraphicsContent;
import me.lumpchen.xdiff.document.ImageContent;
import me.lumpchen.xdiff.document.PageContent;
import me.lumpchen.xdiff.document.PageContent.ColorDesc;
import me.lumpchen.xdiff.document.PageContent.Type;
import me.lumpchen.xdiff.document.TextContent;

public class PDFPageContentDrawer extends PDFGraphicsStreamEngine implements PageContentDrawer {
	
	private static final Logger LOG = Logger.getLogger(PDFPageContentDrawer.class.getName());

    // the graphics device to draw to, xform is the initial transform of the device (i.e. DPI)
    private Graphics2D graphics;
    private AffineTransform xform;
    
    // the page box to draw (usually the crop box but may be another)
    private PDRectangle pageSize;

    private int pageRotation;
    
    // whether image of a transparency group must be flipped
    // needed when in a tiling pattern
    private boolean flipTG = false;
    
    private boolean isTJBegin = false;

    // clipping winding rule used for the clipping path
    private int clipWindingRule = -1;
    private GeneralPath linePath = new GeneralPath();
    
    // last clipping path
    private Area lastClip;
    
    private Shape lastGlyph;
    
    // buffered clipping area for text being drawn
    private Area textClippingArea;

    // glyph caches
    private final Map<PDFont, Glyph2D> fontGlyph2D = new HashMap<PDFont, Glyph2D>();
    
	private List<PageContent> contentList;
    private Stack<PageContent> runtimePageContentStack;
    
    public PDFPageContentDrawer(PDPage page) throws IOException {
        super(page);
        
        this.contentList = new ArrayList<PageContent>();
        this.runtimePageContentStack = new Stack<PageContent>();
    }
    
    @Override
    public List<PageContent> getPageContentList() {
		return this.contentList;
	}

	private void addToContentList(PageContent content) {
		if (!this.runtimePageContentStack.isEmpty()) {
			PageContent last = this.runtimePageContentStack.peek();
			if (last != null && last.getType() == Type.Annot) {
				((AnnotContent) last).addAppearanceContent(content);
				return;
			}
		}
		
		this.contentList.add(content);
	}
	
	@Override
	public void showAnnotation(PDAnnotation annotation) throws IOException {
		this.beginAnnot(annotation);
		this.renderAnnotation(annotation);
		this.endAnnot(annotation);
	}
	
	public void beginAnnot(PDAnnotation annot) {
		AnnotContent content = AnnotContentHelper.createAnnotContent(annot);
		if (content != null) {
			this.runtimePageContentStack.push(content);			
		}
	}

	public void endAnnot(PDAnnotation annot) {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		
		PageContent content = this.runtimePageContentStack.pop();
		this.contentList.add(content);
	}
	
    @Override
    public void drawImage(PDImage pdImage) throws IOException {
    	Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
        AffineTransform at = ctm.createAffineTransform();
        this.renderImage(pdImage, at);
        
		AffineTransform imageTransform = new AffineTransform(at);
		Rectangle rect = new Rectangle(0, 0, 1, 1);
		Shape outline = imageTransform.createTransformedShape(rect);
		this.markImage(pdImage, new GeneralPath(outline));
    }
    
	private void markText(String unicode, Integer cid, Point2D origin, AffineTransform at, Shape glyph, float spaceWidth) {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		
		PageContent content = this.runtimePageContentStack.peek();
		if (content.getType() != PageContent.Type.Text) {
			return;
		}
		
		TextContent textContent = (TextContent) content;
		textContent.setSpaceWidth(spaceWidth);
		
		if (textContent.onBaseline(origin.getY())) {
			textContent.appendText(unicode, cid, origin, at, glyph.getBounds2D());
		} else {
    		if (textContent.getText() == null 
//    				|| textContent.getText().trim().length() == 0
    				|| textContent.getHeight() == 0) { //empty TJ
    			this.runtimePageContentStack.pop();
    		} else {
    			this.addToContentList(this.runtimePageContentStack.pop());
    		}
    		
        	TextContent newContent = new TextContent();
        	newContent.setSpaceWidth(spaceWidth);
        	newContent.appendText(unicode, cid, origin, at, glyph.getBounds2D());
        	this.runtimePageContentStack.push(newContent);
        	this.markGraphicsState();
		}
	}

	private void markImage(PDImage pdImage, Shape outline) {
		ImageContent content = new ImageContent();
		this.runtimePageContentStack.push(content);
		
		content.bitsPerComponent = pdImage.getBitsPerComponent();
		try {
			if (pdImage.getColorSpace() != null) {
				content.colorSpace = pdImage.getColorSpace().getName();
			}
		} catch (IOException e) {
		}
		if (pdImage.getDecode() != null) {
			content.decode = pdImage.getDecode().toString();
		}
		content.height = pdImage.getHeight();
		content.width = pdImage.getWidth();
		content.suffix = pdImage.getSuffix();
		try {
			content.byteCount = pdImage.createInputStream().available();
		} catch (IOException e) {
		}
		
		if (this.lastClip != null) {
			Rectangle r1 = outline.getBounds();
			Rectangle r2 = this.lastClip.getBounds();
			if (r1.contains(r2)) {
				outline = this.lastClip.getBounds2D();
			}
		}

		this.markPath(outline);
		this.addToContentList(this.runtimePageContentStack.pop());
		
	}

	private void markPath(Shape gpath) {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		
		PageContent content = this.runtimePageContentStack.peek();
		content.addOutlineShape(gpath);
	}
	
	private void markGraphicsState() {
		if (this.runtimePageContentStack.isEmpty()) {
			return;
		}
		PageContent content = this.runtimePageContentStack.peek();
		
        PDGraphicsState state = getGraphicsState();
        PDTextState textState = state.getTextState();
        
        PageContent.GraphicsStateDesc gstate = new PageContent.GraphicsStateDesc();
		
		gstate.textState = new PageContent.TextStateDesc();
		gstate.textState.characterSpacing = textState.getCharacterSpacing();
		gstate.textState.wordSpacing = textState.getWordSpacing();
		gstate.textState.horizontalScaling = textState.getHorizontalScaling();
		gstate.textState.leading = textState.getLeading();
		gstate.textState.fontSize = textState.getFontSize();
		gstate.textState.renderingMode = textState.getRenderingMode();
		gstate.textState.rise = textState.getRise();
		gstate.textState.knockout = textState.getKnockoutFlag();
        PDFont font = textState.getFont();
        if (font != null) {
        	gstate.textState.setFontName(font.getName());
        	
        	if (!font.isEmbedded() || font.isDamaged()) {
        		try {
        			String fallbackFont = null;
        			if (font instanceof PDTrueTypeFont) {
        				PDTrueTypeFont ttfFont = (PDTrueTypeFont) font;
        				ttfFont.getFontBoxFont().getName();
        				fallbackFont = ttfFont.getTrueTypeFont().getName();
        	        } else if (font instanceof PDType1Font) {
        	            PDType1Font pdType1Font = (PDType1Font) font;
        	            fallbackFont = pdType1Font.getFontBoxFont().getName();
        	        } else if (font instanceof PDType1CFont) {
        	            PDType1CFont type1CFont = (PDType1CFont) font;
        	            fallbackFont = type1CFont.getFontBoxFont().getName();
        	        } else if (font instanceof PDType0Font) {
        	            PDType0Font type0Font = (PDType0Font) font;
        	            if (type0Font.getDescendantFont() instanceof PDCIDFontType2) {
        	            	fallbackFont = ((PDCIDFontType2) type0Font.getDescendantFont()).getTrueTypeFont().getName();
        	            } else if (type0Font.getDescendantFont() instanceof PDCIDFontType0) {
        	                PDCIDFontType0 cidType0Font = (PDCIDFontType0) type0Font.getDescendantFont();
        	                fallbackFont = cidType0Font.getFontBoxFont().getName();
        	            }
        	        }
        			if (fallbackFont != null && !fallbackFont.equalsIgnoreCase(gstate.textState.getFontName())) {
        				gstate.textState.setFontName(gstate.textState.getFontName() + " (not embedded, using fallback font \'" + fallbackFont + "\')");
        			}
        		} catch (Exception e) {
        			LOG.warning("Font " + font.getName() + " is not embedded, using fallback font!");
					// ignore any exception
				}
        	}
        }
        
        gstate.lineWidth = state.getLineWidth();
        gstate.lineCap = state.getLineCap();
        gstate.lineJoin = state.getLineJoin();
        gstate.miterLimit = state.getMiterLimit();

        if (state.getNonStrokingColor() != null) {
        	gstate.nonStrokingColor = new ColorDesc();
        	toColorDesc(state.getNonStrokingColor(), gstate.nonStrokingColor);
        }
        
        if (state.getStrokingColor() != null) {
        	gstate.strokingColor = new ColorDesc();
        	toColorDesc(state.getStrokingColor(), gstate.strokingColor);
        }
        
        content.setGraphicsStateDesc(gstate);
	}
	
	private static void toColorDesc(PDColor pdColor, ColorDesc colorDesc) {
    	if (pdColor.isPattern()) {
    		colorDesc.patternName = pdColor.getPatternName().getName();
    	}
    	colorDesc.components = pdColor.getComponents();
    	colorDesc.colorSpace = pdColor.getColorSpace().getName();
    	try {
			colorDesc.rgb = pdColor.toRGB();
		} catch (Exception e) {
			colorDesc.rgb = -1;
		}
	}
	
	
    /**
     * Returns the underlying Graphics2D. May be null if drawPage has not yet been called.
     */
    protected final Graphics2D getGraphics()
    {
        return graphics;
    }

    /**
     * Returns the current line path. This is reset to empty after each fill/stroke.
     */
    protected final GeneralPath getLinePath()
    {
        return linePath;
    }

    /**
     * Sets high-quality rendering hints on the current Graphics2D.
     */
    private void setRenderingHints()
    {
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                  RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                                  RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                  RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * Draws the page to the requested context.
     * 
     * @param g The graphics context to draw onto.
     * @param pageSize The size of the page to draw.
     * @throws IOException If there is an IO error while drawing the page.
     */
    public void drawPage(Graphics g, PDRectangle pageSize) throws IOException
    {
        graphics = (Graphics2D) g;
        xform = graphics.getTransform();
        this.pageSize = pageSize;
        pageRotation = getPage().getRotation() % 360;

        setRenderingHints();

        graphics.translate(0, pageSize.getHeight());
        graphics.scale(1, -1);

        // TODO use getStroke() to set the initial stroke
        graphics.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

        // adjust for non-(0,0) crop box
        graphics.translate(-pageSize.getLowerLeftX(), -pageSize.getLowerLeftY());

        processPage(getPage());

        for (PDAnnotation annotation : getPage().getAnnotations())
        {
            showAnnotation(annotation);
        }

        graphics = null;
    }

    /**
     * Draws the pattern stream to the requested context.
     *
     * @param g The graphics context to draw onto.
     * @param pattern The tiling pattern to be used.
     * @param colorSpace color space for this tiling.
     * @param color color for this tiling.
     * @param patternMatrix the pattern matrix
     * @throws IOException If there is an IO error while drawing the page.
     */
    public void drawTilingPattern(Graphics2D g, PDTilingPattern pattern, PDColorSpace colorSpace,
                                  PDColor color, Matrix patternMatrix) throws IOException
    {
        Graphics2D oldGraphics = graphics;
        graphics = g;

        GeneralPath oldLinePath = linePath;
        linePath = new GeneralPath();

        Area oldLastClip = lastClip;
        lastClip = null;
        
        boolean oldFlipTG = flipTG;
        flipTG = true;

        setRenderingHints();
        processTilingPattern(pattern, color, colorSpace, patternMatrix);
        
        flipTG = oldFlipTG;
        graphics = oldGraphics;
        linePath = oldLinePath;
        lastClip = oldLastClip;
    }

    /**
     * Returns an AWT paint for the given PDColor.
     */
    protected Paint getPaint(PDColor color) throws IOException
    {
        PDColorSpace colorSpace = color.getColorSpace();
        if (!(colorSpace instanceof PDPattern))
        {
            float[] rgb = colorSpace.toRGB(color.getComponents());
            return new Color(rgb[0], rgb[1], rgb[2]);
        }
        else
        {
            PDPattern patternSpace = (PDPattern)colorSpace;
            PDAbstractPattern pattern = patternSpace.getPattern(color);
            if (pattern instanceof PDTilingPattern)
            {
                PDTilingPattern tilingPattern = (PDTilingPattern) pattern;

                if (tilingPattern.getPaintType() == PDTilingPattern.PAINT_COLORED)
                {
                    // colored tiling pattern
                    return new TilingPaint(this, tilingPattern, xform);
                }
                else
                {
                    // uncolored tiling pattern
                    return new TilingPaint(this, tilingPattern,
                            patternSpace.getUnderlyingColorSpace(), color, xform);
                }
            }
            else
            {
                PDShadingPattern shadingPattern = (PDShadingPattern)pattern;
                PDShading shading = shadingPattern.getShading();
                if (shading == null)
                {
                    LOG.warning("shadingPattern is null, will be filled with transparency");
                    return new Color(0,0,0,0);
                }
                return shading.toPaint(Matrix.concatenate(getInitialMatrix(),
                                                          shadingPattern.getMatrix()));

            }
        }
    }

    // sets the clipping path using caching for performance, we track lastClip manually because
    // Graphics2D#getClip() returns a new object instead of the same one passed to setClip
    private void setClip()
    {
        Area clippingPath = getGraphicsState().getCurrentClippingPath();
        if (clippingPath != lastClip)
        {
            graphics.setClip(clippingPath);
            lastClip = clippingPath;
        }
    }

    @Override
    public void beginText() throws IOException
    {
        setClip();
        beginTextClip();
    }

    @Override
    public void endText() throws IOException
    {
        endTextClip();
    }
    
    private void beginTextContent() {
    	this.runtimePageContentStack.push(new TextContent());
		this.markGraphicsState();
    }
    
    private void endTextContent() {
    	if (this.runtimePageContentStack.isEmpty()) {
    		return;
    	}
    	PageContent content = this.runtimePageContentStack.pop();
    	
    	if (content.getType() == PageContent.Type.Text) {
    		TextContent textContent = (TextContent) content;
    		if (textContent.getText() == null 
//    				|| textContent.getText().trim().length() == 0
    				|| textContent.getHeight() == 0) { //empty TJ
    			return;
    		}
    	}
    	
    	this.addToContentList(content);
    }
    
    @Override
    public void showTextStrings(COSArray array) throws IOException {
    	this.beginTextContent();
    	this.isTJBegin = true;
    	
    	super.showTextStrings(array);
    
    	this.endTextContent();
    	this.isTJBegin = false;
    }
    
	@Override
	protected void showText(byte[] string) throws IOException {
		if (!this.isTJBegin) {
			this.beginTextContent();
		}
		
		super.showText(string);
		
    	if (!this.isTJBegin) {
    		this.endTextContent();
    	}
	}
    
    /**
     * Begin buffering the text clipping path, if any.
     */
    private void beginTextClip()
    {
        // buffer the text clip because it represents a single clipping area
        textClippingArea = new Area();        
    }

    /**
     * End buffering the text clipping path, if any.
     */
    private void endTextClip()
    {
        PDGraphicsState state = getGraphicsState();
        RenderingMode renderingMode = state.getTextState().getRenderingMode();
        
        // apply the buffered clip as one area
        if (renderingMode.isClip() && !textClippingArea.isEmpty())
        {
            state.intersectClippingPath(textClippingArea);
            textClippingArea = null;
        }
    }

    @Override
    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                                 Vector displacement) throws IOException
    {
        AffineTransform at = textRenderingMatrix.createAffineTransform();
        at.concatenate(font.getFontMatrix().createAffineTransform());

        Glyph2D glyph2D = createGlyph2D(font);
        drawGlyph(glyph2D, font, code, displacement, at);
        
		Point2D ptSrc = new Point2D.Double(0, 0);
		Point2D ptDst = new Point2D.Double(0, 0);
		at.transform(ptSrc, ptDst);
		
		float spaceWidth = font.getSpaceWidth();
		spaceWidth = (spaceWidth / 1000f) * this.getGraphicsState().getTextState().getFontSize();
		this.markText(unicode, code, ptDst, at, this.lastGlyph, spaceWidth);
    }

    /**
     * Renders a glyph.
     * 
     * @param path the GeneralPath for the glyph
     * @param font the font
     * @param code character code
     * @param displacement the glyph's displacement (advance)
     * @param at the transformation
     * @throws IOException if something went wrong
     */
    private void drawGlyph(Glyph2D glyph2D, PDFont font, int code, Vector displacement, AffineTransform at) throws IOException
    {
        PDGraphicsState state = getGraphicsState();
        RenderingMode renderingMode = state.getTextState().getRenderingMode();
        
        GeneralPath path = glyph2D.getPathForCharacterCode(code);
        if (path != null)
        {
            // stretch non-embedded glyph if it does not match the width contained in the PDF
            if (!font.isEmbedded())
            {
                float fontWidth = font.getWidthFromFont(code);
                if (fontWidth > 0 && // ignore spaces
                        Math.abs(fontWidth - displacement.getX() * 1000) > 0.0001)
                {
                    float pdfWidth = displacement.getX() * 1000;
                    at.scale(pdfWidth / fontWidth, 1);
                }
            }

            // render glyph
            Shape glyph = at.createTransformedShape(path);

            if (renderingMode.isFill())
            {
                graphics.setComposite(state.getNonStrokingJavaComposite());
                graphics.setPaint(getNonStrokingPaint());
                setClip();
                graphics.fill(glyph);
            }

            if (renderingMode.isStroke())
            {
                graphics.setComposite(state.getStrokingJavaComposite());
                graphics.setPaint(getStrokingPaint());
                graphics.setStroke(getStroke());
                setClip();
                graphics.draw(glyph);
            }

            if (renderingMode.isClip())
            {
                textClippingArea.add(new Area(glyph));
            }
            
            this.lastGlyph = glyph;
        }
    }
    
    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3)
    {
        // to ensure that the path is created in the right direction, we have to create
        // it by combining single lines instead of creating a simple rectangle
        linePath.moveTo((float) p0.getX(), (float) p0.getY());
        linePath.lineTo((float) p1.getX(), (float) p1.getY());
        linePath.lineTo((float) p2.getX(), (float) p2.getY());
        linePath.lineTo((float) p3.getX(), (float) p3.getY());

        // close the subpath instead of adding the last line so that a possible set line
        // cap style isn't taken into account at the "beginning" of the rectangle
        linePath.closePath();
    }

    //TODO: move soft mask apply to getPaint()?
    private Paint applySoftMaskToPaint(Paint parentPaint, PDSoftMask softMask) throws IOException
    {
        if (softMask == null || softMask.getGroup() == null)
        {
            return parentPaint;
        }
        TransparencyGroup transparencyGroup = new TransparencyGroup(softMask.getGroup(), true);
        BufferedImage image = transparencyGroup.getImage();
        BufferedImage gray = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        if (COSName.ALPHA.equals(softMask.getSubType()))
        {
            gray.setData(image.getAlphaRaster());
        }
        else if (COSName.LUMINOSITY.equals(softMask.getSubType()))
        {
            Graphics g = gray.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
        }
        else
        {
            throw new IOException("Invalid soft mask subtype.");
        }
        return new SoftMask(parentPaint, gray, transparencyGroup.getBounds());
    }

    // returns the stroking AWT Paint
    private Paint getStrokingPaint() throws IOException
    {
        return applySoftMaskToPaint(
                getPaint(getGraphicsState().getStrokingColor()),
                getGraphicsState().getSoftMask());
    }

    // returns the non-stroking AWT Paint
    private Paint getNonStrokingPaint() throws IOException
    {
        //TODO why no soft mask?
        return getPaint(getGraphicsState().getNonStrokingColor());
    }

    // create a new stroke based on the current CTM and the current stroke
    private BasicStroke getStroke()
    {
        PDGraphicsState state = getGraphicsState();

        // apply the CTM
        float lineWidth = transformWidth(state.getLineWidth());

        // minimum line width as used by Adobe Reader
        if (lineWidth < 0.25)
        {
            lineWidth = 0.25f;
        }

        PDLineDashPattern dashPattern = state.getLineDashPattern();
        int phaseStart = dashPattern.getPhase();
        float[] dashArray = dashPattern.getDashArray();
        if (dashArray != null)
        {
            // apply the CTM
            for (int i = 0; i < dashArray.length; ++i)
            {
                // minimum line dash width avoids JVM crash, see PDFBOX-2373, PDFBOX-2929, PDFBOX-3204
                float w = transformWidth(dashArray[i]);
                if (w != 0)
                {
                    dashArray[i] = Math.max(w, 0.035f);
                }
            }
            phaseStart = (int)transformWidth(phaseStart);

            // empty dash array is illegal
            // avoid also infinite and NaN values (PDFBOX-3360)
            if (dashArray.length == 0 || Float.isInfinite(phaseStart) || Float.isNaN(phaseStart))
            {
                dashArray = null;
            }
            else
            {
                for (int i = 0; i < dashArray.length; ++i)
                {
                    if (Float.isInfinite(dashArray[i]) || Float.isNaN(dashArray[i]))
                    {
                        dashArray = null;
                        break;
                    }
                }
            }
        }
        return new BasicStroke(lineWidth, state.getLineCap(), state.getLineJoin(),
                               state.getMiterLimit(), dashArray, phaseStart);
    }

    @Override
    public void strokePath() throws IOException
    {
    	this.strokePath((GeneralPath) this.linePath.clone(), true);
        linePath.reset();
    }
    
    private void strokePath(GeneralPath path, boolean mark) throws IOException {
        graphics.setComposite(getGraphicsState().getStrokingJavaComposite());
        graphics.setPaint(getStrokingPaint());
        graphics.setStroke(getStroke());
        setClip();
        graphics.draw(path);
        
		if (mark) {
			this.markPathContent(path, false);
		}
	}
    
	private void markPathContent(GeneralPath gpath, boolean fill) {
		GraphicsContent content = new GraphicsContent(fill);
		this.runtimePageContentStack.push(content);
		this.markGraphicsState();
		
		this.markPath(gpath);
		this.addToContentList(this.runtimePageContentStack.pop());
	}

	@Override
	public void fillPath(int windingRule) throws IOException {
		this.fillPath((GeneralPath) this.getLinePath().clone(), true, windingRule);
		this.linePath.reset();
	}
	
    private void fillPath(GeneralPath linePath, boolean mark, int windingRule) throws IOException
    {
        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
        
        Paint p = getNonStrokingPaint();
        graphics.setPaint(p);
        setClip();
        linePath.setWindingRule(windingRule);

        // disable anti-aliasing for rectangular paths, this is a workaround to avoid small stripes
        // which occur when solid fills are used to simulate piecewise gradients, see PDFBOX-2302
        // note that we ignore paths with a width/height under 1 as these are fills used as strokes,
        // see PDFBOX-1658 for an example
        Rectangle2D bounds = linePath.getBounds2D();
        boolean noAntiAlias = isRectangular(linePath) && bounds.getWidth() > 1 &&
                                                         bounds.getHeight() > 1;
        if (noAntiAlias)
        {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                      RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        if (!(graphics.getPaint() instanceof Color))
        {
            // apply clip to path to avoid oversized device bounds in shading contexts (PDFBOX-2901)
            Area area = new Area(linePath);
            area.intersect(new Area(graphics.getClip()));
            graphics.fill(area);
        }
        else
        {
            graphics.fill(linePath);
        }
        
        if (noAntiAlias)
        {
            // JDK 1.7 has a bug where rendering hints are reset by the above call to
            // the setRenderingHint method, so we re-set all hints, see PDFBOX-2302
            setRenderingHints();
        }
        
		if (mark) {
			this.markPathContent(linePath, true);
		}
    }

    /**
     * Returns true if the given path is rectangular.
     */
    private boolean isRectangular(GeneralPath path)
    {
        PathIterator iter = path.getPathIterator(null);
        double[] coords = new double[6];
        int count = 0;
        int[] xs = new int[4];
        int[] ys = new int[4];
        while (!iter.isDone())
        {
            switch(iter.currentSegment(coords))
            {
                case PathIterator.SEG_MOVETO:
                    if (count == 0)
                    {
                        xs[count] = (int)Math.floor(coords[0]);
                        ys[count] = (int)Math.floor(coords[1]);
                    }
                    else
                    {
                        return false;
                    }
                    count++;
                    break;

                case PathIterator.SEG_LINETO:
                    if (count < 4)
                    {
                        xs[count] = (int)Math.floor(coords[0]);
                        ys[count] = (int)Math.floor(coords[1]);
                    }
                    else
                    {
                        return false;
                    }
                    count++;
                    break;

                case PathIterator.SEG_CUBICTO:
                    return false;

                case PathIterator.SEG_CLOSE:
                    break;
            }
            iter.next();
        }

        if (count == 4)
        {
            return xs[0] == xs[1] || xs[0] == xs[2] ||
                   ys[0] == ys[1] || ys[0] == ys[3];
        }
        return false;
    }

    /**
     * Fills and then strokes the path.
     *
     * @param windingRule The winding rule this path will use.
     * @throws IOException If there is an IO error while filling the path.
     */
    @Override
    public void fillAndStrokePath(int windingRule) throws IOException
    {
        // TODO can we avoid cloning the path?
        GeneralPath path = (GeneralPath)linePath.clone();
        fillPath(windingRule);
        linePath = path;
        strokePath();
    }

    @Override
    public void clip(int windingRule)
    {
        // the clipping path will not be updated until the succeeding painting operator is called
        clipWindingRule = windingRule;
    }

    @Override
    public void moveTo(float x, float y)
    {
        linePath.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y)
    {
        linePath.lineTo(x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3)
    {
        linePath.curveTo(x1, y1, x2, y2, x3, y3);
    }

    @Override
    public Point2D getCurrentPoint()
    {
        return linePath.getCurrentPoint();
    }

    @Override
    public void closePath()
    {
        linePath.closePath();
    }

    @Override
    public void endPath()
    {
        if (clipWindingRule != -1)
        {
            linePath.setWindingRule(clipWindingRule);
            getGraphicsState().intersectClippingPath(linePath);
            clipWindingRule = -1;
        }
        linePath.reset();
    }
    
    private void renderImage(PDImage pdImage, AffineTransform at) throws IOException 
    {
        if (!pdImage.getInterpolate())
        {
            boolean isScaledUp = pdImage.getWidth() < Math.round(at.getScaleX()) ||
                                 pdImage.getHeight() < Math.round(at.getScaleY());

            // if the image is scaled down, we use smooth interpolation, eg PDFBOX-2364
            // only when scaled up do we use nearest neighbour, eg PDFBOX-2302 / mori-cvpr01.pdf
            // stencils are excluded from this rule (see survey.pdf)
            if (isScaledUp || pdImage.isStencil())
            {
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            }
        }

        if (pdImage.isStencil())
        {
            // fill the image with paint
            //TODO why no soft mask?
            BufferedImage image = pdImage.getStencilImage(getNonStrokingPaint());

            // draw the image
            drawBufferedImage(image, at);
        }
        else
        {
            // draw the image
            drawBufferedImage(pdImage.getImage(), at);
        }

        if (!pdImage.getInterpolate())
        {
            // JDK 1.7 has a bug where rendering hints are reset by the above call to
            // the setRenderingHint method, so we re-set all hints, see PDFBOX-2302
            setRenderingHints();
        }
    }

    private void drawBufferedImage(BufferedImage image, AffineTransform at) throws IOException
    {
        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
        setClip();
        PDSoftMask softMask = getGraphicsState().getSoftMask();
        if( softMask != null )
        {
            AffineTransform imageTransform = new AffineTransform(at);
            imageTransform.scale(1, -1);
            imageTransform.translate(0, -1);
            Paint awtPaint = new TexturePaint(image,
                    new Rectangle2D.Double(imageTransform.getTranslateX(), imageTransform.getTranslateY(),
                            imageTransform.getScaleX(), imageTransform.getScaleY()));
            awtPaint = applySoftMaskToPaint(awtPaint, softMask);
            graphics.setPaint(awtPaint);
            Rectangle2D unitRect = new Rectangle2D.Float(0, 0, 1, 1);
            graphics.fill(at.createTransformedShape(unitRect));
        }
        else
        {
            COSBase transfer = getGraphicsState().getTransfer();
            if (transfer instanceof COSArray || transfer instanceof COSDictionary)
            {
                image = applyTransferFunction(image, transfer);
            }

            int width = image.getWidth(null);
            int height = image.getHeight(null);
            AffineTransform imageTransform = new AffineTransform(at);
            imageTransform.scale(1.0 / width, -1.0 / height);
            imageTransform.translate(0, -height);
            graphics.drawImage(image, imageTransform, null);
        }
    }

    private BufferedImage applyTransferFunction(BufferedImage image, COSBase transfer) throws IOException
    {
        BufferedImage bim;
        if (image.getColorModel().hasAlpha())
        {
            bim = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        else
        {
            bim = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        }

        // prepare transfer functions (either one per color or one for all) 
        // and maps (actually arrays[256] to be faster) to avoid calculating values several times
        Integer rMap[], gMap[], bMap[];
        PDFunction rf, gf, bf;
        if (transfer instanceof COSArray)
        {
            COSArray ar = (COSArray) transfer;
            rf = PDFunction.create(ar.getObject(0));
            gf = PDFunction.create(ar.getObject(1));
            bf = PDFunction.create(ar.getObject(2));
            rMap = new Integer[256];
            gMap = new Integer[256];
            bMap = new Integer[256];
        }
        else
        {
            rf = PDFunction.create(transfer);
            gf = rf;
            bf = rf;
            rMap = new Integer[256];
            gMap = rMap;
            bMap = rMap;
        }

        // apply the transfer function to each color, but keep alpha
        float input[] = new float[1];
        for (int x = 0; x < image.getWidth(); ++x)
        {
            for (int y = 0; y < image.getHeight(); ++y)
            {
                int rgb = image.getRGB(x, y);
                int ri = (rgb >> 16) & 0xFF;
                int gi = (rgb >> 8) & 0xFF;
                int bi = rgb & 0xFF;
                int ro, go, bo;
                if (rMap[ri] != null)
                {
                    ro = rMap[ri];
                }
                else
                {
                    input[0] = (ri & 0xFF) / 255f;
                    ro = (int) (rf.eval(input)[0] * 255);
                    rMap[ri] = ro;
                }
                if (gMap[gi] != null)
                {
                    go = gMap[gi];
                }
                else
                {
                    input[0] = (gi & 0xFF) / 255f;
                    go = (int) (gf.eval(input)[0] * 255);
                    gMap[gi] = go;
                }
                if (bMap[bi] != null)
                {
                    bo = bMap[bi];
                }
                else
                {
                    input[0] = (bi & 0xFF) / 255f;
                    bo = (int) (bf.eval(input)[0] * 255);
                    bMap[bi] = bo;
                }
                bim.setRGB(x, y, (rgb & 0xFF000000) | (ro << 16) | (go << 8) | bo);
            }
        }
        return bim;
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException
    {
        PDShading shading = getResources().getShading(shadingName);
        Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
        Paint paint = shading.toPaint(ctm);

        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
        graphics.setPaint(paint);
        graphics.setClip(null);
        lastClip = null;
        graphics.fill(getGraphicsState().getCurrentClippingPath());
    }

    public void renderAnnotation(PDAnnotation annotation) throws IOException
    {
        lastClip = null;
        //TODO support more annotation flags (Invisible, NoZoom, NoRotate)
        // Example for NoZoom can be found in p5 of PDFBOX-2348
        int deviceType = graphics.getDeviceConfiguration().getDevice().getType();
        if (deviceType == GraphicsDevice.TYPE_PRINTER && !annotation.isPrinted())
        {
            return;
        }
        if (deviceType == GraphicsDevice.TYPE_RASTER_SCREEN && annotation.isNoView())
        {
            return;
        }
        if (annotation.isHidden())
        {
            return;
        }
        super.showAnnotation(annotation);

        if (annotation.getAppearance() == null)
        {
            if (annotation instanceof PDAnnotationLink)
            {
                drawAnnotationLinkBorder((PDAnnotationLink) annotation);
            }

            if (annotation instanceof PDAnnotationMarkup && annotation.getSubtype().equals(PDAnnotationMarkup.SUB_TYPE_INK))
            {
                drawAnnotationInk((PDAnnotationMarkup) annotation);
            }
        }
    }

    // return border info. BorderStyle must be provided as parameter because
    // method is not available in the base class
    private AnnotationBorder getAnnotationBorder(PDAnnotation annotation, 
            PDBorderStyleDictionary borderStyle)
    {
        AnnotationBorder ab = new AnnotationBorder();
        COSArray border = annotation.getBorder();
        if (borderStyle == null)
        {
            if (border.get(2) instanceof COSNumber)
            {
                ab.width = ((COSNumber) border.getObject(2)).floatValue();
            }
            if (border.size() > 3)
            {
                COSBase base3 = border.getObject(3);
                if (base3 instanceof COSArray)
                {
                    ab.dashArray = ((COSArray) base3).toFloatArray();
                }
            }
        }
        else
        {
            ab.width = borderStyle.getWidth();
            if (borderStyle.getStyle().equals(PDBorderStyleDictionary.STYLE_DASHED))
            {
                ab.dashArray = borderStyle.getDashStyle().getDashArray();
            }
            if (borderStyle.getStyle().equals(PDBorderStyleDictionary.STYLE_UNDERLINE))
            {
                ab.underline = true;
            }
        }
        ab.color = annotation.getColor();
        if (ab.color == null)
        {
            // spec is unclear, but black seems to be the right thing to do
            ab.color = new PDColor(new float[] { 0 }, PDDeviceGray.INSTANCE);
        }
        if (ab.dashArray != null)
        {
            boolean allZero = true;
            for (float f : ab.dashArray)
            {
                if (f != 0)
                {
                    allZero = false;
                    break;
                }
            }
            if (allZero)
            {
                ab.dashArray = null;
            }
        }
        return ab;
    }
    
    private void drawAnnotationLinkBorder(PDAnnotationLink link) throws IOException
    {
        AnnotationBorder ab = getAnnotationBorder(link, link.getBorderStyle());
        if (ab.width == 0 || ab.color.getComponents().length == 0)
        {
            return;
        }
        PDRectangle rectangle = link.getRectangle();
        Stroke oldStroke = graphics.getStroke();
        graphics.setPaint(getPaint(ab.color));
        BasicStroke stroke = new BasicStroke(ab.width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, ab.dashArray, 0);
        graphics.setStroke(stroke);
        graphics.setClip(null);
        if (ab.underline)
        {
            graphics.drawLine((int) rectangle.getLowerLeftX(), (int) rectangle.getLowerLeftY(),
                    (int) (rectangle.getLowerLeftX() + rectangle.getWidth()), (int) rectangle.getLowerLeftY());
        }
        else
        {
            graphics.drawRect((int) rectangle.getLowerLeftX(), (int) rectangle.getLowerLeftY(),
                    (int) rectangle.getWidth(), (int) rectangle.getHeight());
        }
        graphics.setStroke(oldStroke);
    }

    private void drawAnnotationInk(PDAnnotationMarkup inkAnnotation) throws IOException
    {
        if (!inkAnnotation.getCOSObject().containsKey(COSName.INKLIST))
        {
            return;
        }
        //TODO there should be an InkAnnotation class with a getInkList method
        COSBase base = inkAnnotation.getCOSObject().getDictionaryObject(COSName.INKLIST);
        if (!(base instanceof COSArray))
        {
            return;
        }
        // PDF spec does not mention /Border for ink annotations, but it is used if /BS is not available
        AnnotationBorder ab = getAnnotationBorder(inkAnnotation, inkAnnotation.getBorderStyle());
        if (ab.width == 0 || ab.color.getComponents().length == 0)
        {
            return;
        }
        graphics.setPaint(getPaint(ab.color));
        Stroke oldStroke = graphics.getStroke();
        BasicStroke stroke = 
                new BasicStroke(ab.width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, ab.dashArray, 0);
        graphics.setStroke(stroke);
        graphics.setClip(null);
        COSArray pathsArray = (COSArray) base;
        for (COSBase baseElement : pathsArray)
        {
            if (!(baseElement instanceof COSArray))
            {
                continue;
            }
            COSArray pathArray = (COSArray) baseElement;
            int nPoints = pathArray.size() / 2;
            
            // "When drawn, the points shall be connected by straight lines or curves 
            // in an implementation-dependent way" - we do lines.
            GeneralPath path = new GeneralPath();
            for (int i = 0; i < nPoints; ++i)
            {
                COSBase bx = pathArray.getObject(i * 2);
                COSBase by = pathArray.getObject(i * 2 + 1);
                if (bx instanceof COSNumber && by instanceof COSNumber)
                {
                    float x = ((COSNumber) bx).floatValue();
                    float y = ((COSNumber) by).floatValue();
                    if (i == 0)
                    {
                        path.moveTo(x, y);
                    }
                    else
                    {
                        path.lineTo(x, y);
                    }
                }
            }
            graphics.draw(path);
        }
        graphics.setStroke(oldStroke);
    }

    @Override
    public void showTransparencyGroup(PDTransparencyGroup form) throws IOException
    {
        TransparencyGroup group = new TransparencyGroup(form, false);

        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
        setClip();

        // both the DPI xform and the CTM were already applied to the group, so all we do
        // here is draw it directly onto the Graphics2D device at the appropriate position
        PDRectangle bbox = group.getBBox();
        AffineTransform prev = graphics.getTransform();
        float x = bbox.getLowerLeftX();
        float y = pageSize.getHeight() - bbox.getLowerLeftY() - bbox.getHeight();

        Matrix m = new Matrix(xform);
        float xScale = Math.abs(m.getScalingFactorX());
        float yScale = Math.abs(m.getScalingFactorY());
        
        // adjust the initial translation (includes the translation used to "help" the rotation)
        graphics.setTransform(AffineTransform.getTranslateInstance(xform.getTranslateX(), xform.getTranslateY()));

        graphics.rotate(Math.toRadians(pageRotation));

        // adjust (x,y) at the initial scale + cropbox
        graphics.translate((x - pageSize.getLowerLeftX()) * xScale, (y + pageSize.getLowerLeftY()) * yScale);

        if (flipTG)
        {
            graphics.translate(0, group.getImage().getHeight());
            graphics.scale(1, -1);
        }

        PDSoftMask softMask = getGraphicsState().getSoftMask();
        if (softMask != null)
        {
            BufferedImage image = group.getImage();
            Paint awtPaint = new TexturePaint(image,
                    new Rectangle2D.Float(0, 0, image.getWidth(), image.getHeight()));
            awtPaint = applySoftMaskToPaint(awtPaint, softMask); // todo: PDFBOX-994 problem here?
            graphics.setPaint(awtPaint);
            graphics.fill(new Rectangle2D.Float(0, 0, bbox.getWidth() * (float)xform.getScaleX(),
                                                bbox.getHeight() * (float)xform.getScaleY()));
        }
        else
        {
            graphics.drawImage(group.getImage(), null, null);
        }

        graphics.setTransform(prev);
    }

    private static class AnnotationBorder
    {
        private float[] dashArray = null;
        private boolean underline = false;
        private float width = 0;
        private PDColor color;
    }

    /**
     * Transparency group.
     **/
    private final class TransparencyGroup
    {
        private final BufferedImage image;
        private final PDRectangle bbox;

        private final int minX;
        private final int minY;
        private final int width;
        private final int height;

        /**
         * Creates a buffered image for a transparency group result.
         */
        private TransparencyGroup(PDTransparencyGroup form, boolean isSoftMask) throws IOException
        {
            Graphics2D g2dOriginal = graphics;
            Area lastClipOriginal = lastClip;

            // get the CTM x Form Matrix transform
            Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
            Matrix transform = Matrix.concatenate(ctm, form.getMatrix());

            // transform the bbox
            GeneralPath transformedBox = form.getBBox().transform(transform);

            // clip the bbox to prevent giant bboxes from consuming all memory
            Area clip = (Area)getGraphicsState().getCurrentClippingPath().clone();
            clip.intersect(new Area(transformedBox));
            Rectangle2D clipRect = clip.getBounds2D();
            this.bbox = new PDRectangle((float)clipRect.getX(), (float)clipRect.getY(),
                                        (float)clipRect.getWidth(), (float)clipRect.getHeight());

            // apply the underlying Graphics2D device's DPI transform
            Matrix m = new Matrix(xform);
            AffineTransform dpiTransform = AffineTransform.getScaleInstance(Math.abs(m.getScalingFactorX()), Math.abs(m.getScalingFactorY()));
            Shape deviceClip = dpiTransform.createTransformedShape(clip);
            Rectangle2D bounds = deviceClip.getBounds2D();

            minX = (int) Math.floor(bounds.getMinX());
            minY = (int) Math.floor(bounds.getMinY());
            int maxX = (int) Math.floor(bounds.getMaxX()) + 1;
            int maxY = (int) Math.floor(bounds.getMaxY()) + 1;

            width = maxX - minX;
            height = maxY - minY;

            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); // FIXME - color space
            Graphics2D g = image.createGraphics();

            // flip y-axis
            g.translate(0, image.getHeight());
            g.scale(1, -1);

            boolean oldFlipTG = flipTG;
            flipTG = false;

            // apply device transform (DPI)
            // the initial translation is ignored, because we're not writing into the initial graphics device
            g.transform(dpiTransform);

            AffineTransform xformOriginal = xform;
            xform = AffineTransform.getScaleInstance(m.getScalingFactorX(), m.getScalingFactorY());
            PDRectangle pageSizeOriginal = pageSize;
            pageSize = new PDRectangle(0, 0,
                        (float) bounds.getWidth() / Math.abs(m.getScalingFactorX()),
                        (float) bounds.getHeight() / Math.abs(m.getScalingFactorY()));
            int pageRotationOriginal = pageRotation;
            pageRotation = 0;

            // adjust the origin
            g.translate(-clipRect.getX(), -clipRect.getY());

            graphics = g;
            try
            {
                if (isSoftMask)
                {
                    processSoftMask(form);
                }
                else
                {
                    processTransparencyGroup(form);
                }
            }
            finally 
            {
                flipTG = oldFlipTG;
                lastClip = lastClipOriginal;                
                graphics.dispose();
                graphics = g2dOriginal;
                pageSize = pageSizeOriginal;
                xform = xformOriginal;
                pageRotation = pageRotationOriginal;
            }
        }

        public BufferedImage getImage()
        {
            return image;
        }

        public PDRectangle getBBox()
        {
            return bbox;
        }

        public Rectangle2D getBounds()
        {
            Point2D size = new Point2D.Double(pageSize.getWidth(), pageSize.getHeight());
            // apply the underlying Graphics2D device's DPI transform and y-axis flip
            Matrix m = new Matrix(xform);
            AffineTransform dpiTransform = AffineTransform.getScaleInstance(Math.abs(m.getScalingFactorX()), Math.abs(m.getScalingFactorY()));
            size = dpiTransform.transform(size, size);
            // Flip y
            return new Rectangle2D.Double(minX, size.getY() - minY - height, width, height);
        }
    }
    
    private Glyph2D createGlyph2D(PDFont font) throws IOException
    {
        Glyph2D glyph2D = fontGlyph2D.get(font);
        // Is there already a Glyph2D for the given font?
        if (glyph2D != null)
        {
            return glyph2D;
        }

        if (font instanceof PDTrueTypeFont)
        {
            PDTrueTypeFont ttfFont = (PDTrueTypeFont)font;
            glyph2D = new TTFGlyph2D(ttfFont);  // TTF is never null
        }
        else if (font instanceof PDType1Font)
        {
            PDType1Font pdType1Font = (PDType1Font)font;
            glyph2D = new Type1Glyph2D(pdType1Font); // T1 is never null
        }
        else if (font instanceof PDType1CFont)
        {
            PDType1CFont type1CFont = (PDType1CFont)font;
            glyph2D = new Type1Glyph2D(type1CFont);
        }
        else if (font instanceof PDType0Font)
        {
            PDType0Font type0Font = (PDType0Font) font;
            if (type0Font.getDescendantFont() instanceof PDCIDFontType2)
            {
                glyph2D = new TTFGlyph2D(type0Font); // TTF is never null
            }
            else if (type0Font.getDescendantFont() instanceof PDCIDFontType0)
            {
                // a Type0 CIDFont contains CFF font
                PDCIDFontType0 cidType0Font = (PDCIDFontType0)type0Font.getDescendantFont();
                glyph2D = new CIDType0Glyph2D(cidType0Font); // todo: could be null (need incorporate fallback)
            }
        }
        else
        {
            throw new IllegalStateException("Bad font type: " + font.getClass().getSimpleName());
        }

        // cache the Glyph2D instance
        if (glyph2D != null)
        {
            fontGlyph2D.put(font, glyph2D);
        }

        if (glyph2D == null)
        {
            // todo: make sure this never happens
            throw new UnsupportedOperationException("No font for " + font.getName());
        }

        return glyph2D;
    }
}


