package me.lumpchen.xafp.goca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.goca.DrawingOrder.Position;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class BeginImageOrder extends DrawingOrder {
	
	private Position position;
	
	private int LENGTH;
	private int XPOS = 0;
	private int YPOS = 0;
	
	private int FORMAT = 0;
	private int WIDTH;
	private int HEIGHT;
	
	public BeginImageOrder(Position position) {
		this.position = position;
	}

	public String toString() {
		if (position == Position.Current) {
			return "Begin Image at Current Position";
		} else {
			return "Begin Image at Given Position";
		}
	}

	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		
	}

	@Override
	protected void readOperands(AFPInputStream in) throws IOException {
		if (position == Position.Given) {
			this.LENGTH = in.readUBin(1);
			this.XPOS = in.readSBin(2);
			this.YPOS = in.readSBin(2);
			this.FORMAT = in.readCode();
			in.read();
			this.WIDTH = in.readUBin(2);
			this.HEIGHT = in.readUBin(2);
		} else {
			this.LENGTH = in.readUBin(1);
			this.FORMAT = in.readCode();
			in.read();
			this.WIDTH = in.readUBin(2);
			this.HEIGHT = in.readUBin(2);
		}
	}
}
