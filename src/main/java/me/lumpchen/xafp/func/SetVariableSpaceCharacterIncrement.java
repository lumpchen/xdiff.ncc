package me.lumpchen.xafp.func;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class SetVariableSpaceCharacterIncrement extends Function {

	private int INCRMENT;
	
	SetVariableSpaceCharacterIncrement() {
		this.type = PTX_SVI;
	}

	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		System.out.println(getCommandDesc() + " " + this.INCRMENT);
	}

	@Override
	void readFunction(AFPInputStream in) throws IOException {
		this.INCRMENT = in.readSBin(2);
		this.remain -= 2;
	}

	@Override
	public String getCommandString() {
		return "SVI";
	}

	@Override
	public String getCommandDesc() {
		return "Set Variable Space Character Increment";
	}
}
