package me.lumpchen.xafp.func;

import java.io.IOException;

import me.lumpchen.xafp.AFPConst;
import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.ResourceManager;
import me.lumpchen.xafp.render.StructuredAFPGraphics;

public class TransparentData extends Function {

	private byte[] TRNDATA;
	
	public TransparentData() {
		this.type = PTX_TRN;
	}

	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		if (graphics instanceof StructuredAFPGraphics) {
			((StructuredAFPGraphics) graphics).beginText();
		}
		char[] str = null;
		if (aeg.getCharacterEncoding() == ActiveEnvironmentGroup.CHARACTER_ENCODING_SIGNGLE_BYTE) {
			str = new char[this.TRNDATA.length];
			for (int i = 0; i < this.TRNDATA.length; i++) {
				str[i] = (char) (this.TRNDATA[i] & 0xFF);
			}
		} else if (aeg.getCharacterEncoding() == ActiveEnvironmentGroup.CHARACTER_ENCODING_UNICODE16BE) {
			String s = AFPConst.toUincode16BEString(this.TRNDATA);
			str = s.toCharArray();
		}
		graphics.drawString(str, 0, 0);
		
		if (graphics instanceof StructuredAFPGraphics) {
			((StructuredAFPGraphics) graphics).endText();
		}
	}
	
	@Override
	void readFunction(AFPInputStream in) throws IOException {
		if (this.remain > 0) {
			this.TRNDATA = in.readBytes(this.remain);
			this.remain = 0;
		}
	}
	
	@Override
	public String getCommandString() {
		return "TRN";
	}
	
	@Override
	public String getCommandDesc() {
		return "Transparent Data";
	}

}