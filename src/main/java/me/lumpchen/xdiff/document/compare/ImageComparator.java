package me.lumpchen.xdiff.document.compare;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xdiff.BitmapComparator;
import me.lumpchen.xdiff.BitmapComparator.Mode;
import me.lumpchen.xdiff.PageDiffResult.DiffContent;
import me.lumpchen.xdiff.document.ImageSet;
import me.lumpchen.xdiff.document.ImageSet.ImageLob;
import me.lumpchen.xdiff.document.PageThread;

public class ImageComparator extends ContentComparator {

	public ImageComparator(CompareSetting setting) {
		super(setting);
	}

	public DiffContent[] compare(ImageSet baseImageSet, ImageSet testImageSet) {
		List<ImageLob> baseImageList = baseImageSet.getImageLobList();
		List<ImageLob> testImageList = testImageSet.getImageLobList();
		List<DiffContent> result = new ArrayList<DiffContent>();
		
		for (int i = 0; i < baseImageList.size(); i++) {
			ImageLob baseImage = baseImageList.get(i);
			ImageLob testImage = this.findImageLob(baseImage, testImageList);
	
			DiffContent diffContent = new DiffContent(DiffContent.Category.Image);
			if (!this.diff(baseImage, testImage, diffContent)) {
				result.add(diffContent);
			}
			if (testImage != null) {
				testImageList.remove(testImage);
			}
		}
		
		// process remain images in test
		for (ImageLob image : testImageList) {
			DiffContent diffContent = new DiffContent(DiffContent.Category.Image);
			if (!this.diff(null, image, diffContent)) {
				result.add(diffContent);
			}
		}
		
		return result.toArray(new DiffContent[result.size()]);
	}
	
	private boolean diff(ImageLob baseImage, ImageLob testImage, DiffContent entry) {
		Rectangle2D baseRect = baseImage == null ? null : baseImage.getBBox();
		Rectangle2D testRect = testImage == null ? null : testImage.getBBox();
		
		if (this.setting.ignoreZeroSizeArea && isZeroSize(baseRect) && isZeroSize(testRect)) {
			return true;
		}
		
		entry.setBBox(baseRect, testRect);
		boolean result = true;
		
		Integer val_1 = baseImage == null ? null : baseImage.width;
		Integer val_2 = testImage == null ? null : testImage.width;
		boolean equals = compare(val_1, val_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Width, equals, val_1, val_2);
		
		val_1 = baseImage == null ? null : baseImage.height;
		val_2 = testImage == null ? null : testImage.height;
		equals = compare(val_1, val_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Height, equals, val_1, val_2);
		
		val_1 = baseImage == null ? null : baseImage.byteCount;
		val_2 = testImage == null ? null : testImage.byteCount;
		equals = compare(val_1, val_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Byte_count, equals, val_1, val_2);
		
		val_1 = baseImage == null ? null : baseImage.bitsPerComponent;
		val_2 = testImage == null ? null : testImage.bitsPerComponent;
		equals = compare(val_1, val_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Bits_Per_Component, equals, val_1, val_2);

		String s_1 = baseImage == null ? null : baseImage.suffix;
		String s_2 = testImage == null ? null : testImage.suffix;
		equals = compare(s_1, s_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Suffix, equals, s_1, s_2);
		
//		s_1 = baseImage == null ? null : baseImage.decode;
//		s_2 = testImage == null ? null : testImage.decode;
//		equals = compare(s_1, s_2);
//		result &= equals;
//		entry.putAttr(DiffContent.Key.Attr_Decode, equals, s_1, s_2);
		
		equals = compare(baseRect, testRect, this.setting.toleranceOfHorPosition, this.setting.toleranceOfVerPosition, 
				this.setting.toleranceOfRectWidth, this.setting.toleranceOfRectHeight);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Position_size, equals, baseRect, testRect);
		
		if (this.setting.enableImageAppearanceCompare && result) {
			BufferedImage img_1 = baseImage == null ? null : baseImage.imageData;
			BufferedImage img_2 = testImage == null ? null : testImage.imageData;
			
			BitmapComparator.CompareResult res = BitmapComparator.getComparator(Mode.XOR).compare(img_1, img_2);
			if (res.pecentage > 0 && res.diffImage != null) {
				result &= false;
				entry.putAttr(DiffContent.Key.Attr_Image_Appearance, false, "NA", "NA");
			}
		}
		
		return result;
	}
	
	private ImageLob findImageLob(ImageLob base, List<ImageLob> testImageList) {
		for (ImageLob test : testImageList) {
			if (base.getBBox() == null || test.getBBox() == null) {
				continue;
			}
			boolean equals = compare(base.getBBox(), test.getBBox(), this.setting.toleranceOfHorPosition, this.setting.toleranceOfVerPosition, 
					this.setting.toleranceOfRectWidth, this.setting.toleranceOfRectHeight);
			if (equals) {
				return test;
			}
		}
		
		for (ImageLob test : testImageList) {
			if (base.getBBox() == null || test.getBBox() == null) {
				continue;
			}
			if (base.getBBox().intersects(test.getBBox())) {
				return test;
			}
		}
		return null;
	}

	@Override
	public DiffContent[] compare(PageThread basePageThread, PageThread testPageThread) {
		DiffContent[] diffs = this.compare(basePageThread.getImageSet(), testPageThread.getImageSet());
		return diffs;
	}
}
