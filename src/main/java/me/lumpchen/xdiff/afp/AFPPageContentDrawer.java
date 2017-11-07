package me.lumpchen.xdiff.afp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.Page;
import me.lumpchen.xafp.PrintFile;
import me.lumpchen.xafp.render.RenderParameter;
import me.lumpchen.xafp.render.ResourceManager;
import me.lumpchen.xdiff.PageContentDrawer;
import me.lumpchen.xdiff.document.PageContent;

public class AFPPageContentDrawer implements PageContentDrawer {
	
	private RenderParameter parameters;
	private ResourceManager resourceManager;
	
	private List<PageContent> contentList;
	
	public AFPPageContentDrawer(RenderParameter parameters, PrintFile printFile) {
		this.parameters = parameters;
		this.resourceManager = new ResourceManager(printFile.getResourceGroup());
		
		this.contentList = new ArrayList<PageContent>();
	}
	
	public BufferedImage renderPage(Page page) {
		double pw = page.getPageWidth();
		double ph = page.getPageHeight();
		double hRes = 96;
		double vRes = 96;
		if (this.parameters != null) {
			if (this.parameters.usePageResolution) {
				hRes = page.getHorResolution();
				vRes = page.getVerResolution();
			} else {
				hRes = this.parameters.resolution;
				vRes = this.parameters.resolution;
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
        
        AFPContentGraphics2D graphics = new AFPContentGraphics2D(g, (float) pw, (float) ph);
        page.render(graphics, this.resourceManager);
        this.contentList = graphics.getContentList();
        return image;
	}
	
	@Override
    public List<PageContent> getPageContentList() {
		return this.contentList;
	}
}
