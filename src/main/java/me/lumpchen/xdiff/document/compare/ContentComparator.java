package me.lumpchen.xdiff.document.compare;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import me.lumpchen.xdiff.PageDiffResult.DiffContent;
import me.lumpchen.xdiff.document.PageThread;
import me.lumpchen.xdiff.document.PageContent.ColorDesc;
import me.lumpchen.xdiff.document.PageContent.GraphicsStateDesc;
import me.lumpchen.xdiff.document.PageContent.TextStateDesc;

public abstract class ContentComparator {

	CompareSetting setting;
	private static final float defaultTor = 0.1f;

	public ContentComparator(CompareSetting setting) {
		this.setting = setting;
	}

	public abstract DiffContent[] compare(PageThread basePageThread, PageThread testPageThread);

	protected static boolean compare(Float f1, Float f2) {
		if (f1 == null || f2 == null) {
			return false;
		}
		return Math.abs(f1 - f2) <= defaultTor;
	}
	
	protected static boolean compare(Double d1, Double d2) {
		return compare(d1, d2, defaultTor);
	}
	
	protected static boolean compare(Double d1, Double d2, double tor) {
		if (d1 == null || d2 == null) {
			return false;
		}
		return Math.abs(d1 - d2) <= tor;
	}

	protected static boolean compare(Integer f1, Integer f2) {
		if (f1 == null || f2 == null) {
			return false;
		}
		return f1.intValue() == f2.intValue();
	}
	
	protected static boolean compare(Rectangle2D r1, Rectangle2D r2) {
		return compare(r1, r2, defaultTor, defaultTor, defaultTor, defaultTor);
	}
	
	protected static boolean compare(Rectangle2D r1, Rectangle2D r2, double torX, double torY, double torW, double torH) {
		if (r1 == null || r2 == null) {
			return false;
		}
		
		boolean equal = compare(r1.getX(), r2.getX(), torX);
		equal &= compare(r1.getY(), r2.getY(), torY);
		equal &= compare(r1.getHeight(), r2.getHeight(), torH);
		equal &= compare(r1.getWidth(), r2.getWidth(), torW);
		return equal;
	}

	protected static boolean compare(GraphicsStateDesc gstate_1, GraphicsStateDesc gstate_2) {
		if (gstate_1 == null && gstate_2 == null) {
			return true;
		}
		if (gstate_1 != null && gstate_2 != null) {
			boolean b = compare(gstate_1.textState, gstate_2.textState);
			b &= compare(gstate_1.nonStrokingColor, gstate_2.nonStrokingColor);
			b &= compare(gstate_1.strokingColor, gstate_2.strokingColor);
			return b;
		}
		return false;
	}

	protected static boolean compare(TextStateDesc tstate_1, TextStateDesc tstate_2) {
		if (tstate_1 == null && tstate_2 == null) {
			return true;
		}
		if (tstate_1 != null && tstate_2 != null) {
			boolean b = tstate_1.characterSpacing == tstate_2.characterSpacing
					&& tstate_1.wordSpacing == tstate_2.wordSpacing
					&& tstate_1.horizontalScaling == tstate_2.horizontalScaling && tstate_1.leading == tstate_2.leading
					&& tstate_1.fontSize == tstate_2.fontSize && tstate_1.renderingMode == tstate_2.renderingMode
					&& tstate_1.rise == tstate_2.rise && tstate_1.knockout == tstate_2.knockout;
			b &= compare(tstate_1.fontName, tstate_2.fontName);
			return b;
		}
		return false;
	}

	protected static boolean compare(ColorDesc color_1, ColorDesc color_2) {
		if (color_1 == null && color_2 == null) {
			return true;
		}
		if (color_1 != null && color_2 != null) {
			boolean b = compare(color_1.patternName, color_2.patternName);
			b &= compare(color_1.colorSpace, color_2.colorSpace);
			b &= compare(color_1.components, color_2.components);
			return b;
		}
		return false;
	}

	protected static boolean compare(float[] arr1, float[] arr2) {
		if (arr1 == null && arr2 == null) {
			return true;
		}
		if (arr1 != null && arr2 != null) {
			if (arr1.length != arr2.length) {
				return false;
			}
			for (int i = 0; i < arr1.length; i++) {
				if (!compare(arr1[i], arr2[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	protected static boolean compare(String s1, String s2) {
		if (s1 != null) {
			return s1.equals(s2);
		}
		if (s2 != null) {
			return s2.equals(s1);
		}
		return true;
	}
	
	protected static boolean isZeroSize(Rectangle2D rect) {
		if (rect == null) {
			return true;
		}
		if (rect.getHeight() <= 0.1 || rect.getWidth() <= 0.1) {
			return true;
		}
		
		if (rect.getHeight() < 0.5 && rect.getWidth() < 0.5) {
			return true;
		}
		return false;
	}
	
	protected boolean compareFontName(String s1, String s2) {
		List<String[]> acceptanceDifferenceFontNameMap = this.setting.acceptanceDifferenceFontNameMap;
		boolean equal = compare(s1, s2);
		if (equal || acceptanceDifferenceFontNameMap == null || acceptanceDifferenceFontNameMap.isEmpty()) {
			return equal;
		}
		
		for (String[] pair : acceptanceDifferenceFontNameMap) {
			if ((compare(pair[0], s1) && compare(pair[1], s2))
					|| (compare(pair[1], s1) && compare(pair[2], s2))) {
				return true;
			}
		}
		
		return equal;
	}

	protected static double roundM(double d) {
		return Math.round(d * 1000) / 1000d;
	}

	public static String removeFontNameSuffix(String fontName) {
		if (fontName == null) {
			return null;
		}
		if (fontName.indexOf("+") > 0) {
			return fontName.substring(fontName.indexOf("+") + 1, fontName.length()).trim();
		}
		return fontName.trim();
	}

	protected BufferedImage diffImages(BufferedImage bim1, BufferedImage bim2) throws IOException {
		int minWidth = Math.min(bim1.getWidth(), bim2.getWidth());
		int minHeight = Math.min(bim1.getHeight(), bim2.getHeight());
		int maxWidth = Math.max(bim1.getWidth(), bim2.getWidth());
		int maxHeight = Math.max(bim1.getHeight(), bim2.getHeight());
		BufferedImage bim3 = null;
		if (minWidth != maxWidth || minHeight != maxHeight) {
			bim3 = createEmptyDiffImage(minWidth, minHeight, maxWidth, maxHeight);
		}
		for (int x = 0; x < minWidth; ++x) {
			for (int y = 0; y < minHeight; ++y) {
				int rgb1 = bim1.getRGB(x, y);
				int rgb2 = bim2.getRGB(x, y);
				if (rgb1 != rgb2
						// don't bother about differences of 1 color step
						&& (Math.abs((rgb1 & 0xFF) - (rgb2 & 0xFF)) > 1
								|| Math.abs(((rgb1 >> 8) & 0xFF) - ((rgb2 >> 8) & 0xFF)) > 1
								|| Math.abs(((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF)) > 1)) {
					if (bim3 == null) {
						bim3 = createEmptyDiffImage(minWidth, minHeight, maxWidth, maxHeight);
					}
					int r = Math.abs((rgb1 & 0xFF) - (rgb2 & 0xFF));
					int g = Math.abs((rgb1 & 0xFF00) - (rgb2 & 0xFF00));
					int b = Math.abs((rgb1 & 0xFF0000) - (rgb2 & 0xFF0000));
					bim3.setRGB(x, y, 0xFFFFFF - (r | g | b));
				} else {
					if (bim3 != null) {
						bim3.setRGB(x, y, Color.WHITE.getRGB());
					}
				}
			}
		}
		return bim3;
	}

	protected BufferedImage createEmptyDiffImage(int minWidth, int minHeight, int maxWidth, int maxHeight) {
		BufferedImage bim3 = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = bim3.getGraphics();
		if (minWidth != maxWidth || minHeight != maxHeight) {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, maxWidth, maxHeight);
		}
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, minWidth, minHeight);
		graphics.dispose();
		return bim3;
	}
	
	public static String getSymbolText(String text) {
		if (text == null || text.isEmpty()) {
			return "";
		}
		
		char[] cs = text.toCharArray();
		StringBuilder buf = new StringBuilder();
		buf.append("Symbol( ");
		for (Character c : cs) {
			buf.append("0x" + Integer.toHexString(c));
			buf.append(" ");
		}
		buf.append(")");
		return buf.toString();
	}
	
}
