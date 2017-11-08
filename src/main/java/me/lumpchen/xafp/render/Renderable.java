package me.lumpchen.xafp.render;

import me.lumpchen.xafp.ActiveEnvironmentGroup;

public interface Renderable {
	
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager);
	
}
