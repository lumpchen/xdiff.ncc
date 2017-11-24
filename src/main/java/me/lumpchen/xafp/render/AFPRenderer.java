package me.lumpchen.xafp.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lumpchen.xafp.Document;
import me.lumpchen.xafp.Page;
import me.lumpchen.xafp.PrintFile;

public class AFPRenderer {

	private RenderParameter parameters;
	
	private List<Document> documents;
	private ResourceManager resourceManager;
	
	private Map<String, BufferedImage> pageImageMap;
	
	public AFPRenderer(RenderParameter parameters, PrintFile printFile) {
		this.parameters = parameters;
		this.pageImageMap = new HashMap<String, BufferedImage>();
		
		this.resourceManager = new ResourceManager(printFile.getResourceGroup());
		this.documents = printFile.getDocuments();
	}
	
	public BufferedImage getPageImage(int documentIndex, int pageIndex) {
		String key = this.getImageKey(documentIndex, pageIndex);
		if (this.pageImageMap.containsKey(key)) {
			return this.pageImageMap.get(key);
		}
		
		if (documentIndex < 0 || documentIndex >= this.documents.size()) {
			throw new IllegalArgumentException("Invalid Document Index: " + documentIndex);
		}
		if (pageIndex < 0 || pageIndex >= this.documents.get(documentIndex).getPageList().size()) {
			throw new IllegalArgumentException("Invalid Page Index: " + pageIndex);
		}
		
		Page page = this.documents.get(documentIndex).getPageList().get(pageIndex);
		BufferedImage pageImage = this.renderPage(page);
		
//		this.pageImageMap.put(key, pageImage);
		return pageImage;
	}
	
	private String getImageKey(int documentIndex, int pageIndex) {
		return documentIndex + "-" + pageIndex;
	}
	
	public BufferedImage renderPage(Page page) {
		BufferedImage image = renderPage(page, this.parameters, this.resourceManager);
		return image;
	}
	
	public static BufferedImage renderPage(Page page, RenderParameter parameters, ResourceManager resourceManager) {
		double pw = page.getPageWidth();
		double ph = page.getPageHeight();
		
		double hRes = 96;
		double vRes = 96;
		if (parameters != null) {
			if (parameters.usePageResolution) {
				hRes = page.getHorResolution();
				vRes = page.getVerResolution();
			} else {
				hRes = parameters.resolution;
				vRes = parameters.resolution;
			}
		}

		double hScale = hRes / 72;
		double vScale = vRes / 72;
		
		// process rotation
        int widthPx = (int) Math.round(pw * hScale);
        int heightPx = (int) Math.round(ph * vScale);
        int rotationAngle = 0;
        
        BufferedImage image;
        if (rotationAngle == 90 || rotationAngle == 270) {
            image = new BufferedImage(heightPx, widthPx, BufferedImage.TYPE_INT_RGB);
        } else {
            image = new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D g = image.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(hScale, vScale);
        
        if (rotationAngle != 0) {
            double translateX = 0;
            double translateY = 0;
            switch (rotationAngle) {
                case 90:
                    translateX = ph;
                    break;
                case 270:
                    translateY = pw;
                    break;
                case 180:
                    translateX = pw;
                    translateY = ph;
                    break;
            }
            g.translate(translateX, translateY);
            g.rotate((float) Math.toRadians(rotationAngle));
        }
        
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, image.getWidth(), image.getHeight());
        
        AFPGraphics graphics = new AFPGraphics2D(g, (float) pw, (float) ph);
        page.render(graphics, resourceManager);
        return image;
	}
	
}
