package me.lumpchen.xdiff;

public abstract class DiffLogger {
	
	public static DiffLogger getDefaultLogger() {
		return new DiffLogger() {
			public void info(String msg) {
				System.out.println(msg);
			}
			
			public void error(String msg) {
				System.err.println(msg);
			}
			
			public void warn(String msg) {
				System.err.println(msg);
			}
			
			public void error(Throwable t) {
				t.printStackTrace();
			}
		};
	}
	
	private float progress;
	
	public abstract void info(String msg);
	
	public abstract void error(String msg);
	
	public abstract void warn(String msg);
	
	public abstract void error(Throwable t);
	
	public void progress(int pageNo, int pageCount) {
		this.progress = (float) (pageNo) / pageCount;
	}
	
	public float getProgress() {
		return this.progress;
	}
}

