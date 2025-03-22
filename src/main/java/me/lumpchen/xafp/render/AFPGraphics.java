package me.lumpchen.xafp.render;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import me.lumpchen.xafp.AFPColor;
import me.lumpchen.xafp.font.AFPFont;

public interface AFPGraphics {
	
	public void drawString(String str, float x, float y);
	
	public void drawString(char[] text, float x, float y);
	
	public void scale(double sx, double sy);
	
	public void setBackground(Color color);
	
	public void clearRect(int x, int y, int width, int height);
	
	public void translate(double tx, double ty);
	
	public void rotate(double theta);
	
	public void setColor(AFPColor c);
	
    public void drawImage(BufferedImage img, float x, float y, float w, float h);

    public void setLineWidth(float w);
    
    public void drawRule(float x1, float y1, float x2, float y2, boolean horizon);
    
    public abstract void clip(Shape s);
    
    public void beginPath(boolean stroke, boolean fill);
    public GeneralPath getCurrentPath();
    public void draw(Shape s);
    public void fill(Shape s);
    public void endPath();
    
	public void setAFPFont(AFPFont afpFont, float fontSize);
	public void setTextPosX(float tx);
	public void setTextPosY(float tx);
	public void setTextRotation(float degree);
	public void setTextColor(AFPColor c);
	
    public void save();
    public void restore();
    public GraphicsState getGraphicsState();
    public void setGraphicsState(GraphicsState gstate);
    
    public void antialiasOn();
    public void antialiasOff();
    
}
