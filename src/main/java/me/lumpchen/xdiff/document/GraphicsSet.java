package me.lumpchen.xdiff.document;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class GraphicsSet {
	
	private PageThread pageThread;
	private List<GraphicsContent> pathList;
	private boolean mergePath = true;
	
	public GraphicsSet(PageThread pageThread) {
		this.pageThread = pageThread;
		this.pathList = new ArrayList<GraphicsContent>();
	}
	
	private void merge(List<GraphicsContent> mergeList, GraphicsContent content) {
		boolean merged = false;
		for (int i = 0; i < mergeList.size(); i++) {
			if (mergeList.get(i).merge(content)) {
				merged = true;
				break;
			}
		}
		if (!merged) {
			mergeList.add(content);
		}
	}
	
	public void addPathContent(GraphicsContent pathContent) {
		if (this.mergePath) {
			this.merge(this.pathList, pathContent);	
		} else {
			this.pathList.add(pathContent);				
		}
	}
	
	public List<GraphicsLob> getPathLobList() {
		List<GraphicsLob> pathLobList = new ArrayList<GraphicsLob>(this.pathList.size());
		for (GraphicsContent pathContent : this.pathList) {
			pathLobList.add(new GraphicsLob(pathContent));
		}
		
		return pathLobList;
	}
	
	public static class GraphicsLob {
		
		private Rectangle2D bBox;
		private GraphicsContent pathContent;
		
		public GraphicsLob(GraphicsContent pathContent) {
			this.pathContent = pathContent;
			this.bBox = pathContent.getOutlineRect();
		}
		
		public Rectangle2D getBBox() {
			return this.bBox;
		}
		
		public List<Rectangle2D> getShapeBBox() {
			List<Shape> shapes = this.pathContent.getOutlineShapeList();
			List<Rectangle2D> shapeBBox = new ArrayList<Rectangle2D>(shapes.size());
			for (Shape shape : shapes) {
				shapeBBox.add(shape.getBounds2D());
			}
			return shapeBBox;
		}
		
		public GraphicsContent getPathContent() {
			return this.pathContent;
		}
		
		public String getPaintOperator() {
			return this.getPathContent().isFill() ? "Fill" : "Stroke";
		}
	}
	
}