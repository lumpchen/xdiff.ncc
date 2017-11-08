package me.lumpchen.xafp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lumpchen.xafp.func.Function;
import me.lumpchen.xafp.sf.StructureField;

public class PresentationTextData extends AFPObject {

	public static final byte PTX_COMMAND_START_BYTE = (byte)0x2b;  // PTX record data start byte
	public static final byte PTX_COMMAND_2ND_BYTE   = (byte)0xd3;  // and the next byte

	public static final int  PTX_COMMAND_MAX_DATA   = 252;
	public static final int  PTX_RECORD_MAX_DATA    = 32759;
	public static final int  PTX_TEXT_RECORD_EXTRA  = 4;       // extra bytes needed for a text record (TRN+NOP)
	
	private byte[] PTOCAdat;
	private List<Function> controlSequence;
	
	public PresentationTextData(StructureField structField) throws IOException {
		super(structField);
		this.PTOCAdat = this.structField.getData();
		this.parseData();
	}
	
	public List<Function> getControlSequence() {
		return controlSequence;
	}

	private void parseData() throws IOException {
		if (this.PTOCAdat == null || this.PTOCAdat.length <= 0) {
			return;
		}
		this.controlSequence = new ArrayList<Function>();
		AFPInputStream in = new AFPInputStream(this.PTOCAdat);
		try {
			if (in.remain() > 0) {
				int PREFIX = (byte) (in.read() & 0xFF);
				if (PTX_COMMAND_START_BYTE != PREFIX) {
					throw new IOException("Error when parsing Control Sequence, wrong PREFIX(0x2b): " + PREFIX);
				}
				int CLASS = (byte) (in.read() & 0xFF);
				if (PTX_COMMAND_2ND_BYTE != CLASS) {
					throw new IOException("Error when parsing Control Sequence, wrong CLASS(0xd3): " + CLASS);
				}
				
				while (in.remain() > 0) {
					Function cs = Function.readControlSequence(in);
					this.controlSequence.add(cs);
				}
			}
		} finally {
			in.close();
		}
	}
	
	public byte[] getData() {
		return this.PTOCAdat;
	}
	
}
