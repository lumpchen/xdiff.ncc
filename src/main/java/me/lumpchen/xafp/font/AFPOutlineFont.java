package me.lumpchen.xafp.font;

import java.awt.geom.GeneralPath;
import java.io.IOException;

public interface AFPOutlineFont extends AFPFont {

    /**
     * Returns the path for the character with the given name.
     *
     * @return glyph path
     * @throws IOException if the path could not be read
     */
    public GeneralPath getPath(String name) throws IOException;
    
}
