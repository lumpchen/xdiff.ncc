package me.lumpchen.xdiff.document;

import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xdiff.PDocDiffResult.PageInfo;
import me.lumpchen.xdiff.document.compare.CompareSetting;

public class PageThread {

	private PageInfo pageInfo;
	private List<PageContent> contentList;
	private TextThread textThread;
	private ImageSet imageSet;
	private GraphicsSet pathSet;
	private AnnotSet annotSet;
	
	private CompareSetting setting;

	public PageThread(List<PageContent> contentList, PageInfo pageInfo, CompareSetting setting) {
		this.pageInfo = pageInfo;
		this.setting = setting;
		this.contentList = contentList;
		this.analysis();
	}

	private void analysis() {
		this.textThread = new TextThread(this);
		this.imageSet = new ImageSet(this);
		this.pathSet = new GraphicsSet(this);
		this.annotSet = new AnnotSet(this);
		
		if (this.contentList == null || this.contentList.isEmpty()) {
			return;
		}
		
		List<TextContent> textContentList = new ArrayList<TextContent>();
		for (int i = 0; i < this.contentList.size(); i++) {
			PageContent content = this.contentList.get(i);

			if (content.getType() == PageContent.Type.Text) {
				TextContent textContent = (TextContent) content;
				textContentList.add(textContent);
			} else if (content.getType() == PageContent.Type.Path) {
				GraphicsContent path = (GraphicsContent) content;
				this.pathSet.addPathContent(path);
			} else if (content.getType() == PageContent.Type.Image) {
				ImageContent image = (ImageContent) content;
				this.imageSet.addImageContent(image);
			} else if (content.getType() == PageContent.Type.Annot) {
				AnnotContent annot = (AnnotContent) content;
				this.annotSet.addAnnotContent(annot);
			}
		}
		
		this.textThread.addTextContent(textContentList);
	}
	
	public TextThread getTextThread() {
		return this.textThread;
	}
	
	public ImageSet getImageSet() {
		return this.imageSet;
	}
	
	public GraphicsSet getPathSet() {
		return this.pathSet;
	}
	
	public AnnotSet getAnnotSet() {
		return this.annotSet;
	}
	
	public CompareSetting getCompareSetting() {
		return this.setting;
	}

	public PageInfo getPageInfo() {
		return this.pageInfo;
	}
}

