package me.lumpchen.xafp.goca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class SetFractionalLineWidthOrder extends DrawingOrder {

	private int LENGTH;
	
	public SetFractionalLineWidthOrder() {
		
	}
	
	public String toString() {
		return "Set Fractional Line Width";
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		
	}

	@Override
	protected void readOperands(AFPInputStream in) throws IOException {
		this.LENGTH = in.readUBin(1);
		
		in.readBytes(this.LENGTH);
	}

}
