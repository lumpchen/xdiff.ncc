package me.lumpchen.xafp.ioca.filter;

public interface TIFFExtension {
	/** CCITT T.4/Group 3 Fax compression. */
	int COMPRESSION_CCITT_T4 = 3;
	/** CCITT T.6/Group 4 Fax compression. */
	int COMPRESSION_CCITT_T6 = 4;
	/**
	 * LZW Compression. Was baseline, but moved to extension due to license
	 * issues in the LZW algorithm.
	 */
	int COMPRESSION_LZW = 5;
	/** Deprecated. For backwards compatibility only ("Old-style" JPEG). */
	int COMPRESSION_OLD_JPEG = 6;
	/** JPEG Compression (lossy). */
	int COMPRESSION_JPEG = 7;
	/** Custom: PKZIP-style Deflate. */
	int COMPRESSION_DEFLATE = 32946;
	/** Adobe-style Deflate. */
	int COMPRESSION_ZLIB = 8;

	int PHOTOMETRIC_SEPARATED = 5;
	int PHOTOMETRIC_YCBCR = 6;
	int PHOTOMETRIC_CIELAB = 8;
	int PHOTOMETRIC_ICCLAB = 9;
	int PHOTOMETRIC_ITULAB = 10;

	int PLANARCONFIG_PLANAR = 2;

	int PREDICTOR_HORIZONTAL_DIFFERENCING = 2;
	int PREDICTOR_HORIZONTAL_FLOATINGPOINT = 3;

	int FILL_RIGHT_TO_LEFT = 2;

	int SAMPLEFORMAT_INT = 2;
	int SAMPLEFORMAT_FP = 3;
	int SAMPLEFORMAT_UNDEFINED = 4;

	int YCBCR_POSITIONING_CENTERED = 1;
	int YCBCR_POSITIONING_COSITED = 2;

	/** Deprecated. For backwards compatibility only ("Old-style" JPEG). */
	int JPEG_PROC_BASELINE = 1;
	/** Deprecated. For backwards compatibility only ("Old-style" JPEG). */
	int JPEG_PROC_LOSSLESS = 14;

	/**
	 * For use with Photometric: 5 (Separated), when image data is in CMYK color
	 * space.
	 */
	int INKSET_CMYK = 1;

	/**
	 * For use with Photometric: 5 (Separated), when image data is in a color
	 * space other than CMYK. See
	 * {@link com.twelvemonkeys.imageio.metadata.exif.TIFF#TAG_INK_NAMES
	 * InkNames} field for a description of the inks to be used.
	 */
	int INKSET_NOT_CMYK = 2;

	int ORIENTATION_TOPRIGHT = 2;
	int ORIENTATION_BOTRIGHT = 3;
	int ORIENTATION_BOTLEFT = 4;
	int ORIENTATION_LEFTTOP = 5;
	int ORIENTATION_RIGHTTOP = 6;
	int ORIENTATION_RIGHTBOT = 7;
	int ORIENTATION_LEFTBOT = 8;

	int GROUP3OPT_2DENCODING = 1;
	int GROUP3OPT_UNCOMPRESSED = 2;
	int GROUP3OPT_FILLBITS = 4;
	int GROUP3OPT_BYTEALIGNED = 8;
	int GROUP4OPT_UNCOMPRESSED = 2;
	int GROUP4OPT_BYTEALIGNED = 4;
	int COMPRESSION_CCITT_MODIFIED_HUFFMAN_RLE = 2;
	int FILL_LEFT_TO_RIGHT = 1; // Default
}