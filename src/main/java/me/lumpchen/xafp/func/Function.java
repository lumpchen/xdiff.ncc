package me.lumpchen.xafp.func;

import java.io.IOException;

import me.lumpchen.xafp.AFPConst;
import me.lumpchen.xafp.AFPInputStream;
import me.lumpchen.xafp.ActiveEnvironmentGroup;
import me.lumpchen.xafp.render.AFPGraphics;
import me.lumpchen.xafp.render.Renderable;
import me.lumpchen.xafp.render.ResourceManager;

public abstract class Function implements Renderable {
	
//  PTOCA SFX record data control sequences

	//  Inline Controls                   Chained                                            Unchained
	public static final byte PTX_SIM  = (byte)0xC1; // Set Inline Margin                        0xC0
	public static final byte PTX_SIA  = (byte)0xC3; // Set Intercharacter Adjustment            0xC2
	public static final byte PTX_SVI  = (byte)0xC5; // Set Variable Space Character Increment   0xC4
	public static final byte PTX_AMI  = (byte)0xC7; // Absolute Move Inline                     0xC6
	public static final byte PTX_RMI  = (byte)0xC9; // Relative Move Inline                     0xC8
	//  Baseline Controls
	public static final byte PTX_SBI  = (byte)0xD1; // Set Baseline Increment                   0xD0
	public static final byte PTX_AMB  = (byte)0xD3; // Absolute Move Baseline                   0xD2
	public static final byte PTX_RMB  = (byte)0xD5; // Relative Move Baseline                   0xD4
	public static final byte PTX_BLN  = (byte)0xD9; // Begin Line                               0xD8
	public static final byte PTX_STO  = (byte)0xF7; // Set Text Orientation                     0xF6
	//  Controls for Data Strings
	public static final byte PTX_TRN  = (byte)0xDB; // Transparent Data                         0xDA
	public static final byte PTX_RPS  = (byte)0xEF; // Repeat String                            0xEE
	public static final byte PTX_NOP  = (byte)0xF9; // No Operation                             0xF8
	public static final byte PTX_NOPU = (byte)0xF8; // No Operation Unchained                   0xF8
	//  Controls for Rules
	public static final byte PTX_DIR  = (byte)0xE5; // Draw I-axis Rule                         0xE4
	public static final byte PTX_DBR  = (byte)0xE7; // Draw B-axis Rule                         0xE6
	//  Character Controls
	public static final byte PTX_STC_C  = (byte)0x75; // Set Text Color                           0x74
	public static final byte PTX_STC 	= (byte)0x74; // Set Text Color                           0x74
	
	public static final byte PTX_SEC  = (byte)0x81; // Set Extended Text Color                  0x80
	public static final byte PTX_SCFL = (byte)0xF1; // Set Coded Font Local)                    0xF0
	public static final byte PTX_BSU  = (byte)0xF3; // Begin Suppression                        0xF2
	public static final byte PTX_ESU  = (byte)0xF5; // End Suppression                          0xF4
	//  Field Controls
	public static final byte PTX_OVS  = (byte)0x73; // Overstrike                               0x72
	public static final byte PTX_USC  = (byte)0x77; // Underscore                               0x76
	public static final byte PTX_TBM  = (byte)0x79; // Temporary Baseline Move                  0x78

	
	protected int length = 0;
	protected int remain = 0;
	protected byte type;
	
	Function() {
	}
	
	public byte getType() {
		return this.type;
	}
	
	abstract public String getCommandString();
	
	abstract public String getCommandDesc();
	
	abstract void readFunction(AFPInputStream in) throws IOException;
	
	abstract public void render(ActiveEnvironmentGroup aeg, AFPGraphics graphics, ResourceManager resourceManager);
	
	public static Function readControlSequence(AFPInputStream in) throws IOException {
		int LENGTH = in.read();
		byte TYPE = (byte) (in.readCode() & 0xFF);
		
		Function func = null;
		if (TYPE == PTX_STO) {
			func = new SetTextOrientation();
		} else if (TYPE == PTX_SEC) {
			func = new SetExtendedTextColor();
		} else if (TYPE == PTX_AMB) {
			func = new AbsoluteMoveBaseline();
		} else if (TYPE == PTX_AMI) {
			func = new AbsoluteMoveInline();
		} else if (TYPE == PTX_RMI) {
			func = new RelativeMoveInline();
		} else if (TYPE == PTX_AMB) {
			func = new AbsoluteMoveBaseline();
		} else if (TYPE == PTX_DIR) {
			func = new DrawIaxisRule();
		} else if (TYPE == PTX_STC_C) {
			func = new SetTextColor(true);
		} else if (TYPE == PTX_STC) {
			func = new SetTextColor(false);
		} else if (TYPE == PTX_DBR) {
			func = new DrawBaxisRule();
		} else if (TYPE == PTX_SCFL) {
			func = new SetCodedFontLocal();
		} else if (TYPE == PTX_TRN) {
			func = new TransparentData();
		} else if (TYPE == PTX_NOPU) {
			func = new NoOperation();
		} else {
			System.err.println("Not implemented function type: " + AFPConst.bytesToHex(TYPE));
		}
		func.length = LENGTH;
		func.remain = LENGTH - 2;
		func.readFunction(in);
		
		if (func.remain != 0) {
			throw new IOException("Error when parsing Control Sequence: " + func);
		}
		return func;
	}
	
}
