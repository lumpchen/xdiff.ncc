package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.Renderable;
import me.lumpchen.xafp.render.ResourceManager;
import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class IncludePageSegment extends AFPObject implements Renderable {

	private byte[] PsegName;
	private int XpsOset;
	private int YpsOset;
	private List<Triplet> Triplets;
	
	public IncludePageSegment(StructureField structField) throws IOException {
		super(structField);
		this.Triplets = new ArrayList<Triplet>();
		this.parseData(this.structField.getData());
	}
	
	public void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			this.PsegName = in.readBytes(8);
			this.XpsOset = in.readSBin(3);
			this.YpsOset = in.readSBin(3);
			
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
		String name = AFPConst.ebcdic2Ascii(this.PsegName);
		PageSegment psg = resourceManager.getPageSegment(name);
		if (psg == null) {
			return;
		}
		
		float x = (float) aeg.unit2Point(this.XpsOset);
		float y = (float) aeg.unit2Point(this.YpsOset);
		
		graphics.save();
		graphics.translate(x, y);
		
		psg.render(aeg, graphics, resourceManager);
		graphics.restore();
	}
	
	public byte[] getPsegName() {
		return PsegName;
	}
	
	public String getResName() {
		String resName = AFPConst.ebcdic2Ascii(this.getPsegName());
		return resName;
	}

	public void setPsegName(byte[] psegName) {
		PsegName = psegName;
	}

	public int getXpsOset() {
		return XpsOset;
	}

	public void setXpsOset(int xpsOset) {
		XpsOset = xpsOset;
	}

	public int getYpsOset() {
		return YpsOset;
	}

	public void setYpsOset(int ypsOset) {
		YpsOset = ypsOset;
	}

	public List<Triplet> getTriplets() {
		return Triplets;
	}

	public void setTriplets(List<Triplet> triplets) {
		Triplets = triplets;
	}
	
}
