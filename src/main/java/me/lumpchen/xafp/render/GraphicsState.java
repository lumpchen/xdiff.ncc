package me.lumpchen.xafp.render;

import java.awt.Color;

import me.lumpchen.xafp.AFPColor;
import me.lumpchen.xafp.font.AFPFont;

public final class GraphicsState implements Cloneable {

	private Matrix currentTransformationMatrix = new Matrix();

	public AFPColor afpBackgroundColor;
	public Color backgroundColor;
	public AFPColor afpColor;
	public Color color;
	
	public boolean stroke = false;
	public boolean fill = false;
	public boolean isAreaBegin = false;
	
	public TextState textState;

	public GraphicsState() {
		this.textState = new TextState();
	}

	public Matrix getCTM() {
		return this.currentTransformationMatrix;
	}

	public void setCTM(Matrix m) {
		this.currentTransformationMatrix = m;
	}

	public GraphicsState clone() {
		GraphicsState clone = null;
		try {
			clone = (GraphicsState) super.clone();
			clone.currentTransformationMatrix = this.currentTransformationMatrix.clone();
		} catch (CloneNotSupportedException e) {
		}
		return clone;
	}
	
	public static class TextState {
		public float posX;
		public float posY;
		public AFPFont font;
		public float fontSize;
		public AFPColor afpColor;
		public Color color;
		public float rotation;
		public float ruleWidth;
	}
}
