package me.lumpchen.xafp.ioca;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import me.lumpchen.xafp.ioca.ImageEncoding.BitOrder;
import me.lumpchen.xafp.ioca.ImageEncoding.CompressionAlgrithm;
import me.lumpchen.xafp.ioca.filter.CCITTFaxxFilter;
import me.lumpchen.xafp.ioca.filter.TIFFExtension;

public class IOCAUtil {

	public static byte[] decodeData(ImageEncoding encoding, int col, int row, byte[] imageData) {
		CompressionAlgrithm compressionAlg = encoding.getAlgorhtim();
		BitOrder bitOrder = encoding.getBitOrder();
		
		int compression = TIFFExtension.COMPRESSION_CCITT_T6;
		if (compressionAlg == CompressionAlgrithm.G4) {
			compression = TIFFExtension.COMPRESSION_CCITT_T6;
			int order = TIFFExtension.FILL_LEFT_TO_RIGHT;
			if (bitOrder == BitOrder.Left_to_right) {
				order = TIFFExtension.FILL_LEFT_TO_RIGHT;
			} else if (bitOrder == BitOrder.Right_to_left) {
				order = TIFFExtension.FILL_RIGHT_TO_LEFT;
			}
			try {
				InputStream src = new ByteArrayInputStream(imageData);
				byte[] dest = CCITTFaxxFilter.decode(src, col, row, compression, order, false, false);
				return dest;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else if (compressionAlg == CompressionAlgrithm.JPEG) {
			return imageData;
		} else if (compressionAlg == CompressionAlgrithm.None) {
			return imageData;
		}
		
		throw new java.lang.IllegalArgumentException("Unspported compression algoritm: " + compressionAlg);
	}
	
	public static BufferedImage getBufferedImage(ImageEncoding encoding, IDEStructure ideStructure,
			int col, int row, byte[] imageData) throws IOException {
		CompressionAlgrithm alg = encoding.getAlgorhtim();
		byte[] decoded = IOCAUtil.decodeData(encoding, col, row, imageData);
		
		if (alg == CompressionAlgrithm.G4) {
//			int row = this.size.getRow();
//			BufferedImage img = new BufferedImage(this.size.getCol(), row, BufferedImage.TYPE_INT_ARGB);
//			WritableRaster newRaster = img.getRaster();
//
//			int size = newRaster.getDataBuffer().getSize() / 8;
//			for (int i = 0; i < size; i++) {
//				if (decoded[i] != -1) {
//					for (int bit = 0; bit < 8; bit++) {
//						int b = (decoded[i] >> (7 - bit)) & 0x01;
//						if (b == 1) {
//							newRaster.getDataBuffer().setElem(8 * i + bit, 0xff000000);
//						}
//					}
//				}
//			}
//			img.setData(newRaster);
//			return img;
			
			BufferedImage img = new BufferedImage(col, row, BufferedImage.TYPE_BYTE_BINARY);
			WritableRaster newRaster = img.getRaster();

			int size = newRaster.getDataBuffer().getSize();
			for (int i = 0; i < size; i++) {
				newRaster.getDataBuffer().setElem(i, (decoded[i] & 0xFF));
			}
			img.setData(newRaster);
			return makeTransprency(img);
		} else if (alg == CompressionAlgrithm.JPEG) {
			BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(decoded));
			return bimg;
		} else if (alg == CompressionAlgrithm.None) {
			BufferedImage img = new BufferedImage(col, row, BufferedImage.TYPE_BYTE_BINARY);
			WritableRaster newRaster = img.getRaster();

			int size = newRaster.getDataBuffer().getSize();
			if (ideStructure != null && ideStructure.isAdditive()) {
				for (int i = 0; i < size; i++) {
					newRaster.getDataBuffer().setElem(i, (decoded[i] & 0xFF));
				}
			} else {
				for (int i = 0; i < size; i++) {
					newRaster.getDataBuffer().setElem(i, ((~decoded[i]) & 0xFF));
				}
			}

			img.setData(newRaster);
			return makeTransprency(img);
		}
		throw new java.lang.IllegalArgumentException("Unspported compression algoritm: " + alg);
	}
	
	public static BufferedImage getBandImage(BandImageData[] bandImageDataArray, ImageEncoding encoding, 
			IDEStructure ideStructure, int col, int row) throws IOException {
		BufferedImage[] bandImage = new BufferedImage[bandImageDataArray.length];
		int i = 0;
		for (BandImageData data : bandImageDataArray) {
			bandImage[i] = getBufferedImage(encoding, ideStructure, col, row, data.getData());
			i++;
		}
		
		BufferedImage img = new BufferedImage(col, row, BufferedImage.TYPE_INT_RGB);
		for (i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if (ideStructure.getFormat() == IDEStructure.CMYK) {
					float c = toGray(bandImage[0].getRGB(j, i)) / 255f;
					float m = toGray(bandImage[1].getRGB(j, i)) / 255f;
					float y = toGray(bandImage[2].getRGB(j, i)) / 255f;
					float k = toGray(bandImage[3].getRGB(j, i)) / 255f;
					
					img.setRGB(j, i, cmyk2rgb(c, m, y, k));
				}
			}
		}
		
		return img;
	}
	
	private static float toGray(int rgb) {
		int a = (rgb >> 24) & 0xFF;
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		
		float gray = ((r + g + b) / 3) * (a / 255f);
		return gray;
	}
	
	private static int cmyk2rgb(float c, float m, float y, float k) {
		int r = Math.round(255 * (1 - c) * (1 - k));
		int g = Math.round(255 * (1 - m) * (1 - k));
		int b = Math.round(255 * (1 - y) * (1 - k));
		
		int rgb = (( r & 0xFF) << 16) | (( g & 0x0FF) << 8) | ( b & 0x0FF);
		return rgb;
	}
	
	public static BufferedImage makeTransprency(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		
		BufferedImage a = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int p = img.getRGB(i, j);
				if (p != -1) {
					a.setRGB(i, j, p);
				}
			}
		}
		return a;
	}
}
