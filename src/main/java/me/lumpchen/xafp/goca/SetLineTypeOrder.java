package me.lumpchen.xafp.goca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class SetLineTypeOrder extends DrawingOrder {

	/**
	 * Value for line-type attribute:
	 * X'00' Drawing default; solid if none specified
	 * X'01' Dotted line
	 * X'02' Short dashed line
	 * X'03' Dash-dot line
	 * X'04' Double dotted line
	 * X'05' Long dashed line
	 * X'06' Dash-double-dot line
	 * X'07' Solid line
	 * X'08' Invisible line
	 * */
	private int LINETYPE;
	
	public SetLineTypeOrder() {
		
	}
	
	public String toString() {
		return "Set Line Type";
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		
	}

	@Override
	protected void readOperands(AFPInputStream in) throws IOException {
		this.LINETYPE = in.readCode();
	}

}
