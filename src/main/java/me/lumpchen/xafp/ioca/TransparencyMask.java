package me.lumpchen.xafp.ioca;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class TransparencyMask {

	public static final int Begin_ID = 0x8E;
	public static final int End_ID = 0x8F;
	
	private ImageSize imageSize;
	private ImageEncoding encoding;
	private ImageData imageData;
	private BandImageData bandImageData;
	
	public TransparencyMask() {
	}
	
	public void read(AFPInputStream in) throws IOException {
		int length = in.readUBin(1);

		while (true) {
			int id = in.readCode();

			if (id == ImageSize.ID) {
				this.imageSize = new ImageSize();
				this.imageSize.read(in);
			} else if (id == ImageEncoding.ID) {
				this.encoding = new ImageEncoding();
				this.encoding.read(in);
			} else if (id == ImageData.ID_0 || id == BandImageData.ID_0) {
				int id_1 = in.readCode();
				if (id_1 == ImageData.ID_1) {
					this.imageData = new ImageData();
					this.imageData.read(in);
				} else if (id_1 == BandImageData.ID_1) {
					this.bandImageData = new BandImageData();
					this.bandImageData.read(in);
				}
			} else if (id == End_ID) {
				in.readUBin(1);
				break;
			}
		}
	}
}
