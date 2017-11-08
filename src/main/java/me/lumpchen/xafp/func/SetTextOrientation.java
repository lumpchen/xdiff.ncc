package me.lumpchen.xafp.func;

import java.io.IOException;

import me.lumpchen.xafp.AFPConst;
import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class SetTextOrientation extends Function {

	private int IORNTION;
	private int BORNTION;
	
	SetTextOrientation() {
		this.type = PTX_STO;
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		int degree = AFPConst.toDegree(this.IORNTION);
		graphics.setTextRotation(degree);
	}
	
	@Override
	void readFunction(AFPInputStream in) throws IOException {
		if (this.remain > 0) {
			this.IORNTION = in.readCode(2);
			this.remain -= 2;
		}
		if (remain > 0) {
			this.BORNTION = in.readCode(2);
			this.remain -= 2;
		}
	}
	
	@Override
	public String getCommandString() {
		return "STO";
	}
	
	@Override
	public String getCommandDesc() {
		return "Set Text Orientation";
	}
}