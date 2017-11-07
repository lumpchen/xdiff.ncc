package me.lumpchen.xdiff.document;

import java.awt.image.BufferedImage;
import java.util.Map;

public class ImageContent extends PageContent {
	
	public int bitsPerComponent;
	public int byteCount;
	public String colorSpace;
	public String decode;
	public int height;
	public int width;
	public String suffix;
	public BufferedImage imageData;
	
	public ImageContent() {
		super();
		this.type = Type.Image;
	}
	
	@Override
	public String showString() {
		return "";
	}

	@Override
	public String getTypeString() {
		return "Image";
	}
	
	@Override
	public Map<String, String> getAttrMap() {
		return null;
	}

}