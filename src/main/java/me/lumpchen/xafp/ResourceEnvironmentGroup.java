package me.lumpchen.xafp;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.Identifier.Tag;

public class ResourceEnvironmentGroup extends AFPContainer {
	
	public ResourceEnvironmentGroup(StructureField structField) {
		super(structField);
		this.structField = structField;
		if (this.structField != null) {
			this.nameStr = this.structField.getNameStr();
		}
	}
	
	@Override
	public void collect() {
		
	}
	
	@Override
	public boolean isBegin() {
		if (Tag.BSG == this.structField.getStructureTag()) {
			return true;
		} else if (Tag.ESG == this.structField.getStructureTag()) {
			return false;
		}
		return false;
	}
}
