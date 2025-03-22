package me.lumpchen.xafp.func;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class RelativeMoveBaseline extends Function {

	private int INCRMENT;
	
	public RelativeMoveBaseline() {
		this.type = PTX_SEC;
	}
	
	@Override
	void readFunction(AFPInputStream in) throws IOException {
		this.INCRMENT = in.readSBin(2);
		this.remain -= 2;
	}

	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		float rmb = (float) aeg.unit2Point(this.INCRMENT);
		graphics.translate(rmb, 0);
	}
	
	@Override
	public String getCommandString() {
		return "RMB";
	}
	
	@Override
	public String getCommandDesc() {
		return "Relative Move Baseline";
	}

}