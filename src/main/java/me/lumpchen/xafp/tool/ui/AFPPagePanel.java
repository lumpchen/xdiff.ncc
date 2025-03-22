package me.lumpchen.xafp.tool.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import me.lumpchen.xafp.Page;
import me.lumpchen.xafp.PrintFile;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.AFPGraphics2D;
import me.lumpchen.xafp.render.ResourceManager;

public class AFPPagePanel extends JPanel implements PageCanvas {

	private static final long serialVersionUID = 1L;

	private PrintFile pf;
	private ResourceManager resourceManager;
	private Page page;

	private int zoom = 100; // zoom percent

	private int degree = 0;
	
	public AFPPagePanel() {
		super(true);
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setBackground(Color.GRAY);
		g2.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		if (this.page == null) {
			return;
		}
		
		double pw = page.getPageWidth();
		double ph = page.getPageHeight();
		
		double hRes = 96;
		double vRes = 96;
		double hScale = (hRes / 72) * (zoom / 100f);
		double vScale = (vRes / 72) * (zoom / 100f);
		
		// process rotation
        int widthPx = (int) Math.round(pw * hScale);
        int heightPx = (int) Math.round(ph * vScale);

       	Rectangle rect = ((JScrollPane) this.getParent().getParent()).getVisibleRect();
   		this.setPreferredSize(new Dimension(widthPx > rect.width ? widthPx : rect.width, 
    			heightPx > rect.height ? heightPx : rect.height));
       	
        if (widthPx < this.getWidth()) {
        	g2.translate((this.getWidth() - widthPx) / 2, 0);
        }
        if (heightPx < this.getHeight()) {
        	g2.translate(0, (this.getHeight() - heightPx) / 2);
        }
        
   		this.getParent().revalidate();
   		
        int rotationAngle = 0;
        
        g2.setColor(Color.WHITE);
        if (rotationAngle == 90 || rotationAngle == 270) {
            g2.clearRect(0, 0, heightPx, widthPx);
            g2.fillRect(0, 0, heightPx, widthPx);
        } else {
        	 g2.clearRect(0, 0, widthPx, heightPx);
             g2.fillRect(0, 0, widthPx, heightPx);
        }
        
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.scale(hScale, vScale);
        
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
            g2.translate(translateX, translateY);
            g2.rotate((float) Math.toRadians(rotationAngle));
        }
        
        AFPGraphics graphics = new AFPGraphics2D(g2, (float) pw, (float) ph);
        page.render(graphics, this.resourceManager);
	}

	public void setZoom(int zoom) {
		if (this.zoom == zoom) {
			return;
		}
		this.zoom = zoom;
		this.repaint();
//		this.getParent().revalidate();
	}
	
	public void setRotation(int degree) {
		if (this.degree == degree) {
			return;
		}
		this.degree = degree;
		this.repaint();
//		this.getParent().revalidate();
	}
	
	@Override
	public void updatePage(PrintFile pf, ResourceManager resourceManager, int docIndex, int pageIndex) {
		this.pf = pf;
		this.resourceManager = resourceManager;
		Page page = this.pf.getDocuments().get(docIndex).getPageList().get(pageIndex);
		if (page == this.page) {
			return;
		}
		this.page = page;
		this.repaint();
		this.getParent().revalidate();
	}
	
	public void closeFile() {
		this.page = null;
		this.repaint();
	}
}
