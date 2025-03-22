package me.lumpchen.xafp.tool.ui;

import me.lumpchen.xafp.PrintFile;
import me.lumpchen.xafp.render.ResourceManager;

public interface PageCanvas {

	public void updatePage(PrintFile pf, ResourceManager resourceManager, int docIndex, int pageIndex);
}
