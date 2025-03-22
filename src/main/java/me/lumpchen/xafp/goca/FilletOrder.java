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

public class FilletOrder extends DrawingOrder {

	private Position position;
	private int LENGTH;

	private List<int[]> points;

	public FilletOrder(Position position) {
		this.position = position;
	}

	public String toString() {
		if (position == Position.Current) {
			return "Fillet at Current Position (GCFLT) Order";
		} else {
			return "Fillet at Given Position (GFLT) Order";
		}
	}

	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		if (this.points == null || this.points.size() <= 0) {
			return;
		}
		GeneralPath path = graphics.getCurrentPath();
		if (path == null) {
			throw new AFPException("GeneralPath not construct yet.");
		}

		int[] p = this.points.get(0);
		double x = aeg.unit2Point(p[0]);
		double y = aeg.unit2Point(p[1]);
		int begin = 0;
		if (position == Position.Given) {
			path.moveTo(x, y);
			begin = 1;
		}

		double ctrl_1_x = 0;
		double ctrl_1_y = 0;
		double ctrl_2_x = 0;
		double ctrl_2_y = 0;
		double ctrl_3_x = 0;
		double ctrl_3_y = 0;
		for (int i = begin; i < this.points.size(); i++) {
			p = this.points.get(i);
			ctrl_1_x = aeg.unit2Point(p[0]);
			ctrl_1_y = aeg.unit2Point(p[1]);
			
			p = this.points.get(++i);
			ctrl_2_x = aeg.unit2Point(p[0]);
			ctrl_2_y = aeg.unit2Point(p[1]);
			
			p = this.points.get(++i);
			ctrl_3_x = aeg.unit2Point(p[0]);
			ctrl_3_y = aeg.unit2Point(p[1]);
			path.curveTo(ctrl_1_x, ctrl_1_y, ctrl_2_x, ctrl_2_y, ctrl_3_x, ctrl_3_y);
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
			point[0] = in.readSBin(2);
			point[1] = in.readSBin(2);
			this.points.add(point);
			read += 4;
		}
	}

}
