package me.lumpchen.xdiff.pdf;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;

public class Type1Glyph2D implements Glyph2D
{
    private static final Log LOG = LogFactory.getLog(Type1Glyph2D.class);

    private final Map<Integer, GeneralPath> cache = new HashMap<Integer, GeneralPath>();
    private final PDSimpleFont font;

    /**
     * Constructor.
     *
     * @param font PDF Type1 font.
     */
    Type1Glyph2D(PDSimpleFont font)
    {
        this.font = font;
    }

    @Override
    public GeneralPath getPathForCharacterCode(int code)
    {
        // cache
        GeneralPath path = cache.get(code);
        if (path == null)
        {
            // fetch
            try
            {
                String name = font.getEncoding().getName(code);
                if (!font.hasGlyph(name))
                {
                    LOG.warn("No glyph for " + code + " (" + name + ") in font " + font.getName());
                }
    
                // todo: can this happen? should it be encapsulated?
                path = font.getPath(name);
                if (path == null)
                {
                    path = font.getPath(".notdef");
                }
    
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
