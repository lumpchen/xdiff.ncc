package me.lumpchen.xafp;

import me.lumpchen.xafp.sf.StructureField;

public class ObjectContainerData extends AFPObject {

	private byte[] objCdat;
	
	public ObjectContainerData(StructureField structField) {
		super(structField);
		this.objCdat = this.structField.getData();
	}
	
	public byte[] getData() {
		return this.objCdat;
	}

}
