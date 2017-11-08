package me.lumpchen.xafp.goca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class SetPatternSetOrder extends DrawingOrder {

	/**
	 * Local identifier (LCID) for the pattern set:
	 * X'00' Default pattern set
	 * X'01'–X'FE' Local identifier for the pattern set (not supported in AFP GOCA)
	 * X'FF' Default pattern set (not supported in AFP GOCA)
	 * */
	private int LCID;
	
	public SetPatternSetOrder() {
		
	}
	
	public String toString() {
		return "Set Pattern Set";
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		
	}

	@Override
	protected void readOperands(AFPInputStream in) throws IOException {
		this.LCID = in.readCode();
	}

}
