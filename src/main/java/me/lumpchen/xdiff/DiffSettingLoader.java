package me.lumpchen.xdiff;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;

import me.lumpchen.xdiff.document.compare.CompareSetting;

public class DiffSettingLoader {

	private static Logger logger = Logger.getLogger(DiffSettingLoader.class.getName());

	private DiffSettingLoader() {
	};

	public static DiffSetting load(String fileName) throws Exception {
		if (fileName == null) {
			logger.info("Not found compare configuration file in " + fileName + ", using default setting.");
			return DiffSetting.getDefaultSetting();
		}

		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			properties.load(new FileInputStream(new File(fileName)));
		} catch (Exception e) {
			logger.log(Level.WARNING, "Cannot open config file: " + fileName, e);
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.log(Level.WARNING, "", e);
				}
		}

		DiffSetting setting = new DiffSetting();
		
		try {
			setting.resolution = getInteger(properties, "resolution", Math.round(setting.resolution));
			setting.previewImageFormat = getString(properties, "previewImageFormat", setting.previewImageFormat);
			setting.noReportOnSameResult = getBoolean(properties, "noReportOnSameResult", setting.noReportOnSameResult);
			setting.printReport = getBoolean(properties, "printReport", setting.printReport);
			setting.showDifferentPagesOnly = getBoolean(properties, "showDifferentPagesOnly", setting.showDifferentPagesOnly);

			setting.enableAFPTLEComparison = getBoolean(properties, "enableAFPTLEComparison", setting.enableAFPTLEComparison);
			setting.enableAFPNOPComparison = getBoolean(properties, "enableAFPNOPComparison", setting.enableAFPNOPComparison);
			
			CompareSetting compareSetting = new CompareSetting();
			setting.compSetting = compareSetting;
			compareSetting.enableCompareImage = getBoolean(properties, "enableCompareImage", compareSetting.enableCompareImage);
			compareSetting.enableComparePath = getBoolean(properties, "enableComparePath", compareSetting.enableComparePath);
			compareSetting.enableMergePath = getBoolean(properties, "enableMergePath", compareSetting.enableMergePath);
			compareSetting.enableCompareAnnots = getBoolean(properties, "enableCompareAnnots", compareSetting.enableCompareAnnots);
			compareSetting.enableTextPositionCompare = getBoolean(properties, "enableTextPositionCompare", compareSetting.enableTextPositionCompare);
			compareSetting.enableImageAppearanceCompare = getBoolean(properties, "enableImageAppearanceCompare", compareSetting.enableImageAppearanceCompare);
			compareSetting.enableAnnotAppearanceCompare = getBoolean(properties, "enableAnnotAppearanceCompare", compareSetting.enableAnnotAppearanceCompare);
			
			compareSetting.toleranceOfRectWidth = getFloat(properties, "toleranceOfRectWidth", compareSetting.toleranceOfRectWidth);
			compareSetting.toleranceOfRectHeight = getFloat(properties, "toleranceOfRectHeight", compareSetting.toleranceOfRectHeight);
			
			compareSetting.toleranceOfHorPosition = getFloat(properties, "toleranceOfHorPosition", compareSetting.toleranceOfHorPosition);
			compareSetting.toleranceOfVerPosition = getFloat(properties, "toleranceOfVerPosition", compareSetting.toleranceOfVerPosition);
			
			compareSetting.ignoreZeroSizeArea = getBoolean(properties, "ignoreZeroSizeArea", compareSetting.ignoreZeroSizeArea);
			compareSetting.ignoreInvisibleGraphics = getBoolean(properties, "ignoreInvisibleGraphics", compareSetting.ignoreInvisibleGraphics);
			
			compareSetting.scaleOfSpaceingBetweenWords = getFloat(properties, "scaleOfSpaceingBetweenWords", compareSetting.scaleOfSpaceingBetweenWords);
			
			compareSetting.disableLineBreaking = getBoolean(properties, "disableLineBreaking", compareSetting.disableLineBreaking);
			
			compareSetting.enablePathPixelCompare = getBoolean(properties, "enablePathPixelCompare", compareSetting.enablePathPixelCompare);

			String val = getString(properties, "acceptanceDifferenceFontNameMap", "");
			if (val == null || val.trim().isEmpty()) {
				compareSetting.acceptanceDifferenceFontNameMap = new ArrayList<String[]>(0);
			} else {
			    Matcher m = Pattern.compile("\\[.*?\\]").matcher(val);
			    while (m.find()) {
			    	String s = m.group().trim();
			    	if (s.length() <= 0) {
			    		continue;
			    	}
			    	String[] pair = s.substring(1, s.length() - 1).split(",");
			    	if (pair.length != 2) {
			    		logger.log(Level.WARNING, "Fail to load <acceptanceDifferenceFontNameMap> value: " + val);
			    		break;
			    	}
			    	if (pair[0].trim().isEmpty() || pair[1].trim().isEmpty()) {
			    		logger.log(Level.WARNING, "Fail to load <acceptanceDifferenceFontNameMap> value: " + val);
			    		break;
			    	}
			    	
			    	if (compareSetting.acceptanceDifferenceFontNameMap == null) {
			    		compareSetting.acceptanceDifferenceFontNameMap  = new ArrayList<String[]>();
			    	}
			    	compareSetting.acceptanceDifferenceFontNameMap.add(new String[]{pair[0].trim(), pair[1].trim()});
			    }
			}
			
			val = getString(properties, "acceptanceDifferenceCharMap", "");
			if (val == null || val.trim().isEmpty()) {
				compareSetting.acceptanceDifferenceCharMap = new ArrayList<Character[]>(0);
			} else {
			    Matcher m = Pattern.compile("\\[.*?\\]").matcher(val);
			    while (m.find()) {
			    	String s = m.group().trim();
			    	if (s.length() <= 0) {
			    		continue;
			    	}
			    	String[] pair = s.substring(1, s.length() - 1).split(",");
			    	if (pair.length != 2) {
			    		logger.log(Level.WARNING, "Fail to load <acceptanceDifferenceCharMap> value: " + val);
			    		break;
			    	}
			    	if (pair[0].trim().isEmpty() || pair[1].trim().isEmpty()) {
			    		logger.log(Level.WARNING, "Fail to load <acceptanceDifferenceCharMap> value: " + val);
			    		break;
			    	}
			    	
			    	if (compareSetting.acceptanceDifferenceCharMap == null) {
			    		compareSetting.acceptanceDifferenceCharMap  = new ArrayList<Character[]>();
			    	}
			    	char c1 = (char) Integer.parseInt(pair[0].trim(), 16);
			    	char c2 = (char) Integer.parseInt(pair[1].trim(), 16);
			    	compareSetting.acceptanceDifferenceCharMap.add(new Character[]{c1, c2});
			    }
			}
			
			val = getString(properties, "acceptanceDifferenceArea", "");
			if (val == null || val.trim().isEmpty()) {
				compareSetting.acceptanceDifferenceArea = new HashMap<String, List<Rectangle>>(0);
			} else {
			    Matcher m = Pattern.compile("\\{[0-9a-z, \\[\\]]+\\}").matcher(val);
			    while (m.find()) {
			    	String s = m.group();
			    	s = s.trim().substring(1, s.length() - 1);
			    	String[] entry = s.trim().split(",");
			    	String key = entry[0].trim();
			    	
			    	String s2 = entry[1].trim();
			    	String[] r = s2.substring(1, s2.length() - 1).split(" ");
					if (r.length != 4) {
						logger.log(Level.WARNING, "Fail to load <acceptanceDifferenceArea> value in: " + fileName);
					} else {
						int left = Integer.parseInt(r[0]);
						int top = Integer.parseInt(r[1]);
						int w = Integer.parseInt(r[2]);
						int h = Integer.parseInt(r[3]);
						if (compareSetting.acceptanceDifferenceArea == null) {
							compareSetting.acceptanceDifferenceArea = new HashMap<String, List<Rectangle>>();
						}
						if (!compareSetting.acceptanceDifferenceArea.containsKey(key)) {
							List<Rectangle> list = new ArrayList<Rectangle>();
							compareSetting.acceptanceDifferenceArea.put(key, list);
						}
						compareSetting.acceptanceDifferenceArea.get(key).add(new Rectangle(left, top, w, h));
					}
			    }
			}
			// GhostScript configure
			setting.GS_Path = getString(properties, "GS_Path", setting.GS_Path);
			setting.GS_PS2PDF_Para = getString(properties, "GS_PS2PDF_Para", setting.GS_PS2PDF_Para);
			
			setting.diffBitmapBackground = getString(properties, "diffBitmapBackground", setting.diffBitmapBackground);
			
			setting.ignorePageBlankArea = getBoolean(properties, "ignorePageBlankArea", setting.ignorePageBlankArea);
			
			setting.useTwelvemonkeysImageIOProvider = getBoolean(properties, "useTwelvemonkeysImageIOProvider", true);
			
			val = getString(properties, "symbolFonts", "");
			if (val != null) {
				String[] entries = val.trim().split(",");
				for (String name : entries) {
					if (compareSetting.symbolFontList == null) {
						compareSetting.symbolFontList = new ArrayList<String>();
					}
					if (name.trim().length() > 0) {
						compareSetting.symbolFontList.add(name.trim());						
					}
				}
			}
			
			IIORegistry registry = IIORegistry.getDefaultInstance();
			ImageReaderSpi twelvemonkeysProviderJPEG = registry.getServiceProviderByClass(com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReaderSpi.class);
			ImageReaderSpi sunProviderJPEG = registry.getServiceProviderByClass(com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi.class);
			if (sunProviderJPEG != null && twelvemonkeysProviderJPEG != null) {
				if (setting.useTwelvemonkeysImageIOProvider) {
					registry.setOrdering(ImageReaderSpi.class, twelvemonkeysProviderJPEG, sunProviderJPEG);
				} else {
					registry.setOrdering(ImageReaderSpi.class, sunProviderJPEG, twelvemonkeysProviderJPEG);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Fail to load configure file: " + fileName, e);
			throw e;
		}

		return setting;
	}

	static Integer getInteger(Properties properties, String key, int defaultValue) {
		try {
			String val = properties.getProperty(key);
			if (val != null) {
				return Integer.parseInt(val);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Config value error", e);
		}
		logger.log(Level.WARNING, "Config value error: " + key + ", using default value: " + defaultValue + ".");
		return defaultValue;
	}

	static Float getFloat(Properties properties, String key, float defaultValue) {
		try {
			String val = properties.getProperty(key);
			if (val != null) {
				return Float.parseFloat(val);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Config value error", e);
		}
		logger.log(Level.WARNING, "Config value error: " + key + ", using default value: " + defaultValue + ".");
		return defaultValue;
	}

	static Boolean getBoolean(Properties properties, String key, boolean defaultValue) {
		try {
			String val = properties.getProperty(key);
			if (val != null) {
				return Boolean.parseBoolean(val);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Config value error", e);
		}
		logger.log(Level.WARNING, "Config value error: " + key + ", using default value: " + defaultValue + ".");
		return defaultValue;
	}

	static String getString(Properties properties, String key, String defaultValue) {
		try {
			String val = properties.getProperty(key);
			if (val != null) {
				return val.trim();
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Config value error", e);
		}
		logger.log(Level.WARNING, "Config value error: " + key + ", using default value: " + defaultValue + ".");
		return defaultValue;
	}

	private static <T> T lookupProviderByName(final ServiceRegistry registry, final Class providerClass) {
		return (T) registry.getServiceProviderByClass(providerClass);
	}
}
