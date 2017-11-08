package me.lumpchen.xafp.font;

import java.awt.geom.GeneralPath;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.BoundingBox;

import me.lumpchen.xafp.AFPException;
import me.lumpchen.xafp.sf.triplet.X8BTriplet;

public class AFPTruetypeFont implements AFPOutlineFont {

	private BaseFont baseFont;
	private Encoding encoding;
	
	private String familyName;
	private int unitsPerEm;
	
	public AFPTruetypeFont(TrueTypeFont ttf, String encEnv, String encID) throws IOException {
		this.familyName = ttf.getNaming().getFontFamily();
		this.unitsPerEm = ttf.getUnitsPerEm();
		this.baseFont = new BaseFont(ttf);
		
		this.encoding = new CMapEncoding(ttf, encEnv, encID);
	}
	
	public AFPTruetypeFont(byte[] data) throws IOException {
		TTFParser parser = new TTFParser();
		TrueTypeFont ttf = parser.parse(new ByteArrayInputStream(data));
		
		this.familyName = ttf.getNaming().getFontFamily();
		this.unitsPerEm = ttf.getUnitsPerEm();
		this.baseFont = new BaseFont(ttf);
		this.encoding = new Encoding() {

			@Override
			public int getMaxCodePoint() {
				return 0;
			}

			@Override
			public int getMinCodePoint() {
				return 0;
			}

			@Override
			public int getCodePoint(int unicode) {
				return unicode;
			}

			@Override
			public String getCharacterName(int codepoint) {
				String hex = Integer.toHexString(codepoint);
				while (hex.length() < 4) {
					hex = "0" + hex;
				}
				String s = "uni" + hex;
				return s;
			}

			@Override
			public int getUnicode(int codepoint) {
				return codepoint;
			}

			@Override
			public boolean isDefinedCodePoint(int codepoint) {
				return false;
			}
			
		};
	}

	@Override
	public Encoding getEncoding() {
		return this.encoding;
	}

	@Override
	public String getName() {
		if (this.familyName != null) {
			return this.familyName;
		}
		return this.baseFont.getName();
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
	public float getWidth(String name) throws IOException {
		return this.baseFont.getWidth(name);
	}

	@Override
	public boolean hasGlyph(String name) throws IOException {
		return this.baseFont.hasGlyph(name);
	}

	@Override
	public GeneralPath getPath(String name) throws IOException {
		return this.baseFont.getPath(name);
	}

	@Override
	public float getXUnitPerEm() {
		return this.unitsPerEm;
	}

	@Override
	public float getYUnitPerEm() {
		return this.unitsPerEm;
	}
	
	public class CMapEncoding implements Encoding {

		public CMapEncoding(TrueTypeFont ttf, String encEnv, String encID) {
			if (!encEnv.equals(X8BTriplet.EncodingEnvironmentMicrosoft)) {
				throw new AFPException("Encoding environment must be \"Microsoft\"");
			}
			if (!encID.equals(X8BTriplet.EncodingUnicode)) {
				throw new AFPException("EncID must be \"Unicode\"");
			}
		}
		
		@Override
		public int getMaxCodePoint() {
			return 0x00FFFF;
		}

		@Override
		public int getMinCodePoint() {
			return 0;
		}

		@Override
		public int getCodePoint(int unicode) {
			return unicode;
		}

		@Override
		public String getCharacterName(int codepoint) {
			String hex = Integer.toHexString(codepoint);
			while (hex.length() < 4) {
				hex = "0" + hex;
			}
			String s = "uni" + hex;
			return s;
		}

		@Override
		public int getUnicode(int codepoint) {
			return codepoint;
		}

		@Override
		public boolean isDefinedCodePoint(int codepoint) {
			return true;
		}
		
	}
	
}
