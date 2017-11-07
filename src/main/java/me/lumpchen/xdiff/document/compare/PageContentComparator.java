package me.lumpchen.xdiff.document.compare;

import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xdiff.PageDiffResult;
import me.lumpchen.xdiff.PDocDiffResult.PageInfo;
import me.lumpchen.xdiff.PageDiffResult.DiffContent;
import me.lumpchen.xdiff.document.PageContent;
import me.lumpchen.xdiff.document.PageThread;

public class PageContentComparator {

	private CompareSetting setting;
	private List<ContentComparator> comparatorQueue;

	public PageContentComparator() {
		this(new CompareSetting());
	}

	public PageContentComparator(CompareSetting setting) {
		this.setting = setting;
		this.comparatorQueue = new ArrayList<ContentComparator>(4);
		
		this.comparatorQueue.add(new TextComparator(setting));
		
		if (this.setting.enableCompareImage) {
			this.comparatorQueue.add(new ImageComparator(setting));			
		}

		if (this.setting.enableComparePath) {
			this.comparatorQueue.add(new GraphicsComparator(setting));
		}

		if (this.setting.enableCompareAnnots) {
			this.comparatorQueue.add(new AnnotComparator(setting));
		}
	}

	public PageDiffResult compare(List<PageContent> basePageContents, PageInfo basePageInfo, List<PageContent> testPageContents, PageInfo testPageInfo) {
		PageThread basePageThread = new PageThread(basePageContents, basePageInfo, this.setting);
		PageThread testPageThread = new PageThread(testPageContents, testPageInfo, this.setting);

		PageDiffResult result = new PageDiffResult();
		
		DiffContent diff = new DiffContent(DiffContent.Category.Page);
		if (!this.comparePage(basePageInfo, testPageInfo, diff)) {
			result.append(diff);
		}

		for (ContentComparator comparator : this.comparatorQueue) {
			DiffContent[] diffs = comparator.compare(basePageThread, testPageThread);
			result.append(diffs);
		}

		return result;
	}
	
	private boolean comparePage(PageInfo basePageInfo, PageInfo testPageInfo, DiffContent diff) {
		boolean result = true;
		
		Float val_1 = basePageInfo == null ? null : basePageInfo.getWidth();
		Float val_2 = testPageInfo == null ? null : testPageInfo.getWidth();
		boolean equals = ContentComparator.compare(val_1, val_2);
		result &= equals;
		diff.putAttr(DiffContent.Key.Attr_Page_Width, equals, val_1 != null ? ContentComparator.roundM(val_1) : null, 
				val_2 != null ? ContentComparator.roundM(val_2) : null);
		
		val_1 = basePageInfo == null ? null : basePageInfo.getHeight();
		val_2 = testPageInfo == null ? null : testPageInfo.getHeight();
		equals = ContentComparator.compare(val_1, val_2);
		result &= equals;
		diff.putAttr(DiffContent.Key.Attr_Page_Height, equals, val_1 != null ? ContentComparator.roundM(val_1) : null, 
				val_2 != null ? ContentComparator.roundM(val_2) : null);
		
		Integer i_1 = basePageInfo == null ? null : basePageInfo.getRotation();
		Integer i_2 = testPageInfo == null ? null : testPageInfo.getRotation();
		equals = ContentComparator.compare(i_1, i_2);
		result &= equals;
		diff.putAttr(DiffContent.Key.Attr_Page_Rotatoin, equals, i_1, i_2);
		
		return result;
	}

}
