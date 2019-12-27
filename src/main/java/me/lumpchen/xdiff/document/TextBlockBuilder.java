package me.lumpchen.xdiff.document;

import java.util.ArrayList;
import java.util.List;

public class TextBlockBuilder {

	private List<PageContent> contentList;
	private float pageWidth;
	private float pageHeight;
	
	private List<TextBlock> headerBlock;
	private List<TextBlock> footerBlock;
	private List<TextBlock> bodyBlock;
	
	public TextBlockBuilder(List<PageContent> contentList, float pageWidth, float pageHeight) {
		this.contentList = contentList;
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
	}
	
	public void build() {
		if (contentList == null || contentList.isEmpty()) {
			return;
		}
		
		List<TextContent> bodyContentList = new ArrayList<TextContent>();
		List<TextContent> headerContentList = new ArrayList<TextContent>();
		List<TextContent> footerContentList = new ArrayList<TextContent>();

		for (int i = 0; i < contentList.size(); i++) {
			PageContent content = contentList.get(i);

			if (content.getType() == PageContent.Type.Text) {
				TextContent textContent = (TextContent) content;
				
				if (pageHeight - content.getY() < 72) {
					headerContentList.add(textContent);
				} else if (content.getY() < 72) {
					footerContentList.add(textContent);
				} else {
					bodyContentList.add(textContent);
				}
			}
		}

		System.out.println("------- header --------");
		this.headerBlock = buildBlock(headerContentList);
		for (TextBlock tb : this.headerBlock) {
			System.out.println(tb.toString());
		}

		System.out.println("------- footer --------");
		this.footerBlock = buildBlock(footerContentList);
		for (TextBlock tb : this.footerBlock) {
			System.out.println(tb.toString());
		}

		System.out.println("------- body --------");
		this.bodyBlock = buildBlock(bodyContentList);
		for (TextBlock tb : this.bodyBlock) {
			System.out.println(tb.toString());
		}
	}
	
	public List<TextBlock> getHeaderBlock() {
		return headerBlock;
	}

	public List<TextBlock> getFooterBlock() {
		return footerBlock;
	}

	public List<TextBlock> getBodyBlock() {
		return bodyBlock;
	}

	private static List<TextBlock> buildBlock(List<TextContent> textContentList) {
		List<TextBlock> blocks = new ArrayList<TextBlock>();
		TextBlock block = new TextBlock();
		for (TextContent content : textContentList) {
			int x = (int) Math.round(content.getX());
			int baseline = (int) Math.round(content.getBaseline());

			if (block.getContentList().isEmpty()) {
				block.getContentList().add(content);
			} else {
				TextContent last = block.getContentList().get(block.getContentList().size() - 1);
				if (content.getGraphicsStateDesc().equals(last.getGraphicsStateDesc())) {
					block.getContentList().add(content);
				} else {
					blocks.add(block);
					block = new TextBlock();
					block.getContentList().add(content);
				}
			}
		}
		if (!block.getContentList().isEmpty()) {
			blocks.add(block);
		}
		
		return blocks;
	}
}
