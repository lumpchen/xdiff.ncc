package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.Identifier.Tag;
import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class ObjectEnvironmentGroup extends AFPContainer {

	private byte[] OEGName;
	private List<Triplet> triplets;
	
	private ObjectAreaDescriptor objectAreaDescriptor;
	private ObjectAreaPosition objectAreaPosition;
	private MapImageObject mapImageObject;
	private ImageDataDescriptor imageDataDescriptor;
	
	private MapGraphicsObject mapGraphicsObject;
	private GraphicsDataDescriptor graphicsDataDescriptor;
	
	public ObjectEnvironmentGroup(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	@Override
	public void collect() {
		for (AFPObject child : this.children) {
			if (child instanceof ObjectAreaDescriptor) {
				this.objectAreaDescriptor = (ObjectAreaDescriptor) child;
			} else if (child instanceof ObjectAreaPosition) {
				this.objectAreaPosition = (ObjectAreaPosition) child;
			} else if (child instanceof MapImageObject) {
				this.mapImageObject = (MapImageObject) child;
			} else if (child instanceof ImageDataDescriptor) {
				this.imageDataDescriptor = (ImageDataDescriptor) child;
			} else if (child instanceof MapGraphicsObject) {
				this.mapGraphicsObject = (MapGraphicsObject) child;
			} else if (child instanceof GraphicsDataDescriptor) {
				this.graphicsDataDescriptor = (GraphicsDataDescriptor) child;
			}
		}
	}
	
	private void parseData(byte[] data) throws IOException {
		if (data == null) {
			return;
		}
		AFPInputStream in = new AFPInputStream(data);
		try {
			if (in.remain() > 0) {
				this.OEGName = in.readBytes(8);
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
	public boolean isBegin() {
		if (Tag.BOG == this.structField.getStructureTag()) {
			return true;
		} else if (Tag.EOG == this.structField.getStructureTag()) {
			return false;
		}
		return false;
	}

	public byte[] getOEGName() {
		return OEGName;
	}

	public List<Triplet> getTriplets() {
		return triplets;
	}

	public ObjectAreaDescriptor getObjectAreaDescriptor() {
		return objectAreaDescriptor;
	}

	public ObjectAreaPosition getObjectAreaPosition() {
		return objectAreaPosition;
	}

	public MapImageObject getMapImageObject() {
		return mapImageObject;
	}

	public ImageDataDescriptor getImageDataDescriptor() {
		return imageDataDescriptor;
	}
	
	public MapGraphicsObject getMapGraphicsObject() {
		return mapGraphicsObject;
	}

	public GraphicsDataDescriptor getGraphicsDataDescriptor() {
		return graphicsDataDescriptor;
	}

}
