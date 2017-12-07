package me.lumpchen.xdiff.document.compare;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompareSetting {

	public boolean enableCompareImage = true;
	public boolean enableComparePath = true;
	public boolean enableMergePath = false;
	public boolean enableCompareAnnots = true;
	public boolean enableTextPositionCompare = true;
	
	public boolean enableImageAppearanceCompare = false;
	public boolean enableAnnotAppearanceCompare = true;
	
	public boolean disableLineBreaking = false;
	
	public float toleranceOfHorPosition = 0.5f;
	public float toleranceOfVerPosition = 0.5f;
	
	public float toleranceOfRectWidth = 0.5f;
	public float toleranceOfRectHeight = 0.5f;
	
	public boolean ignoreZeroSizeArea = false;
	
	public float scaleOfSpaceingBetweenWords = 0.5f;
	
	public Map<String, List<Rectangle>> acceptanceDifferenceArea;
	
	public boolean ignoreInvisibleGraphics = false;
	
	public float globalSearchDistance = -1;
	
	public List<String[]> acceptanceDifferenceFontNameMap;
	
	public List<Character[]> acceptanceDifferenceCharMap;
	
	public boolean enablePathPixelCompare = false;
	
	public CompareSetting() {
	}
	
	public List<Rectangle> getAcceptanceDifferenceArea(int pageNo, boolean isBase) {
		if (this.acceptanceDifferenceArea == null) {
			return new ArrayList<Rectangle>(0);
		}
		String key = pageNo + " " + (isBase ? "control" : "test");
		if (this.acceptanceDifferenceArea.containsKey(key)) {
			return this.acceptanceDifferenceArea.get(key);
		} else {
			key = "n" + " " + (isBase ? "control" : "test");
			if (this.acceptanceDifferenceArea.containsKey(key)) {
				return this.acceptanceDifferenceArea.get(key);
			}
		}
		return new ArrayList<Rectangle>(0);
	}
	
}
