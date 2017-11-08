package me.lumpchen.xafp.font;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.List;

import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.util.BoundingBox;

public class BaseFont implements AFPOutlineFont {

	private FontBoxFont fontboxFont;
	
	public BaseFont(FontBoxFont fontboxFont) {
		this.fontboxFont = fontboxFont;
	}
	
	@Override
	public String getName() {
		try {
			return this.fontboxFont.getName();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public BoundingBox getFontBBox() throws IOException {
		return this.fontboxFont.getFontBBox();
	}

	@Override
	public List<Number> getFontMatrix() throws IOException {
		return this.fontboxFont.getFontMatrix();
	}

	@Override
	public GeneralPath getPath(String name) throws IOException {
		return this.fontboxFont.getPath(name);
	}

	@Override
	public float getWidth(String name) throws IOException {
		return this.fontboxFont.getWidth(name);
	}

	@Override
	public boolean hasGlyph(String name) throws IOException {
		return this.fontboxFont.hasGlyph(name);
	}

	@Override
	public Encoding getEncoding() {
		return null;
	}

	@Override
	public float getXUnitPerEm() {
		return 0;
	}

	@Override
	public float getYUnitPerEm() {
		return 0;
	}

}
