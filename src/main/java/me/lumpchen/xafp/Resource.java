package me.lumpchen.xafp;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.Identifier.Tag;
import me.lumpchen.xafp.sf.triplet.Triplet;
import me.lumpchen.xafp.sf.triplet.X21Triplet;

public class Resource extends AFPContainer {

	public enum Type {
		GOCA((byte) 0x03), BCOCA((byte) 0x05), IOCA((byte) 0x06), CHARACTER_SET((byte) 0x40), CODE_PAGE((byte) 0x41),
		CODED_FONT((byte) 0x42), OBJECT_CONTAINER((byte) 0x92), DOCUMENT((byte) 0xA8), PAGE_SEGMENT((byte) 0xFB), OVERLAY((byte) 0xFC), 
		PAGEDEF((byte) 0xFD), FORMDEF((byte) 0xFE);
		
		private byte id;
		private String typeStr;
		
		static Type instance(byte id) {
			Type type;
			switch(id) {
			case (byte) 0x03:
				type = GOCA;
				break;
			case (byte) 0x05:
				type = BCOCA;
				break;
			case (byte) 0x06:
				type = IOCA;
				break;
			case (byte) 0x40:
				type = CHARACTER_SET;
				break;
			case (byte) 0x41:
				type = CODE_PAGE;
				break;
			case (byte) 0x42:
				type = CODED_FONT;
				break;
			case (byte) 0x92:
				type = OBJECT_CONTAINER;
				break;
			case (byte) 0xA8:
				type = DOCUMENT;
				break;
			case (byte) 0xFB:
				type = PAGE_SEGMENT;
				break;
			case (byte) 0xFC:
				type = OVERLAY;
				break;
			case (byte) 0xFD:
				type = PAGEDEF;
				break;
			case (byte) 0xFE:
				type = FORMDEF;
				break;
			default:
				throw new java.lang.IllegalArgumentException("Invalid object type: " + id);
			}
			return type;
		}
		
		private Type(byte id) {
			switch(id) {
			case (byte) 0x03:
				typeStr = "GOCA_OBJECT";
				break;
			case (byte) 0x05:
				typeStr = "BCOCA_OBJECT";
				break;
			case (byte) 0x06:
				typeStr = "IOCA_OBJECT";
				break;
			case (byte) 0x40:
				typeStr = "CHARACTER_SET_OBJECT";
				break;
			case (byte) 0x41:
				typeStr = "CODE_PAGE_OBJECT";
				break;
			case (byte) 0x42:
				typeStr = "CODED_FONT_OBJECT";
				break;
			case (byte) 0x92:
				typeStr = "OBJECT_CONTAINER";
				break;
			case (byte) 0xA8:
				typeStr = "DOCUMENT_OBJECT";
				break;
			case (byte) 0xFB:
				typeStr = "PAGE_SEGMENT_OBJECT";
				break;
			case (byte) 0xFC:
				typeStr = "OVERLAY_OBJECT";
				break;
			case (byte) 0xFD:
				typeStr = "PAGEDEF_OBJECT";
				break;
			case (byte) 0xFE:
				typeStr = "FORMDEF_OBJECT";
				break;
			default:
				throw new java.lang.IllegalArgumentException("Invalid object type: " + id);
			}
			this.id = id;
		}
		
		public byte getID() {
			return this.id;
		}
		
		public String getTypeStr() {
			return this.typeStr;
		}
		
		public String toString() {
			return this.typeStr;
		}
	};
	
	private Type type;
	
	public Resource(StructureField structField) {
		super(structField);
		this.structField = structField;
		if (this.structField != null) {
			this.nameStr = this.structField.getNameStr();
		}
		
		if (this.structField.getAllTriplet() != null && 
				this.structField.getAllTriplet().length > 0) {
			for (Triplet triplet : this.structField.getAllTriplet()) {
				if (triplet.getIdentifier() == X21Triplet.ID) {
					X21Triplet x21 = (X21Triplet) triplet;
					this.type = Type.instance(((byte) x21.getObjType()));
				}
			}
		}
	}
	
	public Type getType() {
		return this.type;
	}
	
	@Override
	public boolean isBegin() {
		if (Tag.BRS == this.structField.getStructureTag()) {
			return true;
		} else if (Tag.ERS == this.structField.getStructureTag()) {
			return false;
		}
		return false;
	}

	@Override
	public void collect() {
	}

}
