package me.lumpchen.xafp;

import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.Renderable;
import me.lumpchen.xafp.render.ResourceManager;
import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.Identifier.Tag;

public class PageSegment extends AFPContainer implements Renderable {
	
	public PageSegment(StructureField structField) {
		super(structField);
		this.structField = structField;
		if (this.structField != null) {
			this.nameStr = this.structField.getNameStr();
		}
	}
	
	@Override
	public void collect() {
		System.out.print(this.nameStr);
	}
	
	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		for (AFPObject child : this.children) {
			if (child instanceof Renderable) {
				((Renderable) child).render(aeg, graphics, resourceManager);
			}
		}
	}
	
	@Override
	public boolean isBegin() {
		if (Tag.BPS == this.structField.getStructureTag()) {
			return true;
		} else if (Tag.EPS == this.structField.getStructureTag()) {
			return false;
		}
		return false;
	}
}
