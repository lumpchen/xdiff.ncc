package me.lumpchen.xafp.goca;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.AFPConst;
import me.lumpchen.xafp.AFPException;
import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.Renderable;
import me.lumpchen.xafp.render.ResourceManager;

public class GraphicsSegment implements Renderable {

	public static final int Begin = 0x70;
	public static final int End = 0x71;
	
	public static final int LENGTH = 0x0C;
	
	/**
	 * Indicates whether this segment is chained or not. Chained segments define the picture, 
	 * unchained segments are ignored in AFP GOCA.
	 * */
	private boolean chained; // B'0' Chained B'1' Unchained
	
	/**
	 * Indicates whether the segment has a prolog section at the beginning of the segment data.
	 * */
	private boolean prolog;  // B'0' No Prolog B'1' Prolog
	
	/**
	 * B'00' New segment
	 * B'01' Reserved
	 * B'10' Reserved
	 * B'11' Append the specified data to the end of the existing segment
	 * */
	private boolean append;
	
	private int dataSize;
	
	private byte[] SEGDATA;
	
	private List<DrawingOrder> orderList;
	
	public GraphicsSegment() {
	}

	public void read(AFPInputStream in) throws IOException {
		int command = in.readCode();
		if (command != Begin) {
			throw new AFPException("Invalid Begin Segment command(0x70): " + AFPConst.bytesToHex((byte) command));
		}
		
		int len = in.readUBin(1);
		if (len != LENGTH) {
			throw new AFPException("Invalid Begin Segment command length(0x0C): " + AFPConst.bytesToHex((byte) len));
		}
		
		in.readBytes(4); // Name of segment to be created; ignored
		in.read();	// Ignored
		
		int flag = in.read(); // Segment Properties 2
		int bit0 = flag & 0x01;
		this.chained = (bit0 == 0);
		int bit3 = (flag >> 3) & 1;
		this.prolog = (bit3 == 1);
		int bit56 = (flag >> 5) & 3; // B'00' New segment B'01' Reserved B'10' Reserved B'11' Append the specified data to the 
		this.append = (bit56 == 0x03);
		
		this.dataSize = in.readUBin(2);
		
		in.readBytes(4); // Predecessor/successor name; ignored
		
		this.SEGDATA = in.readBytes(this.dataSize);
		
		this.orderList = this.readDrawingOrders(this.SEGDATA);
	}
	
	private List<DrawingOrder> readDrawingOrders(byte[] data) throws IOException {
		if (data == null) {
			return new ArrayList<DrawingOrder>(0);
		}
		
		List<DrawingOrder> orders = new ArrayList<DrawingOrder>();
		AFPInputStream in = new AFPInputStream(data);
		while (in.available() > 0) {
			DrawingOrder order = DrawingOrder.readOrder(in);
			orders.add(order);
//			System.out.println(order);
		}
		return orders;
	}

	@Override
	public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager) {
		if (this.orderList == null) {
			return;
		}

		boolean areaBegin = false;
		for (int i = 0; i < this.orderList.size(); i++) {
			DrawingOrder order = this.orderList.get(i);
			if (order instanceof BeginAreaOrder) {
				areaBegin = true;
			} else if (order instanceof EndAreaOrder) {
				areaBegin = false;
			} else if (order instanceof LineOrder) {
				if (!areaBegin) {
					graphics.beginPath(true, false);
					order.render(aeg, graphics, resourceManager);
					while ((i + 1) < (this.orderList.size() - 1) && this.orderList.get(i + 1) instanceof LineOrder) {
						this.orderList.get(++i).render(aeg, graphics, resourceManager);
					}
					graphics.endPath();
					continue;
				}
			}
			
			order.render(aeg, graphics, resourceManager);
		}
	}
}



