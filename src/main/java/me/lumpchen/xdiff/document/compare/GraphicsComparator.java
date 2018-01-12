package me.lumpchen.xdiff.document.compare;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import me.lumpchen.xdiff.PDocDiffResult.PageInfo;
import me.lumpchen.xdiff.PageDiffResult.DiffContent;
import me.lumpchen.xdiff.document.GraphicsContent;
import me.lumpchen.xdiff.document.GraphicsSet;
import me.lumpchen.xdiff.document.GraphicsSet.GraphicsLob;
import me.lumpchen.xdiff.document.PageContent.ColorDesc;
import me.lumpchen.xdiff.document.PageContent.GraphicsStateDesc;
import me.lumpchen.xdiff.document.PageThread;
import me.lumpchen.xdiff.document.GraphicsContent.Line;

public class GraphicsComparator extends ContentComparator {

	private PageInfo basePageInfo;
	private PageInfo testPageInfo;
	
	public GraphicsComparator(CompareSetting setting) {
		super(setting);
	}

	List<Shape> ass = new ArrayList<Shape>();
	
	public DiffContent[] compare(GraphicsSet basePathSet, GraphicsSet testPathSet) {
		List<GraphicsLob> basePathList = basePathSet.getPathLobList();
		List<GraphicsLob> testPathList = testPathSet.getPathLobList();
		List<DiffContent> result = new ArrayList<DiffContent>();

		for (int i = 0; i < basePathList.size(); i++) {
			GraphicsLob basePath = basePathList.get(i);
			GraphicsLob testPath = this.findPathLob(basePath, testPathList);

			DiffContent diffContent = new DiffContent(DiffContent.Category.Path);
			if (!this.compare(basePath, testPath, diffContent)) {
				result.add(diffContent);
			}
			if (testPath != null) {
				testPathList.remove(testPath);
			}
		}

		// process remain path in test
		for (GraphicsLob path : testPathList) {
			DiffContent diffContent = new DiffContent(DiffContent.Category.Path);
			if (!this.compare(null, path, diffContent)) {
				result.add(diffContent);
			}
		}

		return result.toArray(new DiffContent[result.size()]);
	}

	private boolean compare(GraphicsLob basePath, GraphicsLob testPath, DiffContent entry) {
		Rectangle2D bbox_1 = basePath == null ? null : basePath.getBBox();
		Rectangle2D bbox_2 = testPath == null ? null : testPath.getBBox();
		
		GraphicsContent pathContent_1 = basePath == null ? null : basePath.getPathContent();
		GraphicsContent pathContent_2 = testPath == null ? null : testPath.getPathContent();
		
		entry.setBBox(bbox_1, bbox_2);
		if (this.setting.ignoreZeroSizeArea && isZeroSize(bbox_1) && isZeroSize(bbox_2)) {
			
			String op_1 = basePath == null ? null : basePath.getPaintOperator();
			String op_2 = testPath == null ? null : testPath.getPaintOperator();
			if ("Fill".equalsIgnoreCase(op_1) || "Fill".equalsIgnoreCase(op_2)) {
				return true;
			} else {
				Float f1 = pathContent_1 == null ? null : pathContent_1.getGraphicsStateDesc().lineWidth;
				Float f2 = pathContent_2 == null ? null : pathContent_2.getGraphicsStateDesc().lineWidth;
				if (f1 == null && f2 == null) {
					return true;
				}
				if ((f1 != null && f1.floatValue() != 0 && bbox_1 != null) || 
						(f2 != null && f2.floatValue() != 0 && bbox_2 != null)) {
					// should be a line, continue comparing
				} else {
					return true;
				}
			}
		}
		
		List<Rectangle2D> subBbox_1 = basePath == null ? null : basePath.getShapeBBox();
		List<Rectangle2D> subBbox_2 = testPath == null ? null : testPath.getShapeBBox();
		entry.setSubBBox(subBbox_1, subBbox_2);
		
		boolean result = true;
		String val_1, val_2;
		
		val_1 = basePath == null ? null : basePath.getPaintOperator();
		val_2 = testPath == null ? null : testPath.getPaintOperator();
		boolean equals = compare(val_1, val_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Painting_OP, equals, val_1, val_2);
		
		if ("Fill".equalsIgnoreCase(val_1) || "Fill".equalsIgnoreCase(val_2)) {
			if (this.setting.ignoreInvisibleGraphics) {
				if ((isInvisible(pathContent_1, true) && pathContent_2 == null)
						|| (isInvisible(pathContent_2, true) && pathContent_1 == null)) {
					return true;
				}
			}
			
			val_1 = pathContent_1 == null ? null : pathContent_1.getNonStrokingColorspace();
			val_2 = pathContent_2 == null ? null : pathContent_2.getNonStrokingColorspace();
			equals = compare(val_1, val_2);
			result &= equals;
			entry.putAttr(DiffContent.Key.Attr_Fill_Colorspace, equals, val_1, val_2);

			float[] color_1 = pathContent_1 == null ? null : pathContent_1.getNonStrokingColor();
			float[] color_2 = pathContent_2 == null ? null : pathContent_2.getNonStrokingColor();
			val_1 = pathContent_1 == null ? null : pathContent_1.getNonStrokingColorValue();
			val_2 = pathContent_2 == null ? null : pathContent_2.getNonStrokingColorValue();
			equals = compare(color_1, color_2);
			result &= equals;
			entry.putAttr(DiffContent.Key.Attr_Fill_Color, equals, val_1, val_2);
		} else {
			if (this.setting.ignoreInvisibleGraphics) {
				if ((isInvisible(pathContent_1, false) && pathContent_2 == null)
						|| (isInvisible(pathContent_2, false) && pathContent_1 == null)) {
					return true;
				}
			}
			
			Float f1 = pathContent_1 == null ? null : pathContent_1.getGraphicsStateDesc().lineWidth;
			Float f2 = pathContent_2 == null ? null : pathContent_2.getGraphicsStateDesc().lineWidth;
			equals = compare(f1, f2);
			result &= equals;
			entry.putAttr(DiffContent.Key.Attr_Line_Width, equals, f1 == null ? null : f1.toString(),
					f2 == null ? null : f2.toString());

			val_1 = pathContent_1 == null ? null : pathContent_1.getStrokingColorspace();
			val_2 = pathContent_2 == null ? null : pathContent_2.getStrokingColorspace();
			equals = compare(val_1, val_2);
			result &= equals;
			entry.putAttr(DiffContent.Key.Attr_Stroke_Colorspace, equals, val_1, val_2);

			float[] color_1 = pathContent_1 == null ? null : pathContent_1.getStrokingColor();
			float[] color_2 = pathContent_2 == null ? null : pathContent_2.getStrokingColor();
			val_1 = pathContent_1 == null ? null : pathContent_1.getStrokingColorValue();
			val_2 = pathContent_2 == null ? null : pathContent_2.getStrokingColorValue();
			equals = compare(color_1, color_2);
			result &= equals;
			entry.putAttr(DiffContent.Key.Attr_Stroke_Color, equals, val_1, val_2);
		}

		Rectangle2D leftTop_rect_1 = this.basePageInfo != null ? this.toPageLeftTop(bbox_1, this.basePageInfo.getHeight()) : null;
		Rectangle2D leftTop_rect_2 = this.testPageInfo != null ? this.toPageLeftTop(bbox_2, this.testPageInfo.getHeight()) : null;
		
		equals = compare(leftTop_rect_1, leftTop_rect_2, this.setting.toleranceOfHorPosition, this.setting.toleranceOfVerPosition, 
				this.setting.toleranceOfRectWidth, this.setting.toleranceOfRectHeight);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Position_size, equals, bbox_1, bbox_2);

		if (this.setting.enablePathPixelCompare) {
			List<Shape> shape_1 = basePath == null ? null : basePath.getPathContent().getOutlineShapeList();
			List<Shape> shape_2 = testPath == null ? null : testPath.getPathContent().getOutlineShapeList();
			if (shape_1 != null && shape_2 != null) {
				
				BufferedImage baseImage = this.basePageInfo.getImageData();
				BufferedImage testImage = this.testPageInfo.getImageData();
				float similarity = GPathComparator.compare(shape_1, shape_2, baseImage.getWidth(), baseImage.getHeight(), 
						testImage.getWidth(), testImage.getHeight());
				
				int percent = Math.round(similarity * 100);
				if (percent < 90) {
					equals = false;
				} else {
					equals = true;
				}
				entry.putAttr(DiffContent.Key.Attr_Path_Similarity_Ratio, equals, percent, percent);
				
				result &= equals;
			}
		}
		
		return result;
	}
	
	private static boolean isInvisible(GraphicsContent graphicsContent, boolean fill) {
		GraphicsStateDesc gdesc = graphicsContent == null ? null : graphicsContent.getGraphicsStateDesc();
		return isInvisible(gdesc == null ? null : fill ? gdesc.nonStrokingColor : gdesc.strokingColor);
	}
	
	private static boolean isInvisible(ColorDesc colorDesc) {
		if (colorDesc == null) {
			return true;
		}
		Color c = new Color(colorDesc.rgb);
		if (Color.WHITE.equals(c)) {
			return true;
		}
		
		return false;
	}
	
	private GraphicsLob findPathLob(GraphicsLob base, List<GraphicsLob> testPathList) {
		for (GraphicsLob test : testPathList) {
			GraphicsContent path_1 = base.getPathContent();
			GraphicsContent path_2 = test.getPathContent();
			if (this.comparePathContent(path_1, path_2)) {
				return test;
			}
		}
		return null;
	}

	private boolean comparePathContent(GraphicsContent path_1, GraphicsContent path_2) {
		if (path_1.isFill() != path_2.isFill()) {
			return false;
		}
		
		Line l1 = path_1.asLine();
		Line l2 = path_2.asLine();
		if (l1 != null && l2 != null) {
			if (l1.vertical == l2.vertical && compare(l1.x, l2.x, 2.0) 
					&& compare(l1.y, l2.y, 2.0)) {
				return true;
			}
		}
		
		Rectangle2D area_1 = this.toPageLeftTop(path_1.getOutlineRect(), this.basePageInfo.getHeight());
		Rectangle2D area_2 = this.toPageLeftTop(path_2.getOutlineRect(), this.testPageInfo.getHeight());
		if (this.equalsWithTolerance(area_1, area_2)) {
			return true;
		}
		
		return false;
	}

	private boolean equalsWithTolerance(Rectangle2D r1, Rectangle2D r2) {
		boolean equals = compare(r1, r2, this.setting.toleranceOfHorPosition, this.setting.toleranceOfVerPosition, 
				this.setting.toleranceOfRectWidth, this.setting.toleranceOfRectHeight);
		return equals;
	}
	
	private Rectangle2D toPageLeftTop(Rectangle2D rect, float pageHeight) {
		if (rect == null) {
			return null;
		}
		AffineTransform at = new AffineTransform(1, 0, 0, 1, 0, -pageHeight);
		Shape leftTop = at.createTransformedShape(rect);
		
		at = new AffineTransform(1, 0, 0, -1, 0, 0);
		leftTop = at.createTransformedShape(leftTop);
		return leftTop.getBounds2D();
	}
	
	@Override
	public DiffContent[] compare(PageThread basePageThread, PageThread testPageThread) {
		this.basePageInfo = basePageThread.getPageInfo();
		this.testPageInfo = testPageThread.getPageInfo();
		DiffContent[] diffs = this.compare(basePageThread.getPathSet(), testPageThread.getPathSet());
		return diffs;
	}
	
	static void show(List<Shape> shapes ) {
		try {
			BufferedImage img = ImageIO.read(new File("c:/temp/1.png"));
			Graphics2D g = (Graphics2D) img.createGraphics();
			
			float dash1[] = {10.0f};
			BasicStroke dashed = new BasicStroke(1.0f);
			g.setStroke(dashed);
			
			for (int i = 0; i < shapes.size(); i++) {
				GeneralPath p = (GeneralPath) shapes.get(i);
				g.setColor(Color.red);
				g.draw(p);
				g.setColor(Color.black);
//				g.fill(p);
			}
			
			ImageIO.write(img, "png", new File("c:/temp/1-1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
