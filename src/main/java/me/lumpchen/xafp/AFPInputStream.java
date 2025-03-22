package me.lumpchen.xafp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class AFPInputStream extends DataInputStream {

	public AFPInputStream(InputStream in) {
		super(in);
	}

	public AFPInputStream(byte[] data) {
		super(new ByteArrayInputStream(data));
	}
	
	public int readUBin(int numOfBytes) throws IOException {
		int val = -1;
		switch (numOfBytes) {
		case 1:
			val = this.readUnsignedByte();
			break;
		case 2:
			val = this.readUnsignedShort();
			break;
		case 3:
	        int ch1 = this.read();
	        int ch2 = this.read();
	        int ch3 = this.read();
	        if ((ch1 | ch2 | ch3) < 0)
	            throw new EOFException();
	        val = (int) ((ch1 << 16) + (ch2 << 8) + (ch3 << 0));
			break;
		default:
			throw new IOException("Invalid number of bytes: " + numOfBytes);
		} 
		
		return val;
	}
	
	public long readUnsignedInt() throws IOException {
		int val = this.readInt();
		return (long) (val & 0x00FFFFFFFF);
	}
	
	public int readSBin(int numOfBytes) throws IOException {
		int val = -1;
		switch (numOfBytes) {
		case 1:
			val = this.read();
			break;
		case 2:
			val = this.readShort();
			break;
		case 3:
	        int ch1 = this.read();
	        int ch2 = this.read();
	        int ch3 = this.read();
	        if ((ch1 | ch2 | ch3) < 0)
	            throw new EOFException();
	        if ((byte) (ch1 & 0xFF) < 0) {
	        	val = (int) ((0xFF << 24) + (ch1 << 16) + (ch2 << 8) + (ch3 << 0));
	        } else {
	        	val = (int) ((ch1 << 16) + (ch2 << 8) + (ch3 << 0));
	        }
			break;
		case 4:
			val = this.readInt();
			break;
		default:
			throw new IOException("Invalid number of bytes: " + numOfBytes);
		} 
		
		return val;
	}
	
	public int readCode() throws IOException {
		return this.readCode(1);
	}
	
	public int readCode(int numOfBytes) throws IOException {
		int val = -1;
		switch (numOfBytes) {
		case 1:
			val = (int) (this.readByte() & 0xFF);
			break;
		case 2:
			val = (int) (this.readShort() & 0xFFFF);
			break;
		default:
			throw new IOException("Invalid number of bytes: " + numOfBytes);
		} 
		
		return val;
	}
	
	public byte[] readBytes(int len) throws IOException {
		byte[] data = new byte[len];
		int read = this.read(data);
		if (read < 0) {
			throw new IOException("EOF reached!");
		}
		return data;
	}
	
	public static int checkBit(int i, int pos)  {
		return i & (1 << pos);
	}
	
	public int remain() throws IOException {
		return this.available(); // refactor
	}
	
}
