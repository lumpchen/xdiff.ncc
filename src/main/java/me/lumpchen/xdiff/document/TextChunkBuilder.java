package me.lumpchen.xdiff.document;

import java.util.ArrayList;
import java.util.List;

public class TextChunkBuilder {

	private List<PageContent> contentList;
	private float pageWidth;
	private float pageHeight;
	
	private List<TextChunk> headerChunk;
	private List<TextChunk> footerChunk;
	private List<TextChunk> bodyChunk;
	
	public TextChunkBuilder(List<PageContent> contentList, float pageWidth, float pageHeight) {
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
		this.headerChunk = buildChunk(headerContentList);
		for (TextChunk tb : this.headerChunk) {
			System.out.println(tb.toString());
		}

		System.out.println("------- footer --------");
		this.footerChunk = buildChunk(footerContentList);
		for (TextChunk tb : this.footerChunk) {
			System.out.println(tb.toString());
		}

		System.out.println("------- body --------");
		this.bodyChunk = buildChunk(bodyContentList);
		for (TextChunk tb : this.bodyChunk) {
			System.out.println(tb.toString());
			System.out.println();
		}
	}
	
	public List<TextChunk> getHeaderBlock() {
		return headerChunk;
	}

	public List<TextChunk> getFooterBlock() {
		return footerChunk;
	}

	public List<TextChunk> getBodyBlock() {
		return bodyChunk;
	}

	private static List<TextChunk> buildChunk(List<TextContent> textContentList) {
		List<TextChunk> chunks = new ArrayList<TextChunk>();
		TextChunk chunk = new TextChunk();
		for (TextContent content : textContentList) {
			int x = (int) Math.round(content.getX());
			int baseline = (int) Math.round(content.getBaseline());

			if (chunk.getContentList().isEmpty()) {
				chunk.getContentList().add(content);
			} else {
				TextContent last = chunk.getContentList().get(chunk.getContentList().size() - 1);
				if (content.getGraphicsStateDesc().equals(last.getGraphicsStateDesc())) {
					chunk.getContentList().add(content);
				} else {
					chunks.add(chunk);
					chunk = new TextChunk();
					chunk.getContentList().add(content);
				}
			}
		}
		if (!chunk.getContentList().isEmpty()) {
			chunks.add(chunk);
		}
		
		return chunks;
	}
}
