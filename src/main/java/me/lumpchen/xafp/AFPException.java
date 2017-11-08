package me.lumpchen.xafp;

public class AFPException extends RuntimeException {

	private static final long serialVersionUID = -7716424414699960238L;

	public AFPException() {
		super();
	}
	
	public AFPException(String msg) {
		super(msg);
	}
	
	public AFPException(Throwable t) {
		super(t);
	}
	
	public AFPException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
