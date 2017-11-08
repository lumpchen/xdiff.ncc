package me.lumpchen.xafp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.Identifier.Tag;
import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;
import me.lumpchen.xafp.sf.triplet.X10Triplet;

public class ObjectContainer extends AFPContainer {
	
	private int ObjClass;
	private int StrucFlgs;
	
	public static class ObjectTypeIdentifier {
		
		public enum Component {
			EPS, TIFF, COM_SETUP, TAPE_LABEL_SETUP, DIB_WINDOWS, DIB_OS2, PCX, COLOR_MAPPING_TABLE, 
			GIF, JFIF, ANASTAK_CONTROL_RECORD, PDF_SINGLEPAGE_OBJECT, PDF_RESOURCE_OBJECT, PCL_PAGE_OBJECT, 
			RESIDENT_COLOR_PROFILE, IOCA_FS45_TILE_RESOURCE, EPS_WITH_TRANSPARENCY, PDF_WITH_TRANSPARENCY, 
			TRUETYPE_FONT, TRUETYPE_FONT_COLLECTION, RESOURCE_ACCESS_TABLE
		}
		/**
		 * Component ID			Object Type					Encoded Object-type OID
		 * 23						JFIF					X'06072B120004010117'
		 * 51				TrueType/OpenType Font			X'06072B120004010133'
		 * 53			TrueType/OpenType Font Collection   X'06072B120004010135'
		 * */
		
		private Component component;
		
		public ObjectTypeIdentifier(byte[] RegObjId) {
			this.parse(RegObjId);
		}
		
		public Component getComponent() {
			return this.component;
		}
		
		private void parse(byte[] data) {
//			Definition
//			Presentation Space Size
//			Foreground
//			Background
//			Component ID
			
			byte componentID = data[8];
			
			switch (componentID) {
			case X10Triplet.CID_JFIF:
				this.component = Component.JFIF;
				break;
			case X10Triplet.CID_TRUETYPE_FONT:
				this.component = Component.TRUETYPE_FONT;
				break;
			case X10Triplet.CID_TRUETYPE_FONT_COLLECTION:
				this.component = Component.TRUETYPE_FONT_COLLECTION;
				break;
			default:
				throw new AFPException("Object Type not implemented yet: " + componentID);
			}
		}
	}
	
	private ObjectTypeIdentifier objectTypeIdentifier;
	
	private byte[] ObjCName;
	private List<Triplet> triplets = new ArrayList<Triplet>();
	
	public ObjectContainer(StructureField structField) throws IOException {
		super(structField);
		this.structField = structField;
		if (this.structField != null) {
			this.nameStr = this.structField.getNameStr();
			this.parseData(this.structField.getData());
		}
	}
	
	private void parseData(byte[] data) throws IOException {
		if (data == null) {
			return;
		}
		AFPInputStream in = new AFPInputStream(data);
		try {
			this.ObjCName = in.readBytes(8);
			
			while (in.remain() > 0) {
				Triplet triplet = Triplet.readTriple(in);
				this.triplets.add(triplet);
			}
		} finally {
			in.close();
		}
	}
	
	public ObjectTypeIdentifier getObjectTypeIdentifier() {
		return this.objectTypeIdentifier;
	}
	
	public byte[] getObjectData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			for (AFPObject child : this.children) {
				if (child instanceof ObjectContainerData) {
					byte[] data = ((ObjectContainerData) child).getData();
					os.write(data);
				}
			}
			return os.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new byte[0];
	}

	@Override
	public void collect() {
		if (this.triplets.isEmpty()) {
			return;
		}
		
		for (Triplet triplet : this.triplets) {
			if (triplet instanceof X10Triplet) {
				X10Triplet t = (X10Triplet) triplet;
				this.ObjClass = t.getObjClass();
				this.StrucFlgs = t.getStrucFlgs();
				
				this.objectTypeIdentifier = new ObjectTypeIdentifier(t.getRegObjId());
			}
		}
	}
	
	public String getObjCName() {
		return AFPConst.ebcdic2Ascii(this.ObjCName);
	}
	
	@Override
	public boolean isBegin() {
		if (Tag.BOC == this.structField.getStructureTag()) {
			return true;
		} else if (Tag.EOC == this.structField.getStructureTag()) {
			return false;
		}
		return false;
	}
}
