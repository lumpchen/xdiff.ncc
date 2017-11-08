package me.lumpchen.xafp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.goca.GraphicsSegment;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.Renderable;
import me.lumpchen.xafp.render.ResourceManager;
import me.lumpchen.xafp.sf.Identifier.Tag;
import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class GraphicsObject extends AFPContainer implements Renderable {

	private byte[] GdoName;
	private List<Triplet> triplets;
	
	private ObjectEnvironmentGroup oeg;
	private byte[] gData;
	private GraphicsSegment segment;
	
	public GraphicsObject(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	public String getName() {
		if (this.GdoName != null) {
			return AFPConst.ebcdic2Ascii(this.GdoName);
		}
		return "";
 	}
	
	private void parseData(byte[] data) throws IOException {
		if (data == null) {
			return;
		}
		
		AFPInputStream in = new AFPInputStream(data);
		try {
			if (in.remain() > 0) {
				this.GdoName = in.readBytes(8);
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
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		float x = (float) aeg.unit2Point(this.oeg.getObjectAreaPosition().getXoaOset());
		float y = (float) aeg.unit2Point(this.oeg.getObjectAreaPosition().getYoaOset());
		float w = (float) aeg.unit2Point(this.oeg.getObjectAreaDescriptor().getXoaSize());
		float h = (float) aeg.unit2Point(this.oeg.getObjectAreaDescriptor().getYoaSize());
		
		graphics.save();
		
		graphics.antialiasOff();
		graphics.translate(x, y);
		int rotation = AFPConst.toDegree(this.oeg.getObjectAreaPosition().getXoaOrent());
		if (rotation != 0) {
			graphics.rotate(Math.toRadians(rotation));
		}
		
		if (this.segment != null) {
			this.segment.render(aeg, graphics, resourceManager);
		}
		graphics.antialiasOn();
		graphics.restore();
	}

	@Override
	public void collect() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			for (AFPObject child : this.children) {
				if (child instanceof ObjectEnvironmentGroup) {
					this.oeg = (ObjectEnvironmentGroup) child;
				} else if (child instanceof GraphicsData) {
					GraphicsData gdata = (GraphicsData) child;
					if (gdata.getData() != null) {
						os.write(gdata.getData());
					}
				}
			}
			this.gData = os.toByteArray();
			
			if (this.gData != null) {
				AFPInputStream dataStream = new AFPInputStream(this.gData);
				this.segment = new GraphicsSegment();
				
				this.segment.read(dataStream);
			}
		} catch (IOException e) {
			throw new AFPException(e);
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				throw new AFPException(e);
			}
		}
	}
	
	@Override
	public boolean isBegin() {
		if (Tag.BGR == this.structField.getStructureTag()) {
			return true;
		} else if (Tag.EGR == this.structField.getStructureTag()) {
			return false;
		}
		return false;
	}

}
