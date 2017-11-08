package me.lumpchen.xafp;

import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.func.Function;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.Renderable;
import me.lumpchen.xafp.render.ResourceManager;
import me.lumpchen.xafp.sf.Identifier.Tag;
import me.lumpchen.xafp.sf.StructureField;

public class Overlay extends AFPContainer implements Renderable {
	
	private ActiveEnvironmentGroup aeg;
	private List<PresentationTextObject> ptxObjList;
	
	private double scaleRatio;
	
	private double hRes;
	private double vRes;
	private double width;
	private double height;
	
	public Overlay(StructureField structField) {
		super(structField);
		this.structField = structField;
		if (this.structField != null) {
			this.nameStr = this.structField.getNameStr();
		}
	}
	
	@Override
	public void collect() {
		for (AFPObject child : this.children) {
			if (child instanceof ActiveEnvironmentGroup) {
				this.aeg = (ActiveEnvironmentGroup) child;
				if (this.aeg != null && this.aeg.getPageDescriptor() != null) {
					PageDescriptor pgd = this.aeg.getPageDescriptor();
					
					this.hRes = ((double) pgd.getXpgUnits()) / pgd.getXpgBase();
					this.vRes = ((double) pgd.getXpgUnits()) / pgd.getXpgBase();
					
					this.scaleRatio = pgd.getXpgBase() / pgd.getXpgUnits();
					this.width = this.unit2Point(pgd.getXpgSize());
					this.height = this.unit2Point(pgd.getYpgSize());
				}
			} else if (child instanceof PresentationTextObject) {
				if (this.ptxObjList == null) {
					this.ptxObjList = new ArrayList<PresentationTextObject>();
				}
				this.ptxObjList.add(((PresentationTextObject) child));
			} else if (child instanceof IncludePageSegment) {
				
			}
		}
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		ActiveEnvironmentGroup parentAeg = aeg;
		
		if (this.ptxObjList != null) {
			for (PresentationTextObject ptxObj : this.ptxObjList) {
//				graphics.beginText();
				for (PresentationTextData ptx : ptxObj.getPTX()) {
					List<Function> cs = ptx.getControlSequence();
					for (Function func : cs) {
						func.render(this.aeg, graphics, resourceManager);
					}
				}
//				graphics.endText();
			}
		}
		
		AFPObject[] children = this.getChildren();
		for (AFPObject child : children) {
			if (child instanceof Renderable) {
				Renderable renderObj = (Renderable) child;
				renderObj.render(this.aeg, graphics, resourceManager);
			}
		}
	}
	
	public double unit2Point(int val) {
		double pt = ((double) val) * this.scaleRatio * 72;
		return pt;
	}
	
	@Override
	public boolean isBegin() {
		if (Tag.BMO == this.structField.getStructureTag()) {
			return true;
		} else if (Tag.EMO == this.structField.getStructureTag()) {
			return false;
		}
		return false;
	}

	public ActiveEnvironmentGroup getAeg() {
		return aeg;
	}

	public double getScaleRatio() {
		return scaleRatio;
	}

	public double gethRes() {
		return hRes;
	}

	public double getvRes() {
		return vRes;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
}
