package me.lumpchen.xafp.goca;

import java.io.IOException;

import me.lumpchen.xafp.AFPConst;
import me.lumpchen.xafp.AFPException;
import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.render.Renderable;

public abstract class DrawingOrder implements Renderable {

	public enum Position {
		Given, Current
	};
	public static final int BeginAreaOrder = 0x68;
	public static final int EndAreaOrder = 0x60;
	
	public static final int BeginImageOrder_GP = 0xD1;
	public static final int BeginImageOrder_CP = 0x91;
	
	public static final int BoxOrder_GP = 0xC0;
	public static final int BoxOrder_CP = 0x80;
	
	public static final int SetProcessColorOrder = 0xB2;
	public static final int SetBackgroundMixOrder = 0x0D;
	
	public static final int NoOperationOrder = 0x00;
	public static final int CommentOrder = 0x01;
	
	public static final int SetPatternSetOrder = 0x08;
	public static final int SetPatternSymbolOrder = 0x28;
	
	public static final int LineOrder_GP = 0xC1;
	public static final int LineOrder_CP = 0x81;
	
	public static final int SetLineWidth = 0x19;
	public static final int SetLineType = 0x18;
	public static final int SetCurrentPosition = 0x21;
	
	public static final int SetFractionalLineWidthOrder = 0x11;
	
	
	public static final DrawingOrder readOrder(AFPInputStream in) throws IOException {
		int code = in.readCode();
		
		DrawingOrder order;
		switch (code) {
		case BeginAreaOrder:
			order = new BeginAreaOrder();
			break;
		case EndAreaOrder:
			order = new EndAreaOrder();
			break;
		case BeginImageOrder_GP:
			order = new BeginImageOrder(Position.Given);
			break;
		case BeginImageOrder_CP:
			order = new BeginImageOrder(Position.Current);
			break;
		case SetProcessColorOrder:
			order = new SetProcessColorOrder();
			break;
		case SetBackgroundMixOrder:
			order = new SetBackgroundMixOrder();
			break;
		case NoOperationOrder:
			order = new NoOperationOrder();
			break;
		case CommentOrder:
			order = new CommentOrder();
			break;
		case SetPatternSetOrder:
			order = new SetPatternSetOrder();
			break;
		case SetPatternSymbolOrder:
			order = new SetPatternSymbolOrder();
			break;
		case LineOrder_GP:
			order = new LineOrder(Position.Given);
			break;
		case LineOrder_CP:
			order = new LineOrder(Position.Current);
			break;
		case SetFractionalLineWidthOrder:
			order = new SetFractionalLineWidthOrder();
			break;
		case SetLineWidth:
			order = new SetLineWidthOrder();
			break;
		case SetLineType:
			order = new SetLineTypeOrder();
			break;
		case SetCurrentPosition:
			order = new SetCurrentPositionOrder();
			break;
		default:
			throw new AFPException("Invalid darwing order command: " + AFPConst.bytesToHex((byte) code));
		}
		
		order.readOperands(in);
		return order;
	}
	
	abstract protected void readOperands(AFPInputStream in) throws IOException;
	
}


