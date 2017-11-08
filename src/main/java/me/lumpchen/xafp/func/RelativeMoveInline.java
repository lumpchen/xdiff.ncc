package me.lumpchen.xafp.func;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class RelativeMoveInline extends Function {

	private int increment;
	
	public RelativeMoveInline() {
		this.type = PTX_RMI;
	}
	
	@Override
	void readFunction(AFPInputStream in) throws IOException {
		this.increment = in.readSBin(2);
		this.remain -= 2;
	}

	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		float rmi = (float) aeg.unit2Point(this.increment);
		graphics.translate(rmi, 0);
	}
	
	@Override
	public String getCommandString() {
		return "RMI";
	}
	
	@Override
	public String getCommandDesc() {
		return "Relative Move Inline";
	}
}
