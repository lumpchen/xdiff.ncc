package me.lumpchen.xafp.func;

import java.awt.Color;
import java.io.IOException;

import me.lumpchen.xafp.AFPColor;
import me.lumpchen.xafp.AFPConst;
import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class SetTextColor extends Function {

	/**
	  	X'0000' or X'FF00' Device default
		X'0001' or X'FF01' Blue
		X'0002' or X'FF02' Red
		X'0003' or X'FF03' Pink/magenta
		X'0004' or X'FF04' Green
		X'0005' or X'FF05' Turquoise/cyan
		X'0006' or X'FF06' Yellow
		X'0008' Black
		X'0010' Brown
		X'FF07' Device default
		X'FF08' Reset color, also called color
		of medium
		X'FFFF' Default indicator
		All others Reserved
	 * */
	private int FRGCOLOR;
	
	private int PRECSION;
	
	public SetTextColor(boolean chained) {
		this.type = PTX_STC_C;
	}
	
	@Override
	void readFunction(AFPInputStream in) throws IOException {
		in.readCode(2);
		this.remain -= 2;
		if (this.remain > 0) {
			this.PRECSION = in.read();
			this.remain -= 1;
		}
	}
	
	@Override
	public String getCommandString() {
		return "STC";
	}
	
	@Override
	public String getCommandDesc() {
		return "Set Text Color";
	}

	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		Color awtColor = AFPConst.toAWTColor(this.FRGCOLOR);
		AFPColor c = new AFPColor(awtColor);
		graphics.setTextColor(c);
	}
}