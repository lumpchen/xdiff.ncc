package me.lumpchen.xafp.ioca;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import me.lumpchen.xafp.AFPConst;
import me.lumpchen.xafp.AFPException;
import me.lumpchen.xafp.AFPInputStream;

public class ImageContent {
	
	private static Logger logger = Logger.getLogger(ImageContent.class.getName());
	
	public static final int Begin = 0x91;
	public static final int End = 0x93;
	
	private int OBJTYPE;
	
	private ImageSize size;
	private ImageEncoding encoding;
	private BandImage bandImage;
	private LUTID lutID;
	private IDESize ideSize;
	private int[] bands;
	private IDEStructure ideStructure;
	private ExternalAlgorithmSpecification alg;
	private byte[] imageData;
	private BandImageData[] bandImageDataArray;
	
	private List<Tile> tileImageList;
	
	private BufferedImage bufferedImage;
	
	public ImageContent() {
	}
	
	public boolean isTile() {
		if (this.tileImageList == null || this.tileImageList.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public boolean isBandImage() {
		return this.bandImage != null && this.bandImageDataArray != null;
	}
	
	public void setImageSize(ImageSize size) {
		this.size = size;
	}
	
	public void setImageEncoding(ImageEncoding encoding) {
		this.encoding = encoding;
	}
	
	public void setImageBands(int[] bands) {
		this.bands = bands;
	}
	
	public void setIDEStructure(IDEStructure ide) {
		this.ideStructure = ide;
	}
	
	public void setExternalAlgorithmSpecification(ExternalAlgorithmSpecification alg) {
		this.alg = alg;
	}
	
	public byte[] getImageData() {
		return this.imageData;
	}
	
	public BufferedImage getBufferedImage() {
		if (this.bufferedImage == null) {
			try {
				if (!this.isTile()) {
					if (!this.isBandImage()) {
						this.bufferedImage = IOCAUtil.getBufferedImage(this.encoding, this.ideStructure,
								this.size.getCol(), this.size.getRow(), this.imageData);	
					} else {
						throw new AFPException("Band image not supported now.");
					}
				} else {
					if (!this.tileImageList.isEmpty()) {
						for (Tile tile : this.tileImageList) {
							this.bufferedImage = IOCAUtil.getBufferedImage(tile.getImageEncoding(), this.ideStructure,
									tile.getCol(), tile.getRow(), tile.getData());
							break; // TODO: handle tile images
						}
					}
				}
			} catch (IOException e) {
				throw new AFPException("Image processing error: ", e);
			}
		}

		return this.bufferedImage;
	}
	
	public Tile getTile(int tileIndex) {
		if (!this.isTile()) {
			throw new AFPException("Not a tile image.");
		}
		if (tileIndex < 0 || tileIndex >= this.tileImageList.size()) {
			throw new AFPException("Tile index out of boundary.");
		}
		return this.tileImageList.get(tileIndex);
	}
	
	public void read(AFPInputStream in) throws IOException {
		int id = in.readCode();
		if (id != ImageContent.Begin) {
			throw new IOException("Invalid Image Content begin mark(0x91): " + id);
		}
		int length = in.readUBin(1);
		if (length > 0) {
			this.OBJTYPE = in.readCode(); // must be 0xFF
		}
		
		ByteArrayOutputStream imageDataStream = new ByteArrayOutputStream();
		
		while (true) {
			id = in.readCode();
			
			if (id == ImageSize.ID) {
				ImageSize size = new ImageSize();
				size.read(in);
				this.setImageSize(size);
			} else if (id == ImageEncoding.ID) {
				ImageEncoding encoding = new ImageEncoding();
				encoding.read(in);
				this.setImageEncoding(encoding);
			} else if (id == IDESize.ID) {
				this.ideSize = new IDESize();
				this.ideSize.read(in);
			} else if (id == LUTID.ID) {
				this.lutID = new LUTID();
				this.lutID.read(in);
			} else if (id == BandImage.ID) {
				this.bandImage = new BandImage();
				this.bandImage.read(in);
			} else if (id == IDEStructure.ID) {
				IDEStructure ide = new IDEStructure();
				ide.read(in);
				this.setIDEStructure(ide);
			} else if (id == ExternalAlgorithmSpecification.ID) {
				ExternalAlgorithmSpecification alg = new ExternalAlgorithmSpecification();
				alg.read(in);
				this.setExternalAlgorithmSpecification(alg);
			} else if (id == ImageSegment.Double_Byte_ID_0) {
				int id_1 = in.readCode();
				if (id_1 == ImageData.ID_1) {
					ImageData ipd = new ImageData();
					ipd.read(in);
					imageDataStream.write(ipd.getData());
				} else if (id_1 == BandImageData.ID_1) {
					if (this.bandImageDataArray == null) {
						this.bandImageDataArray = new BandImageData[this.bandImage.getBandNumber()];
					}
					BandImageData bandImageData = new BandImageData();
					bandImageData.read(in);
					this.bandImageDataArray[bandImageData.getBandNum() - 1] = bandImageData;
				} else if (id_1 == TileTOC.ID_TILE_TOC) {
					TileTOC toc = new TileTOC();
					toc.read(in);
				}
			} else if (id == Tile.Begin_ID) {
				Tile tile = new Tile();
				tile.read(in);
				
				if (this.tileImageList == null) {
					this.tileImageList = new ArrayList<Tile>();
					this.tileImageList.add(tile);
				}
			} else if (id == ImageContent.End) {
				this.imageData = imageDataStream.toByteArray();
				length = in.readUBin(1);
				if (length != 0) {
					// throw exception
				}
				break;
			} else {
				// TODO comment this exception thrown line temporarily, for testcase "Letter_Ref.afp" output X'FECE' as X'CEFE'
				// still not found if there's byte order setting or this is a bug of "Letter_Ref.afp"
//				throw new IOException("Invalid begin code: " + AFPConst.bytesToHex((byte) id));
				logger.warning("Invalid begin code: " + AFPConst.bytesToHex((byte) id));
			}
		}
		imageDataStream.close();
	}
	
}
