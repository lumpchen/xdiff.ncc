package me.lumpchen.xafp.goca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class BeginAreaOrder extends DrawingOrder {
	
	/**
	 * B'0' Do not draw boundary lines 
	 * B'1' Draw boundary lines
	 * */
	private boolean drawBoundary;
	
	public BeginAreaOrder() {
	}
	
	public String toString() {
		return "Begin Area";
	}

	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		graphics.beginPath(this.drawBoundary, true);
	}

	@Override
	protected void readOperands(AFPInputStream in) throws IOException {
		int op = in.read();
		int bit1 = (op >> 1) & 1;
		this.drawBoundary = (bit1 == 0x01);
	}
}

