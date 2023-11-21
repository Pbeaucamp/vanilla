package bpm.vanilla.platform.logging;

public interface IVanillaLogger {
	
	
	public void error(String message, Throwable exception);
	public void error(String message);
	public void error(Throwable error);
	
	public void warn(String message, Throwable exception);
	public void warn(String message);
	public void warn(Throwable error);
	
	public void info(String message, Throwable exception);
	public void info(String message);
	public void info(Throwable error);
	
	public void debug(String message, Throwable exception);
	public void debug(String message);
	public void debug(Throwable error);
	
	public void trace(String message, Throwable exception);
	public void trace(String message);
	public void trace(Throwable error);
}
