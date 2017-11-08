package me.lumpchen.xafp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import me.lumpchen.xafp.ObjectContainer.ObjectTypeIdentifier;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.Renderable;
import me.lumpchen.xafp.render.ResourceManager;
import me.lumpchen.xafp.render.StructuredAFPGraphics;
import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;
import me.lumpchen.xafp.sf.triplet.X04Triplet;
import me.lumpchen.xafp.sf.triplet.X04Triplet.MapOption;
import me.lumpchen.xafp.sf.triplet.X4BTriplet;
import me.lumpchen.xafp.sf.triplet.X4CTriplet;

public class IncludeObject extends AFPObject implements Renderable {

	public enum ObjectType {
		PageSegment, ObjectData, GOCA, BCOCA, IOCA;
		
		public static ObjectType getObjectType(int id) {
			switch ((byte) id) {
			case (byte) 0x5F:
				return PageSegment;
			case (byte) 0x92:
				return ObjectData;
			case (byte) 0xBB:
				return GOCA;
			case (byte) 0xEB:
				return BCOCA;
			case (byte) 0xFB:
				return IOCA;
			default:
				throw new java.lang.IllegalArgumentException("Invalid Object Type: " + id);
			}
		}
	}
	
	private byte[] ObjName;
	private ObjectType objType;
	private int XoaOset;
	private int YoaOset;
	private int XoaOrent;
	private int YoaOrent;
	private int XocaOset;
	private int YocaOset;
	private int RefCSys;
	private List<Triplet> Triplets;
	
	private MapOption mapOption;
	private double width;
	private double height;

	public IncludeObject(StructureField structField) throws IOException {
		super(structField);
		this.Triplets = new ArrayList<Triplet>();
		this.parseData(this.structField.getData());
		this.calcSize();
	}

	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			this.ObjName = in.readBytes(8);
			in.read(); // reserverd
			
			this.objType = ObjectType.getObjectType(in.readCode());
			
			this.XoaOset = in.readSBin(3);
			this.YoaOset = in.readSBin(3);
			
			this.XoaOrent = in.readCode(2);
			this.YoaOrent = in.readCode(2);
			
			this.XocaOset = in.readSBin(3);
			this.YocaOset = in.readSBin(3);
			
			this.RefCSys = in.readCode();
			
			while (in.remain() > 0) {
				// read triplets
				Triplet triplet = Triplet.readTriple(in);
				this.Triplets.add(triplet);
			}
		} finally {
			in.close();
		}
	}

	public byte[] getObjName() {
		return ObjName;
	}

	public ObjectType getObjType() {
		return this.objType;
	}

	public int getXoaOset() {
		return XoaOset;
	}

	public int getYoaOset() {
		return YoaOset;
	}

	public int getXoaOrent() {
		return XoaOrent;
	}

	public int getYoaOrent() {
		return YoaOrent;
	}

	public int getXocaOset() {
		return XocaOset;
	}

	public int getYocaOset() {
		return YocaOset;
	}

	public int getRefCSys() {
		return RefCSys;
	}

	public List<Triplet> getTriplets() {
		return Triplets;
	}
	
	public MapOption getMapOption() {
		return this.mapOption;
	}
	
	public double getWidth() {
		return this.width;
	}
	
	public double getHeight() {
		return this.height;
	}

	private void calcSize() {
		int XoaBase = 10;
		int YoaBase = 10;
		int XoaUnits = 1;
		int YoaUnits = 1;
		int XoaSize = 0;
		int YoaSize = 0;
		
		for (Triplet triplet : this.Triplets) {
			if (triplet instanceof X4BTriplet) {
				X4BTriplet t = (X4BTriplet) triplet;
				XoaBase = t.getXoaBase() == 0 ? XoaBase : t.getXoaBase();
				YoaBase = t.getYoaBase() == 0 ? YoaBase : t.getYoaBase();
				XoaUnits = t.getXoaUnits();
				YoaUnits = t.getYoaUnits();
			} else if (triplet instanceof X04Triplet) {
				X04Triplet t = (X04Triplet) triplet;
				this.mapOption = t.getMapValue();
			} else if (triplet instanceof X4CTriplet) {
				X4CTriplet t = (X4CTriplet) triplet;
				XoaSize = t.getXoaSize();
				YoaSize = t.getYoaSize();
			}
		}
		
		this.width = ((double) XoaSize / XoaUnits) * XoaBase;
		this.height = ((double) YoaSize / YoaUnits) * YoaBase;
	}
	
	private int getRotation() {
		return AFPConst.toDegree(this.XoaOrent);
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		int x = (int) Math.round(aeg.unit2Point(this.XoaOset));
		int y = (int) Math.round(aeg.unit2Point(this.YoaOset));
		int w = (int) Math.round(this.width * 72);
		int h = (int) Math.round(this.height * 72);
		
		graphics.save();
		graphics.antialiasOff();
		
		graphics.translate(x, y);
		int rotation = this.getRotation();
		if (rotation != 0) {
			graphics.rotate(Math.toRadians(rotation));
		}
		
		String resName = AFPConst.ebcdic2Ascii(this.ObjName);
		if (this.objType == ObjectType.ObjectData) {
			ObjectTypeIdentifier objectTypeIdentifier = resourceManager.getObjectTypeIdentifier(resName);
			if (objectTypeIdentifier == null) {
				return;
			}
			
			ObjectTypeIdentifier.Component component = objectTypeIdentifier.getComponent();
			if (ObjectTypeIdentifier.Component.JFIF == component) {
				byte[] imageData = resourceManager.getObjectData(resName);
				try {
					BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(imageData));

					if (graphics instanceof StructuredAFPGraphics) {
						((StructuredAFPGraphics) graphics).beginImage();
					}

					graphics.drawImage(bimg, 0, 0, w, h);
					
					if (graphics instanceof StructuredAFPGraphics) {
						((StructuredAFPGraphics) graphics).endImage();
					}
				} catch (IOException e) {
					throw new AFPException("can't read image data: " + resName);
				}
			}
		} else if (this.objType == ObjectType.IOCA) {
			ImageObject ioca = resourceManager.getIOCAObject(resName);
			if (ioca == null) {
				return;
			}
			BufferedImage img = ioca.getJavaImage();
			if (img != null) {
				if (graphics instanceof StructuredAFPGraphics) {
					((StructuredAFPGraphics) graphics).beginImage();
				}
				
				graphics.drawImage(img, 0, 0, w, h);
				
				if (graphics instanceof StructuredAFPGraphics) {
					((StructuredAFPGraphics) graphics).endImage();
				}
			} else {
				throw new AFPException("can't read image data: " + resName);
			}
		} else {
			throw new AFPException("object type not implemented yet: " + this.objType);
		}
		
		graphics.antialiasOn();
		graphics.restore();
	}
}












