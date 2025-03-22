package me.lumpchen.xafp.goca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.goca.DrawingOrder.Position;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class NoOperationOrder extends DrawingOrder {

	public NoOperationOrder() {
	}
	
	public String toString() {
		return "No Operation";
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		
	}

	@Override
	protected void readOperands(AFPInputStream in) throws IOException {
	}

}
