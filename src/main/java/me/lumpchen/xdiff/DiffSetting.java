package me.lumpchen.xdiff;

import me.lumpchen.xdiff.document.compare.CompareSetting;

public class DiffSetting {

	public float resolution = 96;
	public String previewImageFormat = "png";
	public boolean noReportOnSameResult = true;
	public boolean printReport = false;
	public boolean showDifferentPagesOnly = false;
	public boolean hiddenFileAbsolutePath = false;
	
	public CompareSetting compSetting;
	
	public String GS_Path;
	public String GS_PS2PDF_Para;
	
	public String diffBitmapBackground = "black";
	public String diffBitmapPreviewMode = "Mixed";
	
	public boolean ignorePageBlankArea = false;
	
	public boolean useTwelvemonkeysImageIOProvider = true;
	
	public int fromPage = -1;
	public int toPage = -1;
	
	public boolean enableAFPTLEComparison = false;
	public boolean enableAFPNOPComparison = false;
	
	public ProgressListener progressListener;
	
	public static final DiffSetting getDefaultSetting() {
		DiffSetting setting = new DiffSetting();
		
		setting.resolution = 96;
		setting.previewImageFormat = "png";
		setting.noReportOnSameResult = true;
		setting.printReport = false;
		setting.showDifferentPagesOnly = false;
		
		setting.compSetting = new CompareSetting();
		
		setting.diffBitmapBackground = "black";
		
		setting.fromPage = -1;
		setting.toPage = -1;
		
		return setting;
	}
}
