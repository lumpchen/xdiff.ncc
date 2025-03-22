package me.lumpchen.xafp.render;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class AFPGraphics2D extends StructuredAFPPageGraphics {

	public AFPGraphics2D(Graphics2D g2, float width, float height) {
		super(g2, width, height);
	}

	@Override
	public void beginText() {
	}

	@Override
	public void endText() {
	}

	@Override
	public void beginGraphics(boolean stroke, boolean fill) {
	}

	@Override
	public void endGraphics() {
	}

	@Override
	public void beginImage() {
	}

	@Override
	public void endImage() {
	}

	@Override
	protected void markImage(BufferedImage img, float x, float y, float w, float h) {
	}

	@Override
	protected void markCurrentGraphicsState(GraphicsState state) {
	}

	@Override
	protected void appendChar(String unicode, Integer cid, Point2D origin, AffineTransform at, Shape glyph) {
	}

	@Override
	protected void markOutline(Shape outline) {
	}

	@Override
	protected void markPath(Shape path) {
		
	}

}
