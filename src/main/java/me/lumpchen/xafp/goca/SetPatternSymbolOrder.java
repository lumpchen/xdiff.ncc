package me.lumpchen.xafp.goca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;

public class SetPatternSymbolOrder extends DrawingOrder {

	/**
	 * Value of pattern-symbol code point:
	 * X'00' Drawing default When the default pattern set is selected: (Pattern Set = X'00'):
	 * X'01'–X'08' Dotted patterns of decreasing density
	 * X'09' Vertical lines
	 * X'0A' Horizontal lines
	 * X'0B' Diagonal lines 1 (bottom-left to top-right)
	 * X'0C' Diagonal lines 2 (bottom-left to top-right)
	 * X'0D' Diagonal lines 1 (top-left to bottom-right)
	 * X'0E' Diagonal lines 2 (top-left to bottom-right)
	 * X'0F' No fill
	 * X'10' Solid fill
	 * X'40' Blank (processed the same as
	 * X'0F', no fill)
	 * All other values Reserved
	 * */
	private int PATT;
	
	public SetPatternSymbolOrder() {
		
	}
	
	public String toString() {
		return "Set Pattern Symbol";
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		if (this.PATT == 0x10) {
		}
	}

	@Override
	protected void readOperands(AFPInputStream in) throws IOException {
		this.PATT = in.readCode();
	}

}
