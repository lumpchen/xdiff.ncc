package me.lumpchen.xafp.font;

public interface Encoding {

	public int getMaxCodePoint();
	
	public int getMinCodePoint();
	
	public int getCodePoint(int unicode);
	
	public String getCharacterName(int codepoint);

	public int getUnicode(int codepoint);
	
	public boolean isDefinedCodePoint(int codepoint);
}
