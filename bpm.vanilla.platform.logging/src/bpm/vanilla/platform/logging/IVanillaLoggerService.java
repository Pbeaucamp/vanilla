package bpm.vanilla.platform.logging;

public interface IVanillaLoggerService {

	public IVanillaLogger getLogger(String loggerName);
	
	public void configure(Object configuration);
}
