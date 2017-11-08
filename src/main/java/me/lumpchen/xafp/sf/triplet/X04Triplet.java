package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

public class X04Triplet extends Triplet {

	public static final int ID = 0x04;
	
	public enum MapOption {
		 Position,
		 PositionAndTrim,
		 ScaleToFit,
		 CenterAndTrim,
		 MigrationMapping41,
		 MigrationMapping42, 
		 MigrationMapping50,
		 ScaleToFill,
		 UP3iPrintDataMapping
	}
	/**
	 * X'00' Position 
	 * X'10' Position and trim 
	 * X'20' Scale to fit 
	 * X'30' Center and trim 
	 * X'41' Migration mapping 
	 * X'42' Migration mapping 
	 * X'50' Migration mapping 
	 * X'60' Scale to fill 
	 * X'70' UP3i Print Data mapping
	 * */
	private int MapValue;
	
	public X04Triplet() {
		super();
		this.identifier = ID;
		this.name = "Mapping Option Triplet";
	}
	
	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		this.MapValue = in.readCode();
		this.remain -= 1;
	}
	
	public MapOption getMapValue() {
		MapOption mapOption;
		switch (MapValue) {
		case 0x00:
			mapOption = MapOption.Position;
			break;
		case 0x10:
			mapOption = MapOption.PositionAndTrim;
			break;
		case 0x20:
			mapOption = MapOption.ScaleToFit;
			break;
		case 0x30:
			mapOption = MapOption.CenterAndTrim;
			break;
		case 0x41:
			mapOption = MapOption.MigrationMapping41;
			break;
		case 0x42:
			mapOption = MapOption.MigrationMapping42;
			break;
		case 0x50:
			mapOption = MapOption.MigrationMapping50;
			break;
		case 0x60:
			mapOption = MapOption.ScaleToFill;
			break;
		case 0x70:
			mapOption = MapOption.UP3iPrintDataMapping;
			break;
		default:
			mapOption = MapOption.Position;
			break;
		}
		return mapOption;
	}

}
