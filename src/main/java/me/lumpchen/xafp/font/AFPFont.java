package me.lumpchen.xafp.font;

import java.io.IOException;
import java.util.List;

import org.apache.fontbox.util.BoundingBox;

public interface AFPFont {

	public Encoding getEncoding();
	
    /**
     * The PostScript name of the font.
     */
    public String getName();
    
    /**
     * Returns the font's bounding box in PostScript units.
     */
    public BoundingBox getFontBBox() throws IOException;

    /**
     * Returns the FontMatrix in PostScript units.
     */
    public List<Number> getFontMatrix() throws IOException;

    /**
     * Returns the advance width for the character with the given name.
     *
     * @return glyph advance width
     * @throws IOException if the path could not be read
     */
    public float getWidth(String name) throws IOException;

    /**
     * Returns true if the font contains the given glyph.
     * 
     * @param name PostScript glyph name
     */
    public boolean hasGlyph(String name) throws IOException;
    
    public float getXUnitPerEm();
    
    public float getYUnitPerEm();
    
}
