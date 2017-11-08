package me.lumpchen.xafp;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.Identifier.Tag;

public abstract class AFPObject {

	protected StructureField structField;
	
	public AFPObject(StructureField structField) {
		this.structField = structField;
	}
	
	public byte[] getStructureData() {
		return this.structField.getData();
	}
	
	public Tag getStructureTag() {
		return this.structField.getStructureTag();
	}
	
}
