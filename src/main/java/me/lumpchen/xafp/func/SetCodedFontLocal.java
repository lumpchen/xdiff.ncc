package me.lumpchen.xafp.func;

import java.io.IOException;

import me.lumpchen.xafp.AFPConst;
import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.MapCodedFontFormat2;
import me.lumpchen.xafp.MapDataResource;
import me.lumpchen.xafp.font.AFPFont;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;
import me.lumpchen.xafp.sf.triplet.X01Triplet;

public class SetCodedFontLocal extends Function {

	private int LID;
	
	public SetCodedFontLocal() {
		this.type = PTX_SCFL;
	}
	
	@Override
	void readFunction(AFPInputStream in) throws IOException {
		this.LID = in.readCode();
		this.remain -= 1;
	}
	
	@Override
	public String getCommandString() {
		return "SCFL";
	}
	
	@Override
	public String getCommandDesc() {
		return "Set Coded Font Local";
	}

	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		MapCodedFontFormat2.Attribute mcf = aeg.getMapCodedFont(this.LID);
		if (mcf != null) {
			AFPFont font = resourceManager.getFontManager().getFont(mcf.codePageName, mcf.characterSetName);
			graphics.setAFPFont(font, mcf.fontSize);
		} else {
			MapDataResource.Attribute mdr = aeg.getMapDataResource(this.LID);
			if (mdr != null) {
				AFPFont font = resourceManager.getFontManager().getFont(mdr);
				graphics.setAFPFont(font, mdr.fontSize);
				if (mdr.ccsid == X01Triplet.CCSID_UTF16) {
					aeg.setCharacterEncoding(ActiveEnvironmentGroup.CHARACTER_ENCODING_UNICODE16BE);
				}
			}
		}
	}
}




