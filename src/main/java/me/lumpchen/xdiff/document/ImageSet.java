package me.lumpchen.xdiff.document;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageSet {

	private List<ImageContent> imageList;
	private PageThread pageThread;
	
	public ImageSet(PageThread pageThread) {
		this.pageThread = pageThread;
		this.imageList = new ArrayList<ImageContent>();
	}
	
	public List<ImageLob> getImageLobList() {
		List<ImageLob> list = new ArrayList<ImageLob>(this.imageList.size());
		for (ImageContent img : this.imageList) {
			list.add(new ImageLob(img));
		}
		return list;
	}
	
	public void addImageContent(ImageContent imageContent) {
		this.imageList.add(imageContent);
	}
	
	public List<ImageContent> getImageList() {
		return this.imageList;
	}
	
	public static class ImageLob {
		
		public int bitsPerComponent;
		public int byteCount;
		public String colorSpace;
		public String decode;
		public int height;
		public int width;
		public String suffix;
		public BufferedImage imageData;

		private Rectangle2D bBox;
		
		public ImageLob(ImageContent img) {
			this.bitsPerComponent = img.bitsPerComponent;
			this.byteCount = img.byteCount;
			this.colorSpace = img.colorSpace;
			this.decode = img.decode;
			this.height = img.height;
			this.width = img.width;
			this.suffix = img.suffix;
			this.imageData = img.imageData;
			
			this.bBox = img.getOutlineRect();
		}
		
		public Rectangle2D getBBox() {
			return this.bBox;
		}
	}
}