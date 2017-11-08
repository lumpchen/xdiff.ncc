package me.lumpchen.xafp.goca;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.AFPException;
import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class LineOrder extends DrawingOrder {

	private Position position;
	private int LENGTH;
	
	private List<int[]> points;
	
	public LineOrder(Position position) {
		this.position = position;
	}
	
	public String toString() {
		if (position == Position.Current) {
			return "Line at Current Position";
		} else {
			return "Line at Given Position";
		}
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		if (this.points == null || this.points.size() <= 0) {
			return;
		}
		GeneralPath path =  graphics.getCurrentPath();
		if (path == null) {
			throw new AFPException("GeneralPath not construct yet.");
		}
		
		int[] p = this.points.get(0);
		double x = aeg.unit2Point(p[0]);
		double y = aeg.unit2Point(p[1]);
		if (position == Position.Given) {
			path.moveTo(x, y);
		} else {
			path.lineTo(x, y);
		}
		for (int i = 1; i < this.points.size(); i++) {
			p = this.points.get(i);
			x = aeg.unit2Point(p[0]);
			y = aeg.unit2Point(p[1]);
			path.lineTo(x, y);
		}
	}

	@Override
	protected void readOperands(AFPInputStream in) throws IOException {
		this.LENGTH = in.readUBin(1);
		int read = 0;
		
		this.points = new ArrayList<int[]>();
		while (true) {
			if (read >= this.LENGTH) {
				break;
			}
			int[] point = new int[2];
			point[0] = Math.abs(in.readSBin(2));
			point[1] = Math.abs(in.readSBin(2));
			this.points.add(point);
			read += 4;
		}
	}

}
	