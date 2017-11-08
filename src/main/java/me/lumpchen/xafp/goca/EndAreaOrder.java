package me.lumpchen.xafp.goca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class EndAreaOrder extends DrawingOrder {

	private int LENGTH;
	
	public EndAreaOrder() {
	}
	
	public String toString() {
		return "End Area";
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
//		graphics.endPath();
//		if (graphics.getCurrentPath() != null) {
//			graphics.draw(graphics.getCurrentPath());			
//		}
		graphics.endPath();
	}

	@Override
	protected void readOperands(AFPInputStream in) throws IOException {
		this.LENGTH = in.readUBin(1);
		if (this.LENGTH == 0) {
			return;
		}
		in.readBytes(this.LENGTH);
	}

}
 