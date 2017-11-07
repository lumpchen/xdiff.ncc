package me.lumpchen.xdiff.pdf;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType0;

public class CIDType0Glyph2D  implements Glyph2D
{
    private static final Log LOG = LogFactory.getLog(CIDType0Glyph2D.class);

    private final Map<Integer, GeneralPath> cache = new HashMap<Integer, GeneralPath>();
    private final PDCIDFontType0 font;
    private final String fontName;

    /**
     * Constructor.
     *
     * @param font Type 0 CIDFont
     */
    CIDType0Glyph2D(PDCIDFontType0 font) // todo: what about PDCIDFontType2?
    {
        this.font = font;
        fontName = font.getBaseFont();
    }

    @Override
    public GeneralPath getPathForCharacterCode(int code)
    {
        GeneralPath path = cache.get(code);
        if (path == null)
        {
            try
            {
                if (!font.hasGlyph(code))
                {
                    int cid = font.getParent().codeToCID(code);
                    String cidHex = String.format("%04x", cid);
                    LOG.warn("No glyph for " + code + " (CID " + cidHex + ") in font " + fontName);
                }
    
                path = font.getPath(code);
                cache.put(code, path);
                return path;
            }
            catch (IOException e)
            {
                // todo: escalate this error?
                LOG.error("Glyph rendering failed", e);
                path = new GeneralPath();
            }
        }
        return path;
    }

    @Override
    public void dispose()
    {
        cache.clear();
    }
}