package me.lumpchen.xdiff.pdf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import me.lumpchen.xdiff.document.PageContent;

public class PDFContentRenderer {

	public enum ImageType
	{
	    /** Black or white. */
	    BINARY
	    {
	        @Override
	        int toBufferedImageType()
	        {
	            return BufferedImage.TYPE_BYTE_BINARY;
	        }
	    },

	    /** Shades of gray */
	    GRAY
	    {
	        @Override
	        int toBufferedImageType()
	        {
	            return BufferedImage.TYPE_BYTE_GRAY;
	        }
	    },

	    /** Red, Green, Blue */
	    RGB
	    {
	        @Override
	        int toBufferedImageType()
	        {
	            return BufferedImage.TYPE_INT_RGB;
	        }
	    },

	    /** Alpha, Red, Green, Blue */
	    ARGB
	    {
	        @Override
	        int toBufferedImageType()
	        {
	            return BufferedImage.TYPE_INT_ARGB;
	        }
	    };

	    abstract int toBufferedImageType();
	}
	
    protected final PDDocument document;

    private List<PageContent> pageContentList;
    /**
     * Creates a new PDFRenderer.
     * @param document the document to render
     */
    public PDFContentRenderer(PDDocument document)
    {
        this.document = document;
        this.pageContentList = new ArrayList<PageContent>();
    }

    public List<PageContent> getPageContentList() {
		return this.pageContentList;
	}
    
    /**
     * Returns the given page as an RGB image at 72 DPI
     * @param pageIndex the zero-based index of the page to be converted.
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
    public BufferedImage renderImage(int pageIndex) throws IOException
    {
        return renderImage(pageIndex, 1);
    }

    /**
     * Returns the given page as an RGB image at the given scale.
     * A scale of 1 will render at 72 DPI.
     * @param pageIndex the zero-based index of the page to be converted
     * @param scale the scaling factor, where 1 = 72 DPI
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
    public BufferedImage renderImage(int pageIndex, float scale) throws IOException
    {
        return renderImage(pageIndex, scale, ImageType.RGB);
    }

    /**
     * Returns the given page as an RGB image at the given DPI.
     * @param pageIndex the zero-based index of the page to be converted
     * @param dpi the DPI (dots per inch) to render at
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
    public BufferedImage renderImageWithDPI(int pageIndex, float dpi) throws IOException
    {
        return renderImage(pageIndex, dpi / 72f, ImageType.RGB);
    }

    /**
     * Returns the given page as an RGB image at the given DPI.
     * @param pageIndex the zero-based index of the page to be converted
     * @param dpi the DPI (dots per inch) to render at
     * @param imageType the type of image to return
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
    public BufferedImage renderImageWithDPI(int pageIndex, float dpi, ImageType imageType)
            throws IOException
    {
        return renderImage(pageIndex, dpi / 72f, imageType);
    }

    /**
     * Returns the given page as an RGB or ARGB image at the given scale.
     * @param pageIndex the zero-based index of the page to be converted
     * @param scale the scaling factor, where 1 = 72 DPI
     * @param imageType the type of image to return
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
    public BufferedImage renderImage(int pageIndex, float scale, ImageType imageType)
            throws IOException
    {
        PDPage page = document.getPage(pageIndex);

        PDRectangle cropbBox = page.getCropBox();
        float widthPt = cropbBox.getWidth();
        float heightPt = cropbBox.getHeight();
        int widthPx = Math.round(widthPt * scale);
        int heightPx = Math.round(heightPt * scale);
        int rotationAngle = page.getRotation();

        // swap width and height
        BufferedImage image;
        if (rotationAngle == 90 || rotationAngle == 270)
        {
            image = new BufferedImage(heightPx, widthPx, imageType.toBufferedImageType());
        }
        else
        {
            image = new BufferedImage(widthPx, heightPx, imageType.toBufferedImageType());
        }

        // use a transparent background if the imageType supports alpha
        Graphics2D g = image.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (imageType == ImageType.ARGB)
        {
            g.setBackground(new Color(0, 0, 0, 0));
        }
        else
        {
            g.setBackground(Color.WHITE);
        }
        g.clearRect(0, 0, image.getWidth(), image.getHeight());
        
        transform(g, page, scale);

        // the end-user may provide a custom PageDrawer
        PDFPageContentDrawer drawer = createPageDrawer(page);
        drawer.drawPage(g, page.getCropBox());
        g.dispose();

        this.pageContentList.addAll(drawer.getPageContentList());
        
        return image;
    }

    /**
     * Renders a given page to an AWT Graphics2D instance.
     * @param pageIndex the zero-based index of the page to be converted
     * @param graphics the Graphics2D on which to draw the page
     * @throws IOException if the PDF cannot be read
     */
    public void renderPageToGraphics(int pageIndex, Graphics2D graphics) throws IOException
    {
        renderPageToGraphics(pageIndex, graphics, 1);
    }

    /**
     * Renders a given page to an AWT Graphics2D instance.
     * @param pageIndex the zero-based index of the page to be converted
     * @param graphics the Graphics2D on which to draw the page
     * @param scale the scale to draw the page at
     * @throws IOException if the PDF cannot be read
     */
    public void renderPageToGraphics(int pageIndex, Graphics2D graphics, float scale)
            throws IOException
    {
        PDPage page = document.getPage(pageIndex);
        // TODO need width/wight calculations? should these be in PageDrawer?

        transform(graphics, page, scale);

        PDRectangle cropBox = page.getCropBox();
        graphics.clearRect(0, 0, (int) cropBox.getWidth(), (int) cropBox.getHeight());

        // the end-user may provide a custom PageDrawer
        PDFPageContentDrawer drawer = createPageDrawer(page);
        drawer.drawPage(graphics, cropBox);
    }

    // scale rotate translate
    private void transform(Graphics2D graphics, PDPage page, float scale)
    {
        graphics.scale(scale, scale);

        // TODO should we be passing the scale to PageDrawer rather than messing with Graphics?
        int rotationAngle = page.getRotation();
        PDRectangle cropBox = page.getCropBox();

        if (rotationAngle != 0)
        {
            float translateX = 0;
            float translateY = 0;
            switch (rotationAngle)
            {
                case 90:
                    translateX = cropBox.getHeight();
                    break;
                case 270:
                    translateY = cropBox.getWidth();
                    break;
                case 180:
                    translateX = cropBox.getWidth();
                    translateY = cropBox.getHeight();
                    break;
            }
            graphics.translate(translateX, translateY);
            graphics.rotate((float) Math.toRadians(rotationAngle));
        }
    }

    protected PDFPageContentDrawer createPageDrawer(PDPage page) throws IOException {
        return new PDFPageContentDrawer(page);
    }
}
