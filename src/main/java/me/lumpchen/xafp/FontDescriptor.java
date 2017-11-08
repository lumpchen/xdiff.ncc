package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.triplet.Triplet;

public class FontDescriptor extends AFPObject {

	private byte[] TypeFcDesc;
	
	/**
	 * X'01' Ultralight
	 * X'02' Extralight
	 * X'03' Light
	 * X'04' Semilight
	 * X'05' MediumX(normal)
	 * X'06' Semibold
	 * X'07' Bold
	 * X'08' Extrabold
	 * X'09' Ultrabold
	 * */
	private int FtWtClass;
	
	private int FtWdClass;
	
	private int MaxPtSize;
	private int NomPtSize;
	private int MinPtSize;
	private int MaxHSize;
	private int NomHSize;
	private int MinHSize;
	
	private int DsnGenCls;
	private int DsnSubCls;
	private int DsnSpcGrp;
	
	private int FtDsFlags;
	private int GCSGID;
	private int FGID;
	
	private List<Triplet> triplets = new ArrayList<Triplet>();
	
	public FontDescriptor(StructureField structField) throws IOException {
		super(structField);
		this.parseData(this.structField.getData());
	}
	
	public byte[] getTypeFcDesc() {
		return this.TypeFcDesc;
	}
	
	public float getNormalVerticalSize() {
		return this.NomPtSize / 10f;
	}

	private void parseData(byte[] data) throws IOException {
		AFPInputStream in = new AFPInputStream(data);
		try {
			this.TypeFcDesc = in.readBytes(32);
			this.FtWtClass = in.readCode();
			this.FtWdClass = in.readCode();
			this.MaxPtSize = in.readUBin(2);
			this.NomPtSize = in.readUBin(2);
			
			this.MinPtSize = in.readUBin(2);
			this.MaxHSize = in.readUBin(2);
			this.NomHSize = in.readUBin(2);
			this.MinHSize = in.readUBin(2);
			
			this.DsnGenCls = in.readCode();
			this.DsnSubCls = in.readCode();
			this.DsnSpcGrp = in.readCode();
			
			in.readBytes(63 - 49 + 1);
			
			this.FtDsFlags = in.readUBin(2);
			
			in.readBytes(75 - 66 + 1);
			
			this.GCSGID = in.readUBin(2);
			
			this.FGID = in.readUBin(2);
			
			while (in.remain() > 0) {
				Triplet triplet = Triplet.readTriple(in);
				this.triplets.add(triplet);
			}
		} finally {
			in.close();
		}
	}
}
