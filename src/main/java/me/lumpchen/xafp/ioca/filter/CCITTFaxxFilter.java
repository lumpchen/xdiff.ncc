package me.lumpchen.xafp.ioca.filter;

import java.io.IOException;
import java.io.InputStream;

public class CCITTFaxxFilter {

	public static byte[] decode(InputStream inputStream, int cols, int rows, int compression, 
			int bitOrder, boolean encodedByteAlign, boolean blackIsOne) throws IOException {
		int arraySize = (cols + 7) / 8 * rows;
		byte[] decompressed = new byte[arraySize];
		CCITTFaxDecoderStream s;
		int type = 0;
		long tiffOptions = 0;

		if (compression == TIFFExtension.COMPRESSION_CCITT_MODIFIED_HUFFMAN_RLE) {
			tiffOptions = encodedByteAlign ? TIFFExtension.GROUP3OPT_BYTEALIGNED : 0;
			type = TIFFExtension.COMPRESSION_CCITT_MODIFIED_HUFFMAN_RLE;
		} else if (compression == TIFFExtension.COMPRESSION_CCITT_T4) {
			tiffOptions = encodedByteAlign ? TIFFExtension.GROUP3OPT_BYTEALIGNED : 0;
			tiffOptions |= TIFFExtension.GROUP3OPT_2DENCODING;
			type = TIFFExtension.COMPRESSION_CCITT_T4;
		} else if (compression == TIFFExtension.COMPRESSION_CCITT_T6) {
			tiffOptions = encodedByteAlign ? TIFFExtension.GROUP4OPT_BYTEALIGNED : 0;
			type = TIFFExtension.COMPRESSION_CCITT_T6;
		}

		s = new CCITTFaxDecoderStream(inputStream, cols, type, bitOrder, tiffOptions);
		readFromDecoderStream(s, decompressed);

		// invert bitmap
		if (!blackIsOne) {
			invertBitmap(decompressed);
		}
		
		return decompressed;
	}

	static void readFromDecoderStream(CCITTFaxDecoderStream decoderStream, byte[] result) throws IOException {
		int pos = 0;
		int read;
		while ((read = decoderStream.read(result, pos, result.length - pos)) > -1) {
			pos += read;
			if (pos >= result.length) {
				break;
			}
		}
		decoderStream.close();
	}

	private static void invertBitmap(byte[] bufferData) {
		for (int i = 0, c = bufferData.length; i < c; i++) {
			bufferData[i] = (byte) (~bufferData[i] & 0xFF);
		}
	}
}
