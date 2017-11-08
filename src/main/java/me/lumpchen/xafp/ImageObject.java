package me.lumpchen.xafp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.ioca.ImageSegment;
import me.lumpchen.xafp.ioca.Tile;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.Renderable;
import me.lumpchen.xafp.render.ResourceManager;
import me.lumpchen.xafp.render.StructuredAFPGraphics;
import me.lumpchen.xafp.sf.Identifier.Tag;
import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class ImageObject extends AFPContainer implements Renderable {

	private byte[] IdoName;
	private List<Triplet> triplets;
	
	private ObjectEnvironmentGroup oeg;
	
	private ImageSegment imgSegment;
	
	public ImageObject(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	@Override
	public void collect() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			for (AFPObject child : this.children) {
				if (child instanceof ObjectEnvironmentGroup) {
					this.oeg = (ObjectEnvironmentGroup) child;
				} else if (child instanceof ImagePictureData) {
					ImagePictureData ipd = (ImagePictureData) child;
					os.write(ipd.getIOCAdata());
				}
			}
			
			byte[] imageSegmentData = os.toByteArray();
			this.imgSegment = new ImageSegment();
			this.imgSegment.read(new AFPInputStream(imageSegmentData));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private int getRotation() {
		return AFPConst.toDegree(this.oeg.getObjectAreaPosition().getXoaOrent());
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		float x = (float) aeg.unit2Point(this.oeg.getObjectAreaPosition().getXoaOset());
		float y = (float) aeg.unit2Point(this.oeg.getObjectAreaPosition().getYoaOset());
		float w = (float) aeg.unit2Point(this.oeg.getObjectAreaDescriptor().getXoaSize());
		float h = (float) aeg.unit2Point(this.oeg.getObjectAreaDescriptor().getYoaSize());
		
		if (graphics instanceof StructuredAFPGraphics) {
			((StructuredAFPGraphics) graphics).beginImage();
		}
		graphics.save();
		
		graphics.antialiasOff();
		graphics.translate(x, y);
		int rotation = this.getRotation();
		if (rotation != 0) {
			graphics.rotate(Math.toRadians(rotation));
		}
		
		BufferedImage img = null;
		float dx = 0, dy = 0;
		if (this.imgSegment.isTile()) {
			Tile tile = this.imgSegment.getTile(0);
			dx = (float) aeg.unit2Point(tile.getPosX());
			dy = (float) aeg.unit2Point(tile.getPosY());
			w = (float) aeg.unit2Point(tile.getCol());
			h = (float) aeg.unit2Point(tile.getRow());
			img = this.imgSegment.getBufferedImage(tile);
		} else {
			img = this.imgSegment.getBufferedImage();
		}
		if (img != null) {
			graphics.drawImage(img, dx, dy, w, h);
		}
		graphics.antialiasOn();
		graphics.restore();
		
		if (graphics instanceof StructuredAFPGraphics) {
			((StructuredAFPGraphics) graphics).endImage();
		}
	}

	private void parseData(byte[] data) throws IOException {
		if (data == null) {
			return;
		}
		AFPInputStream in = new AFPInputStream(data);
		try {
			if (in.remain() > 0) {
				this.IdoName = in.readBytes(8);
			}
			
			while (in.remain() > 0) {
				Triplet triplet = Triplet.readTriple(in);
				if (this.triplets == null) {
					this.triplets = new ArrayList<Triplet>();
				}
				this.triplets.add(triplet);
			}
			
		} finally {
			in.close();
		}
	}
	
	public BufferedImage getJavaImage() {
		if (this.imgSegment != null) {
			return this.imgSegment.getBufferedImage();
		}
		return null;
	}

	@Override
	public boolean isBegin() {
		if (Tag.BIM == this.structField.getStructureTag()) {
			return true;
		} else if (Tag.EIM == this.structField.getStructureTag()) {
			return false;
		}
		return false;
	}
}
