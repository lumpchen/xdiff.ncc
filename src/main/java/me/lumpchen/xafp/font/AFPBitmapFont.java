package me.lumpchen.xafp.font;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.fontbox.util.BoundingBox;

import me.lumpchen.xafp.CodePage;
import me.lumpchen.xafp.Font;
import me.lumpchen.xafp.FontPatternsMap;
import me.lumpchen.xafp.FontControl.MeasureUnit;
import me.lumpchen.xafp.FontPatternsMap.Pattern;

public class AFPBitmapFont implements AFPFont {
	
	private CodePage codePage;
	private Font charset;
	
	private float pointSize;
	private float xShapeScaleRatio = 1;
	private float yShapeScaleRatio = 1;
	
	private Encoding encoding;
	private FontPatternsMap patternsMap;
	
	public static class CharData {
		int w;
		int h;
		byte[] data;
	}
	private List<CharData> charDataList;
	
	public AFPBitmapFont(CodePage codePage, Font charset) {
		this.codePage = codePage;
		this.charset = charset;
		
		this.xShapeScaleRatio = 72f / this.charset.getFontControl().getXShapeResolution();
		this.yShapeScaleRatio = 72f / this.charset.getFontControl().getYShapeResolution();
		this.initEncoding(this.codePage, charset);
		
		this.patternsMap = this.charset.getPatternsMap();
		this.initData(this.charset.getFontPatterns().getFontData());
	}
	
	private void initEncoding(final CodePage codePage, final Font charset) {
		this.encoding = new Encoding() {
			@Override
			public int getMaxCodePoint() {
				return charset.getPatternsMap().getPatterCount();
			}

			@Override
			public int getMinCodePoint() {
				return 0;
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
			}};
	}
	
	private void initData(byte[] patternBytes) {
		int n = this.patternsMap.getPatterCount();
		this.charDataList = new ArrayList<CharData>();
		
		long end = 0;
		for (int i = 0; i < n; i++) {
			CharData charData = new CharData();
			Pattern pattern = this.patternsMap.getPattern(i);
			charData.w = pattern.charBoxWd;
			charData.h = pattern.charBoxHt;
			
			if (i == n - 1) {
				end = patternBytes.length;
			} else {
				Pattern next = this.patternsMap.getPattern(i + 1);
				end = next.patDOset;
			}
			
			charData.data = new byte[(int) (end - pattern.patDOset)];
			System.arraycopy(patternBytes, (int) pattern.patDOset, charData.data, 0, charData.data.length);
			
			this.charDataList.add(charData);
		}
	}
	
	public BufferedImage getBitmap(int codePoint, Color fillColor) throws IOException {
		CharData charData = this.getCharData(codePoint);
		if (charData == null) {
			return null;
		}
		
		int row = charData.h + 1;
		int col = charData.w + 1;
		if (row == 0 || col == 0) {
			return null;
		}
		BufferedImage img = new BufferedImage(col, row, BufferedImage.TYPE_BYTE_BINARY);
		WritableRaster newRaster = img.getRaster();

		int size = newRaster.getDataBuffer().getSize();
		for (int i = 0; i < size; i++) {
			newRaster.getDataBuffer().setElem(i, (~charData.data[i] & 0xFF));
//			newRaster.getDataBuffer().setElem(i, 0x00);
		}
		img.setData(newRaster);
//		ImageIO.write(img, "jpg", new File("C:/temp/afp/xpression/notwork/4/" + codePoint + ".jpg"));
		BufferedImage timg = makeTransprency(img, fillColor);
		return timg;
	}
	
	private CharData getCharData(int codePoint) {
		String charName = this.encoding.getCharacterName(codePoint);
		if (charName == null) {
			throw new IllegalArgumentException("Invalid code point: " + codePoint);
		}
		int fnmIndex = this.charset.getFontIndex().getFNMIndex(charName);
		return this.charDataList.get(fnmIndex);
	}
	
	public float getWidth(int codePoint) throws IOException {
		CharData charData = this.getCharData(codePoint);
		if (charData == null) {
			return 0;
		}
		return charData.w * this.xShapeScaleRatio;
	}
	
	public float getHeight(int codePoint) throws IOException {
		CharData charData = this.getCharData(codePoint);
		if (charData == null) {
			return 0;
		}
		return charData.h * this.yShapeScaleRatio;
	}
	
	public float getAscenderHeight(int codePoint) throws IOException {
		String charName = this.encoding.getCharacterName(codePoint);
		if (charName == null) {
			throw new IllegalArgumentException("Invalid code point: " + codePoint);
		}
		int ascender = this.charset.getFontIndex().getAscenderHeight(charName);
		return this.unit2Point(ascender, false);
	}
	
	public float getDescenderDepth(int codePoint) throws IOException {
		String charName = this.encoding.getCharacterName(codePoint);
		if (charName == null) {
			throw new IllegalArgumentException("Invalid code point: " + codePoint);
		}
		int descender = this.charset.getFontIndex().getDescenderDepth(charName);
		if (descender <= 0) {
			return 0;
		}
		return this.unit2Point(descender, false);
	} 
	
	public float getCharacterIncrement(int codePoint) throws IOException {
		String charName = this.encoding.getCharacterName(codePoint);
		if (charName == null) {
			throw new IllegalArgumentException("Invalid code point: " + codePoint);
		}
		int inc = this.charset.getFontIndex().getCharacterIncrement(charName);
		return this.unit2Point(inc, true);
	}
	
	@Override
	public Encoding getEncoding() {
		return this.encoding;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override 
	public BoundingBox getFontBBox() throws IOException {
		return null;
	}

	@Override
	public List<Number> getFontMatrix() throws IOException {
		return null;
	}

	@Override
	public float getWidth(String name) throws IOException {
		return 0;
	}

	@Override
	public boolean hasGlyph(String name) throws IOException {
		return false;
	}

	public String getTechSpecName(String gcgid) {
		return null;
	}

	
	private BufferedImage makeTransprency(BufferedImage img, Color fillColor) {
		int w = img.getWidth();
		int h = img.getHeight();
		
		int p = Color.BLACK.getRGB();
		if (fillColor != null) {
			p = fillColor.getRGB();
		}
		BufferedImage a = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (img.getRGB(i, j) != -1) {
					a.setRGB(i, j, p);
				}
			}
		}
		return a;
	}
	
	public void setPointSize(float pointSize) {
		this.pointSize = pointSize;
		if (this.pointSize <= 0) {
			this.pointSize = this.charset.getPointSize();
		}
	}

	public float unit2Point(int unit, boolean hor) {
		if (this.charset.getFontControl().getXMeasureUnit() == MeasureUnit.Fixed) {
			if (hor) {
				float pt = (unit * 72f) / this.charset.getFontControl().getXUnitsPerUnitBase();
				return pt;
			} else {
				float pt = (unit * 72f) / this.charset.getFontControl().getYUnitsPerUnitBase();
				return pt;
			}
		} else {
			if (hor) {
				float pt = (float) unit / this.charset.getFontControl().getXUnitsPerUnitBase() * this.pointSize;
				return pt;
			} else {
				float pt = (float) unit / this.charset.getFontControl().getYUnitsPerUnitBase() * this.pointSize;
				return pt;
			}
		}
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
