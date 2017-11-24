package me.lumpchen.xafp.render;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.fontbox.ttf.NameRecord;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeCollection.TrueTypeFontProcessor;
import org.apache.fontbox.ttf.TrueTypeFont;

import me.lumpchen.xafp.AFPException;
import me.lumpchen.xafp.CodePage;
import me.lumpchen.xafp.Font;
import me.lumpchen.xafp.FontControl.PatTech;
import me.lumpchen.xafp.MapDataResource;
import me.lumpchen.xafp.font.AFPBitmapFont;
import me.lumpchen.xafp.font.AFPFont;
import me.lumpchen.xafp.font.AFPTruetypeFont;
import me.lumpchen.xafp.font.AFPType1Font;
import me.lumpchen.xafp.sf.triplet.X8BTriplet;
import me.lumpchen.xafp.tool.AFPTool;

public class FontManager {
	
	private static Logger logger = Logger.getLogger(FontManager.class.getName());
	private Map<String, CodePage> codePageMap;
	private Map<String, Font> charsetMap;
	
	private Map<String, AFPFont> fontCache;
	private Map<String, TrueTypeFont> ttfCache;
	
	public FontManager() {
		this.codePageMap = new HashMap<String, CodePage>();
		this.charsetMap = new HashMap<String, Font>();
		
		this.fontCache = new HashMap<String, AFPFont>();
		this.ttfCache = new HashMap<String, TrueTypeFont>();
	}
	
	public AFPFont getFont(String codePageName, String characterSetName) {
		
		String key = codePageName + ":" + characterSetName;
		if (this.fontCache.containsKey(key)) {
			return this.fontCache.get(key);
		}
		
		CodePage codePage = this.codePageMap.get(codePageName);
		Font charset = this.charsetMap.get(characterSetName);
		
		if (codePage == null || charset == null) {
			return null;
		}
		AFPFont font = null;
		if (PatTech.Laser_Matrix_N_bit_Wide == charset.getPatTech()) {
			font = new AFPBitmapFont(codePage, charset);
		} else if (PatTech.PFB_Type1 == charset.getPatTech()) {
			font = new AFPType1Font(codePage, charset);
		} else if (PatTech.CID_Keyed_font_Type0 == charset.getPatTech()) {
			throw new java.lang.IllegalArgumentException("CID_Keyed_font_Type0 still not implemented.");
		}
		
		this.fontCache.put(key, font);
		return font;
	}
	
	public AFPFont getFont(MapDataResource.Attribute mdr) {
		String familyName = mdr.extResRef;
		
		if (!mdr.fontTech.equals(X8BTriplet.TrueType_OpenType)) {
			throw new AFPException("Not a TrueType/ OpenType font.");
		}
		
		TrueTypeFont ttf = this.ttfCache.get(familyName);
		if (ttf == null) {
			logger.severe("Can't find font: " + familyName);
			return null;
		}
		
		try {
			AFPTruetypeFont afpTTF = new AFPTruetypeFont(ttf, mdr.encEnv, mdr.encID);
			return afpTTF;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to load font: " + familyName, e);
		}
		return null;
	}
	
	public void addCodePage(String resName, CodePage codePage) {
		this.codePageMap.put(resName, codePage);
	}
	
	public void addCharset(String resName, Font charset) {
		this.charsetMap.put(resName, charset);
	}
	
	public void addTrueTypeFont(byte[] data) {
		this.addTrueTypeFont(data, false);
	}
	
	public void addTrueTypeFont(byte[] data, boolean isTTC) {
		try {
			if (isTTC) {
				TrueTypeCollection ttc = new TrueTypeCollection(new ByteArrayInputStream(data));
				TrueTypeFontProcessor ttcProcessor = new TrueTypeFontProcessor() {
					@Override
					public void process(TrueTypeFont ttf) throws IOException {
						String name = getTTFFullName(ttf);
						ttfCache.put(name, ttf);
						
						logger.info("TrueType collection font loaded: " + name);
					}
				};
				ttc.processAllFonts(ttcProcessor);
				ttc.close();
			} else {
				if (data[0] == 'O' && data[1] == 'T' && data[2] == 'T' && data[0] == 'O') {
					OTFParser otfParser = new OTFParser();
					OpenTypeFont otf = otfParser.parse(new ByteArrayInputStream(data));
					String name = getTTFFullName(otf);
					this.ttfCache.put(name, otf);
					
					logger.info("OpenType font loaded: " + name);
					otf.close();
				} else {
					TTFParser parser = new TTFParser();
					TrueTypeFont ttf = parser.parse(new ByteArrayInputStream(data));
					String name = getTTFFullName(ttf);
					this.ttfCache.put(getTTFFullName(ttf), ttf);
					
					logger.info("TrueType font loaded: " + name);
					ttf.close();
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to Loading font...", e);
		}
	}
	
	private static String getTTFFullName(TrueTypeFont ttf) throws IOException {
		if (ttf == null) {
			return "";
		}
        // Unicode, Full, BMP, 1.1, 1.0
        for (int i = 4; i >= 0; i--) {
            String nameUni = ttf.getNaming().getName(NameRecord.NAME_FULL_FONT_NAME,
                            NameRecord.PLATFORM_UNICODE,
                            i,
                            NameRecord.LANGUGAE_UNICODE);
            if (nameUni != null) {
                return nameUni;
            }
        }
        
        // Windows, Unicode BMP, EN-US
        String nameWin = ttf.getNaming().getName(NameRecord.NAME_FULL_FONT_NAME,
                        NameRecord.PLATFORM_WINDOWS,
                        NameRecord.ENCODING_WINDOWS_UNICODE_BMP,
                        NameRecord.LANGUGAE_WINDOWS_EN_US);
        if (nameWin != null) {
            return nameWin;
        }

        // Macintosh, Roman, English
        String nameMac = ttf.getNaming().getName(NameRecord.NAME_FULL_FONT_NAME,
                        NameRecord.PLATFORM_MACINTOSH,
                        NameRecord.ENCODING_MACINTOSH_ROMAN,
                        NameRecord.LANGUGAE_MACINTOSH_ENGLISH);
        if (nameMac != null) {
            return nameMac;
        }
        
        return ttf.getNaming().getFontFamily();
	}
	
}
