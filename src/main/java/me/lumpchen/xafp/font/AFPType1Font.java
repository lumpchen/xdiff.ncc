package me.lumpchen.xafp.font;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.fontbox.type1.Type1Font;
import org.apache.fontbox.util.BoundingBox;

import me.lumpchen.xafp.CodePage;
import me.lumpchen.xafp.Font;
import me.lumpchen.xafp.FontControl.PatTech;
import me.lumpchen.xafp.FontPatterns;

public class AFPType1Font implements AFPOutlineFont {

	private static Logger logger = Logger.getLogger(AFPType1Font.class.getName());
	
	private String name;
	private CodePage codePage;
	private Font charset;

	private Encoding encoding;
	private BaseFont baseFont;

	private Map<String, String> nameMap;

	public AFPType1Font(CodePage codePage, Font charset) throws IOException {
		this.codePage = codePage;
		this.charset = charset;
		this.initBaseFont();
		this.initEncoding(codePage);
		this.nameMap = this.charset.getNameMap();
	}

	private void initBaseFont() throws IOException {
		FontPatterns patterns = this.charset.getFontPatterns();
		PatTech patTech = patterns.getPatTech();
		if (patTech == PatTech.PFB_Type1) {
			byte[] fdata = patterns.getFontData();
			PFBParser pfbParser = new PFBParser();
			pfbParser.parse(fdata);
			Type1Font type1 = Type1Font.createWithSegments(pfbParser.getSegment1(), pfbParser.getSegment2());
			this.baseFont = new BaseFont(type1);
		} else {
			throw new java.lang.IllegalArgumentException(patTech.name() + " still not implemented.");
		}
	}

	private void initEncoding(final CodePage codePage) {
		this.encoding = new Encoding() {
			@Override
			public int getMaxCodePoint() {
				return codePage.getMaxCodePoint();
			}

			@Override
			public int getMinCodePoint() {
				return codePage.getMinCodePoint();
			}

			@Override
			public int getCodePoint(int unicode) {
				Integer codepoint = (Integer) codePage.unicode2CodePointMap().get(new Integer(unicode));
				if (codepoint != null) {
					return codepoint.intValue();
				}
				return 0xFFFF;
			}

			@Override
			public String getCharacterName(int codepoint) {
				String cid = codePage.getCodePoint2CharIDMap().get(new Integer(codepoint));
				if (cid == null) {
					return codePage.getDefaultCID();
				}
				return cid;
			}

			@Override
			public int getUnicode(int codepoint) {
				Integer unicode = (Integer) codePage.codePoint2UnicodeMap().get(new Integer(codepoint));
				if (unicode != null) {
					return unicode.intValue();
				}
				return 0xFFFF;
			}

			@Override
			public boolean isDefinedCodePoint(int codepoint) {
				return codePage.getCodePoint2CharIDMap().containsKey(codepoint);
			}
		};
	}

	@Override
	public String getName() {
		return this.charset.getTypefaceStr();
	}

	@Override
	public BoundingBox getFontBBox() throws IOException {
		return this.baseFont.getFontBBox();
	}

	@Override
	public List<Number> getFontMatrix() throws IOException {
		return this.baseFont.getFontMatrix();
	}

	@Override
	public GeneralPath getPath(String name) throws IOException {
		String techSpecName = this.getTechSpecName(name);
		if (techSpecName == null) {
			return null;
		}
		return this.baseFont.getPath(techSpecName);
	}

	@Override
	public float getWidth(String name) throws IOException {
		String techSpecName = this.getTechSpecName(name);
		if (techSpecName == null) {
			return 0;
		}
		return this.baseFont.getWidth(techSpecName);
	}

	@Override
	public boolean hasGlyph(String name) throws IOException {
		return this.baseFont.hasGlyph(name);
	}

	@Override
	public Encoding getEncoding() {
		return this.encoding;
	}

	public String getTechSpecName(String gcgid) {
		if (this.nameMap != null) {
			return this.nameMap.get(gcgid);
		}
		return null;
	}

	@Override
	public float getXUnitPerEm() {
		return this.charset.getFontControl().getXUnitsPerUnitBase();
	}

	@Override
	public float getYUnitPerEm() {
		return this.charset.getFontControl().getYUnitsPerUnitBase();
	}

}
