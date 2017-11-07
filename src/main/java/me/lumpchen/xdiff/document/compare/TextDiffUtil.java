package me.lumpchen.xdiff.document.compare;

import java.util.LinkedList;

import me.lumpchen.xdiff.document.compare.name.fraser.neil.plaintext.diff_match_patch;
import me.lumpchen.xdiff.document.compare.name.fraser.neil.plaintext.diff_match_patch.Diff;

public class TextDiffUtil {

	public static LinkedList<Diff> diffText(String s1, String s2) {
		diff_match_patch textDiff = new diff_match_patch();
		LinkedList<Diff> diffs = textDiff.diff_main(s1, s2);
		textDiff.diff_cleanupEfficiency(diffs);
//		for (Diff diff : diffs) {
//			System.out.println(diff);
//		}
		return diffs;
	}

	public static int similarity(String s1, String s2) {
		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) {
			longer = s2;
			shorter = s1;
		}
		int longerLength = longer.length();
		if (longerLength == 0) {
			return 100;
		}
		float sim = (longerLength - LevenshteinDistance(longer, shorter)) / (float) longerLength;
		return Math.round(sim * 100);
	}

	public static int LevenshteinDistance(String s, String t) {
		// degenerate cases
		if (s.equals(t))
			return 0;
		if (s.length() == 0)
			return t.length();
		if (t.length() == 0)
			return s.length();

		// create two work vectors of integer distances
		int[] v0 = new int[t.length() + 1];
		int[] v1 = new int[t.length() + 1];

		// initialize v0 (the previous row of distances)
		// this row is A[0][i]: edit distance for an empty s
		// the distance is just the number of characters to delete from t
		for (int i = 0; i < v0.length; i++)
			v0[i] = i;

		for (int i = 0; i < s.length(); i++) {
			// calculate v1 (current row distances) from the previous row v0

			// first element of v1 is A[i+1][0]
			// edit distance is delete (i+1) chars from s to match empty t
			v1[0] = i + 1;

			// use formula to fill in the rest of the row
			for (int j = 0; j < t.length(); j++) {
				int cost = (s.charAt(i) == t.charAt(j)) ? 0 : 1;
				v1[j + 1] = Math.min(Math.min(v1[j] + 1, v0[j + 1] + 1), v0[j] + cost);
			}

			// copy v1 (current row) to v0 (previous row) for next iteration
			for (int j = 0; j < v0.length; j++)
				v0[j] = v1[j];
		}

		return v1[t.length()];
	}
}
