package me.lumpchen.xdiff;

public class DiffException extends RuntimeException {
	
	private static final long serialVersionUID = -3060377057814832455L;

	public DiffException(String msg) {
		super(msg);
	}
	
	public DiffException(String msg, Throwable t) {
		super(msg, t);
	}
}
