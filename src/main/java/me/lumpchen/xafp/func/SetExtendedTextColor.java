package me.lumpchen.xafp.func;

import java.io.IOException;

import me.lumpchen.xafp.AFPColor;
import me.lumpchen.xafp.AFPConst.ColorSpace;
import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class SetExtendedTextColor extends Function {

	private int COLSPCE;
	private int[] COLVALUE;
	private int COLSIZE1;
	private int COLSIZE2;
	private int COLSIZE3;
	private int COLSIZE4;
	
	private ColorSpace colorspace;
	
	public SetExtendedTextColor() {
		this.type = PTX_SEC;
	}

	public ColorSpace getColorSpace() {
		return this.colorspace;
	}
	
	@Override
	void readFunction(AFPInputStream in) throws IOException {
		in.read(); // reserved
		this.remain -= 1;
		this.COLSPCE = in.readCode();
		
		switch (this.COLSPCE) {
		case 0x01:
			this.colorspace = ColorSpace.RGB;
			break;
		case 0x04:
			this.colorspace = ColorSpace.CMYK;
			break;
		case 0x06:
			this.colorspace = ColorSpace.Highlight;
			break;
		case 0x08:
			this.colorspace = ColorSpace.CIELAB;
			break;
		case 0x40:
			this.colorspace = ColorSpace.OCA;
			break;
		default:
			throw new java.lang.IllegalArgumentException("Illegal colorspace id: " + this.COLSPCE);
		}
		
		this.remain -= 1;
		in.readBytes(9 - 6 + 1);
		this.remain -= (9 - 6 + 1);
		this.COLSIZE1 = in.readUBin(1);
		this.remain -= 1;
		this.COLSIZE2 = in.readUBin(1);
		this.remain -= 1;
		this.COLSIZE3 = in.readUBin(1);
		this.remain -= 1;
		this.COLSIZE4 = in.readUBin(1);
		this.remain -= 1;
		
		int componentLength = this.colorspace.getComponentLength();
		this.COLVALUE = new int[componentLength];
		for (int i = 0; i < componentLength; i++) {
			this.COLVALUE[i] = in.readUBin(1);
		}
		this.remain -= componentLength;
		
		if (this.remain > 0) {
			in.readBytes(this.remain);
		}
		this.remain = 0;
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		AFPColor c = new AFPColor(this.colorspace, this.COLVALUE);
		graphics.setTextColor(c);
	}
	
	@Override
	public String getCommandString() {
		return "SEC";
	}
	
	@Override
	public String getCommandDesc() {
		return "Set Extended Text Color";
	}
}