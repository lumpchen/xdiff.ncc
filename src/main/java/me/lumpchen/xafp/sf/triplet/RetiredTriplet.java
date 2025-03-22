package me.lumpchen.xafp.sf.triplet;

import java.io.IOException;

import me.lumpchen.xafp.AFPInputStream;

/**
 * The following triplets have been retired:
	Text Orientation Triplet X'1D'
	Resource Object Type Triplet X'21'
	Line Data Object Position Migration Triplet X'27'
	Object Checksum Triplet X'63' 
	Object Origin Identifier Triplet X'64' 
	IMM Insertion Triplet X'73'
*/
public class RetiredTriplet extends Triplet {
	public RetiredTriplet(int ID) {
		super();
		this.identifier = ID;
		this.name = "Retired triplet";
	}

	@Override
	protected void readContents(AFPInputStream in) throws IOException {
		while (remain > 0) {
			in.readBytes(remain);
			remain = 0;
		}
	}


}
