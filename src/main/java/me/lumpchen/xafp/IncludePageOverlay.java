package me.lumpchen.xafp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.AFPGraphics2D;
import me.lumpchen.xafp.render.Renderable;
import me.lumpchen.xafp.render.ResourceManager;
import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class IncludePageOverlay extends AFPObject implements Renderable {

	private byte[] OvlyName;
	private int XolOset;
	private int YolOset;
	private int OvlyOrent = -1;
	private List<Triplet> Triplets;
	
	public IncludePageOverlay(StructureField structField) throws IOException {
		super(structField);
		this.Triplets = new ArrayList<Triplet>();
		this.parseData(this.structField.getData());
	}
	
	public void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			this.OvlyName = in.readBytes(8);
			this.XolOset = in.readSBin(3);
			this.YolOset = in.readSBin(3);
			
			if (in.remain() > 0) {
				this.OvlyOrent = in.readCode(2);				
			}
			
			while (in.remain() > 0) {
				// read triplets
				Triplet triplet = Triplet.readTriple(in);
				this.Triplets.add(triplet);
			}
		} finally {
			in.close();
		}
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		String olyName = AFPConst.ebcdic2Ascii(this.getOvlyName());
		Overlay overlay = resourceManager.getOverlay(olyName);
		if (overlay == null) {
			return;
		}
		
		float x = (float) aeg.unit2Point(this.XolOset);
		float y = (float) aeg.unit2Point(this.YolOset);
		
		graphics.save();
		
		graphics.translate(x, y);
		int rotation = this.getRotation();
		if (rotation != 0) {
			graphics.rotate(Math.toRadians(rotation));
		}
		
		BufferedImage image = resourceManager.getRenderImage(olyName);
		if (image == null) {
			double hRes = overlay.gethRes();
			double vRes = overlay.gethRes();
			double hScale = hRes / 72;
			double vScale = vRes / 72;
			
			int widthPx = (int) Math.round(overlay.getWidth() * hScale);
	        int heightPx = (int) Math.round(overlay.getHeight() * vScale);
	        
	        image = new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g = image.createGraphics();
	        
	        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g.scale(hScale, vScale);
	        
	       	g.setBackground(Color.WHITE);
	        g.clearRect(0, 0, image.getWidth(), image.getHeight());
	        
	        AFPGraphics olyGraphics = new AFPGraphics2D(g, (float) overlay.getWidth(), (float) overlay.getHeight());
			overlay.render(aeg, olyGraphics, resourceManager);
			
			resourceManager.addRenderImage(olyName, image);
		}
		
		graphics.drawImage(AFPConst.makeTransprency(image), 0, 0, (float) overlay.getWidth(), (float) overlay.getHeight());
		graphics.restore();
	}
	
	private int getRotation() {
		return AFPConst.toDegree(this.OvlyOrent);
	}
	
	public byte[] getOvlyName() {
		return OvlyName;
	}
	
	public String getResName() {
		String resName = AFPConst.ebcdic2Ascii(this.getOvlyName());
		return resName;
	}

	public void setOvlyName(byte[] ovlyName) {
		OvlyName = ovlyName;
	}

	public int getXolOset() {
		return XolOset;
	}

	public void setXolOset(int xolOset) {
		XolOset = xolOset;
	}

	public int getYolOset() {
		return YolOset;
	}

	public void setYolOset(int yolOset) {
		YolOset = yolOset;
	}

	public int getOvlyOrent() {
		return OvlyOrent;
	}

	public void setOvlyOrent(int ovlyOrent) {
		OvlyOrent = ovlyOrent;
	}

	public List<Triplet> getTriplets() {
		return Triplets;
	}

	public void setTriplets(List<Triplet> triplets) {
		Triplets = triplets;
	}
	
}
