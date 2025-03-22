package me.lumpchen.xafp.goca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class BoxOrder extends DrawingOrder {
	
	private Position position;
	
	private int LENGTH;
	
	public BoxOrder(Position position) {
		this.position = position;
	}

	public String toString() {
		return "Box";
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		
	}

	@Override
	protected void readOperands(AFPInputStream in) throws IOException {
		if (position == Position.Given) {
			this.LENGTH = in.readUBin(1);
		} else {
			this.LENGTH = in.readUBin(1);
		}
	}
}
