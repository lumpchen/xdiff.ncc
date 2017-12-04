package me.lumpchen.xdiff.document.compare;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xdiff.PageDiffResult;
import me.lumpchen.xdiff.PDocDiffResult.PageInfo;
import me.lumpchen.xdiff.PageDiffResult.ContentAttr;
import me.lumpchen.xdiff.PageDiffResult.DiffContent;
import me.lumpchen.xdiff.document.AnnotSet;
import me.lumpchen.xdiff.document.PageContent;
import me.lumpchen.xdiff.document.PageThread;
import me.lumpchen.xdiff.document.AnnotSet.AnnotLob;

public class AnnotComparator extends ContentComparator {

	private PageInfo basePageInfo;
	private PageInfo testPageInfo;
	
	public AnnotComparator(CompareSetting setting) {
		super(setting);
	}
	
	public DiffContent[] compare(AnnotSet baseAnnotSet, AnnotSet testAnnotSet) {
		List<AnnotLob> baseAnnotList = baseAnnotSet.getAnnotLobList();
		List<AnnotLob> testAnnotList = testAnnotSet.getAnnotLobList();
		List<DiffContent> result = new ArrayList<DiffContent>();
		
		for (int i = 0; i < baseAnnotList.size(); i++) {
			AnnotLob baseAnnot = baseAnnotList.get(i);
			AnnotLob testAnnot = this.findAnnotLob(baseAnnot, testAnnotList);
			
			DiffContent diffContent = new DiffContent(DiffContent.Category.Annot);
			if (!this.compare(baseAnnot, testAnnot, diffContent)) {
				result.add(diffContent);
			}
			if (testAnnot != null) {
				testAnnotList.remove(testAnnot);
			}
		}
		
		// process remain annots in test
		for (AnnotLob annot : testAnnotList) {
			DiffContent diffContent = new DiffContent(DiffContent.Category.Annot);
			if (!this.compare(null, annot, diffContent)) {
				result.add(diffContent);
			}
		}
		
		return result.toArray(new DiffContent[result.size()]);
	}
	
	private boolean compare(AnnotLob baseAnnot, AnnotLob testAnnot, DiffContent entry) {
		Rectangle2D bbox_1 = baseAnnot == null ? null : baseAnnot.getBBox();
		Rectangle2D bbox_2 = testAnnot == null ? null : testAnnot.getBBox();
		entry.setBBox(bbox_1, bbox_2);
		boolean result = true;
		
		String s_1 = baseAnnot == null ? null : baseAnnot.fieldType;
		String s_2 = testAnnot == null ? null : testAnnot.fieldType;
		boolean equals = compare(s_1, s_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_FieldType, equals, s_1, s_2);
		
		s_1 = baseAnnot == null ? null : baseAnnot.subType;
		s_2 = testAnnot == null ? null : testAnnot.subType;
		equals = compare(s_1, s_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_SubType, equals, s_1, s_2);
		
		s_1 = baseAnnot == null ? null : baseAnnot.fieldName;
		s_2 = testAnnot == null ? null : testAnnot.fieldName;
		equals = compare(s_1, s_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_AnnotName, equals, s_1, s_2);
		
		s_1 = baseAnnot == null ? null : baseAnnot.alternateFieldName;
		s_2 = testAnnot == null ? null : testAnnot.alternateFieldName;
		equals = compare(s_1, s_2);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_AnnotContents, equals, s_1, s_2);
		
		Rectangle2D baseRect = baseAnnot == null ? null : baseAnnot.getBBox();
		Rectangle2D testRect = testAnnot == null ? null : testAnnot.getBBox();
		equals = compare(baseRect, testRect, this.setting.toleranceOfHorPosition, this.setting.toleranceOfVerPosition,
				this.setting.toleranceOfRectWidth, this.setting.toleranceOfRectHeight);
		result &= equals;
		entry.putAttr(DiffContent.Key.Attr_Annot_Rect, equals, baseRect, testRect);
		
		List<PageContent> baseAppearance = baseAnnot == null ? new ArrayList<PageContent>(0) : baseAnnot.getAppearance();
		List<PageContent> testAppearance = testAnnot == null ? new ArrayList<PageContent>(0) : testAnnot.getAppearance();
		PageContentComparator PageContentComparator = new PageContentComparator(this.setting);
		PageDiffResult appearanceDiffResult = PageContentComparator.compare(baseAppearance, this.basePageInfo, testAppearance, this.testPageInfo);
		if (appearanceDiffResult.countDiffs() > 0) {
			result &= false;
			entry.putAttr(DiffContent.Key.Attr_Annot_Appearance, false, "", "");
			
			List<DiffContent> contentList = appearanceDiffResult.getContentList();
			for (DiffContent content : contentList) {
				if (content.getCategory() == DiffContent.Category.Text) {
					List<ContentAttr> attrList = content.getAttrList();
					if (attrList.size() > 0) {
						entry.putAttr("|-----Text", false, "", "");
					}
					for (ContentAttr attr : attrList) {
						entry.putAttr("|----------" + attr.key, attr.equals, attr.baseVal, attr.testVal);
					}
				}
				
				if (content.getCategory() == DiffContent.Category.Image) {
					List<ContentAttr> attrList = content.getAttrList();
					if (attrList.size() > 0) {
						entry.putAttr("|-----Image", false, "", "");
					}
					for (ContentAttr attr : attrList) {
						entry.putAttr("|----------" + attr.key, attr.equals, attr.baseVal, attr.testVal);
					}
				}
				
				if (content.getCategory() == DiffContent.Category.Path) {
					List<ContentAttr> attrList = content.getAttrList();
					if (attrList.size() > 0) {
						entry.putAttr("|-----Path", false, "", "");
					}
					for (ContentAttr attr : attrList) {
						entry.putAttr("|----------" + attr.key, attr.equals, attr.baseVal, attr.testVal);
					}
				}
			}			
		}
		
		return result;
	}
	
	private AnnotLob findAnnotLob(AnnotLob base, List<AnnotLob> testAnnotList) {
		for (AnnotLob test : testAnnotList) {
			if (compare(base.fieldType, test.fieldType) 
					&& compare(base.subType, test.subType)
					&& base.getBBox().intersects(test.getBBox())
					) {
				return test;
			}
		}
		return null;
	}

	@Override
	public DiffContent[] compare(PageThread basePageThread, PageThread testPageThread) {
		this.basePageInfo = basePageThread.getPageInfo();
		this.testPageInfo = basePageThread.getPageInfo();
		DiffContent[] diffs = this.compare(basePageThread.getAnnotSet(), testPageThread.getAnnotSet());
		return diffs;
	}
}
