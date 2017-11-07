package me.lumpchen.xdiff.pdf;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

public class ConcurrentResourceCache implements ResourceCache {

	private final Map<COSObject, SoftReference<PDFont>> fonts = new ConcurrentHashMap<COSObject, SoftReference<PDFont>>();

	private final Map<COSObject, SoftReference<PDColorSpace>> colorSpaces = new ConcurrentHashMap<COSObject, SoftReference<PDColorSpace>>();

	private final Map<COSObject, SoftReference<PDXObject>> xobjects = new ConcurrentHashMap<COSObject, SoftReference<PDXObject>>();

	private final Map<COSObject, SoftReference<PDExtendedGraphicsState>> extGStates = new ConcurrentHashMap<COSObject, SoftReference<PDExtendedGraphicsState>>();

	private final Map<COSObject, SoftReference<PDShading>> shadings = new ConcurrentHashMap<COSObject, SoftReference<PDShading>>();

	private final Map<COSObject, SoftReference<PDAbstractPattern>> patterns = new ConcurrentHashMap<COSObject, SoftReference<PDAbstractPattern>>();

	private final Map<COSObject, SoftReference<PDPropertyList>> properties = new ConcurrentHashMap<COSObject, SoftReference<PDPropertyList>>();

	@Override
	public PDFont getFont(COSObject indirect) throws IOException {
		SoftReference<PDFont> font = fonts.get(indirect);
		if (font != null) {
			return font.get();
		}
		return null;
	}

	@Override
	public void put(COSObject indirect, PDFont font) throws IOException {
		if (indirect == null || font == null) {
			return;
		}
		fonts.put(indirect, new SoftReference<PDFont>(font));
	}

	@Override
	public PDColorSpace getColorSpace(COSObject indirect) throws IOException {
		SoftReference<PDColorSpace> colorSpace = colorSpaces.get(indirect);
		if (colorSpace != null) {
			return colorSpace.get();
		}
		return null;
	}

	@Override
	public void put(COSObject indirect, PDColorSpace colorSpace) throws IOException {
		if (indirect == null || colorSpace == null) {
			return;
		}
		colorSpaces.put(indirect, new SoftReference<PDColorSpace>(colorSpace));
	}

	@Override
	public PDExtendedGraphicsState getExtGState(COSObject indirect) {
		SoftReference<PDExtendedGraphicsState> extGState = extGStates.get(indirect);
		if (extGState != null) {
			return extGState.get();
		}
		return null;
	}

	@Override
	public void put(COSObject indirect, PDExtendedGraphicsState extGState) {
		if (indirect == null || extGState == null) {
			return;
		}
		extGStates.put(indirect, new SoftReference<PDExtendedGraphicsState>(extGState));
	}

	@Override
	public PDShading getShading(COSObject indirect) throws IOException {
		SoftReference<PDShading> shading = shadings.get(indirect);
		if (shading != null) {
			return shading.get();
		}
		return null;
	}

	@Override
	public void put(COSObject indirect, PDShading shading) throws IOException {
		if (indirect == null || shading == null) {
			return;
		}
		shadings.put(indirect, new SoftReference<PDShading>(shading));
	}

	@Override
	public PDAbstractPattern getPattern(COSObject indirect) throws IOException {
		SoftReference<PDAbstractPattern> pattern = patterns.get(indirect);
		if (pattern != null) {
			return pattern.get();
		}
		return null;
	}

	@Override
	public void put(COSObject indirect, PDAbstractPattern pattern) throws IOException {
		if (indirect == null || pattern == null) {
			return;
		}
		patterns.put(indirect, new SoftReference<PDAbstractPattern>(pattern));
	}

	@Override
	public PDPropertyList getProperties(COSObject indirect) {
		SoftReference<PDPropertyList> propertyList = properties.get(indirect);
		if (propertyList != null) {
			return propertyList.get();
		}
		return null;
	}

	@Override
	public void put(COSObject indirect, PDPropertyList propertyList) {
		if (indirect == null || propertyList == null) {
			return;
		}
		properties.put(indirect, new SoftReference<PDPropertyList>(propertyList));
	}

	@Override
	public PDXObject getXObject(COSObject indirect) throws IOException {
		SoftReference<PDXObject> xobject = xobjects.get(indirect);
		if (xobject != null) {
			return xobject.get();
		}
		return null;
	}

	@Override
	public void put(COSObject indirect, PDXObject xobject) throws IOException {
		if (indirect == null || xobject == null) {
			return;
		}
		xobjects.put(indirect, new SoftReference<PDXObject>(xobject));
	}

}
