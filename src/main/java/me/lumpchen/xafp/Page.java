package me.lumpchen.xafp;

import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.func.Function;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.Renderable;
import me.lumpchen.xafp.render.ResourceManager;
import me.lumpchen.xafp.sf.Identifier.Tag;
import me.lumpchen.xafp.sf.StructureField;

public class Page extends AFPContainer {

	private ActiveEnvironmentGroup aeg;
	private List<PresentationTextObject> ptxObjList;
	
	public Page(StructureField structField) {
		super(structField);
		this.structField = structField;
		if (this.structField != null) {
			this.nameStr = this.structField.getNameStr();
		}
		this.ptxObjList = new ArrayList<PresentationTextObject>();
	}
	
	public double getPageWidth() {
		return this.aeg.getWidth();
	}
	
	public double getPageHeight() {
		return this.aeg.getHeight();
	}
	
	public double getHorResolution() {
		return this.aeg.gethRes();
	}
	
	public double getVerResolution() {
		return this.aeg.getvRes();
	}
	
	@Override
	public void collect() {
		for (AFPObject child : this.children) {
			if (child instanceof ActiveEnvironmentGroup) {
				this.aeg = (ActiveEnvironmentGroup) child;
			} else if (child instanceof PresentationTextObject) {
				this.ptxObjList.add(((PresentationTextObject) child));
			} else if (child instanceof IncludePageSegment) {
				
			}
		}
	}
	
	public void render(AFPGraphics graphics, ResourceManager resourceManager) {
		AFPObject[] children = this.getChildren();
		for (AFPObject child : children) {
			if (child instanceof Renderable) {
				Renderable renderObj = (Renderable) child;
				renderObj.render(this.aeg, graphics, resourceManager);
			} else if (child instanceof PresentationTextObject) {
				PresentationTextObject ptxObj = (PresentationTextObject) child;
				for (PresentationTextData ptx : ptxObj.getPTX()) {
					List<Function> cs = ptx.getControlSequence();
					for (Function func : cs) {
						func.render(this.aeg, graphics, resourceManager);
					}
				}
			}
		}
	}
	
	@Override
	public boolean isBegin() {
		if (Tag.BPG == this.structField.getStructureTag()) {
			return true;
		} else if (Tag.EPG == this.structField.getStructureTag()) {
			return false;
		}
		return false;
	}

}
