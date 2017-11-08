package me.lumpchen.xafp.goca;

import java.io.IOException;

import me.lumpchen.xafp.AFPColor;
import me.lumpchen.xafp.AFPConst.ColorSpace;
import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class SetProcessColorOrder extends DrawingOrder {

	private int LENGTH;
	
	private ColorSpace colorSpace;
	private int[] compenents;
	
	public SetProcessColorOrder() {
		
	}
	
	public String toString() {
		return "Set Process Color";
	}
	
	public AFPColor getColor() {
		if (this.colorSpace == null || this.compenents == null) {
			return null;
		}
		return new AFPColor(this.colorSpace, this.compenents);
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		AFPColor color = this.getColor();
		if (color == null) {
			return;
		}
		graphics.setColor(color);
	}

	@Override
	protected void readOperands(AFPInputStream in) throws IOException {
		this.LENGTH = in.readUBin(1);
		int remain = this.LENGTH;
		
		in.read();
		remain -= 1;
		
		int COLSPCE = in.readCode();
		remain -= 1;
		switch (COLSPCE) {
		case 0x01:
			this.colorSpace = ColorSpace.RGB;
			break;
		case 0x04:
			this.colorSpace = ColorSpace.CMYK;
			break;
		case 0x06:
			this.colorSpace = ColorSpace.Highlight;
			break;
		case 0x08:
			this.colorSpace = ColorSpace.CIELAB;
			break;
		case 0x40:
			this.colorSpace = ColorSpace.OCA;
			break;
		}
		
		in.readBytes(4);
		remain -= 4;
		
		
		int[] COLSIZE = new int[] {
			in.readUBin(1), in.readUBin(1),
			in.readUBin(1), in.readUBin(1)
		};
		
		this.compenents = new int[this.colorSpace.getComponentLength()];
		int i = 0;
		while (i < this.compenents.length) {
			this.compenents[i] = in.readUBin(1);
			i++;
		}
		
	}

}
