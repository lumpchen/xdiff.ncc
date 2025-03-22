package me.lumpchen.xafp.font;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class if for resolve bug in FontBox PdfParser.
 * Root cause: In FontBox PdfParser, it doesn't handle multiple segment.
 
 * See Technical Note #5040， "There can be any number of segments in the file; lengths can be odd numbers, 
 * and there can be several sequential segments of the same type."
 * 
 * Sample segments:
 * The following are examples of data segments (all numbers are decimal):
		128 1 20 0 0 0 <20 bytes of ASCII>
		128 2  10 1 0 0 <266 bytes of binary data>
		128 2  30 2 0 0 <542 bytes of binary data>
		128 3 <end-of-file>
 * 
 * */
public class PFBParser {

	private static final int START_MARKER = 0x80;

	private static final int ASCII_MARKER = 0x01;
	private static final int BINARY_MARKER = 0x02;
	private static final int EOF_MARKER = 0x03;

	private byte[] header;
	private byte[] binary;

	public PFBParser() {
	}

	public byte[] getSegment1() {
		return this.header;
	}

	public byte[] getSegment2() {
		return this.binary;
	}

	public void parse(byte[] src) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(src);
		this.parse(in);
	}

	public void parse(InputStream in) throws IOException {
		ByteArrayOutputStream bin = new ByteArrayOutputStream();

		if (in.read() != START_MARKER) {
			throw new IOException("");
		}
		int segType = in.read();
		if (segType == ASCII_MARKER) {
			int size = readSize(in);
			this.header = new byte[size];
			in.read(this.header, 0, size);
		}

		while (true) {
			if (in.read() != START_MARKER) {
				throw new IOException("");
			}
			segType = in.read();
			if (segType == BINARY_MARKER) {
				int size = readSize(in);

				byte[] data = new byte[size];
				in.read(data, 0, size);
				bin.write(data);
			} else if (segType == ASCII_MARKER) {
				int size = readSize(in);
				byte[] data = new byte[size];
				in.read(data, 0, size);
			} else if (segType == EOF_MARKER) {
				break;
			}
		}

		in.close();
		bin.close();
		this.binary = bin.toByteArray();
	}
	
	private int readSize(InputStream in) throws IOException {
		int size = in.read();
		size += in.read() << 8;
		size += in.read() << 16;
		size += in.read() << 24;
		return size;
	}

}

