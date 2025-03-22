package me.lumpchen.xafp;

import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.Identifier.Tag;

public class PresentationTextObject extends AFPContainer {
	
	private List<PresentationTextData> ptxList;
	
	public PresentationTextObject(StructureField structField) {
		super(structField);
		this.structField = structField;
		if (this.structField != null) {
			this.nameStr = this.structField.getNameStr();
		}
		this.ptxList = new ArrayList<PresentationTextData>();
	}
	
	public PresentationTextData[] getPTX() {
		return this.ptxList.toArray(new PresentationTextData[this.ptxList.size()]);
	}
	
	@Override
	public boolean isBegin() {
		if (Tag.BPT == this.structField.getStructureTag()) {
			return true;
		} else if (Tag.EPT == this.structField.getStructureTag()) {
			return false;
		}
		return false;
	}

	@Override
	public void collect() {
		for (AFPObject child : this.children) {
			if (child instanceof PresentationTextData) {
				this.ptxList.add((PresentationTextData) child);
			}
		}
	}
}
