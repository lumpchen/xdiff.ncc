package me.lumpchen.xafp.ioca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class BeginTile {
	
	public BeginTile() {
	}
	
	
	public void read(AFPInputStream in) throws IOException {
		int length = in.readUBin(2);
	}
}
