package me.lumpchen.xafp.func;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class AbsoluteMoveInline extends Function {

	private int DSPLCMNT;
	
	AbsoluteMoveInline() {
		this.type = PTX_AMI;
	}
	
	@Override
	void readFunction(AFPInputStream in) throws IOException {
		this.DSPLCMNT = in.readSBin(2);
		this.remain -= 2;
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		float ami = (float) aeg.unit2Point(this.DSPLCMNT);
		graphics.setTextPosX(ami);
	}
	
	@Override
	public String getCommandString() {
		return "AMI";
	}
	
	@Override
	public String getCommandDesc() {
		return "Absolute Move Inline";
	}

}