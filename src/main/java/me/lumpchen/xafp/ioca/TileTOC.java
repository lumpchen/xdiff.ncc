package me.lumpchen.xafp.ioca;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.AFPInputStream;

public class TileTOC {

	public static final int ID_TILE_TOC = 0xBB;
	
	private List<TOC> tocList;
	
	public TileTOC() {
		this.tocList = new ArrayList<TOC>();
	}
	
	public List<TOC> getTOCList() {
		return this.tocList;
	}
	
	public void read(AFPInputStream in) throws IOException {
		int length = in.readUBin(2);
		in.readUBin(2);
		
		int remain = length - 2;
		while (remain > 0) {
			TOC toc = new TOC();
			
			toc.XOFFSET = in.readUBin(4);
			toc.YOFFSET = in.readUBin(4);
			toc.THSIZE = in.readUBin(4);
			toc.TVSIZE = in.readUBin(4);
			toc.RELRES = in.readUBin(1);
			toc.COMPR = in.readCode();
			toc.DATAPOS = in.readUnsignedInt();
			this.tocList.add(toc);
			
			remain -= 26;
		}
	}
	
	public static class TOC {
		/**
		 * Horizontal offset of the tile origin, relative to the image origin
		 * */
		public int XOFFSET;
		
		/**
		 * Vertical offset of the tile origin, relative to the image origin
		 * */
		public int YOFFSET;
		
		/**
		 * Horizontal size in image points, excluding any padding bits in each scan line
		 * */
		public int THSIZE;
		
		/**
		 * Vertical size in image points, excluding any padding scan lines
		 * */
		public int TVSIZE;
		
		/**
		 * Relative resolution of the tile
		 * */
		public int RELRES;
		
		/**
		 * Compression algorithm
		 * */
		public int COMPR;
		
		/**
		 * Offset, in bytes, from the start of the Begin Segment parameter of the current image, 
		 * to the start of the Begin Tile parameter starting the tile
		 * */
		public long DATAPOS;
	}
}
