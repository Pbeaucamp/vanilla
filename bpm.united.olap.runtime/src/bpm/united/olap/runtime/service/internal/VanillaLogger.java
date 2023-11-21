package bpm.united.olap.runtime.service.internal;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.logging.IVanillaLogger;

public class VanillaLogger  implements IVanillaLogger{

	private Logger logger;
	
	public VanillaLogger(Logger logger) {
		this.logger= logger;
	}

	@Override
	public void debug(String message, Throwable exception) {
		logger.debug(message, exception);
		
	}

	@Override
	public void debug(String message) {
		logger.debug(message);
		
	}

	@Override
	public void debug(Throwable error) {
		logger.debug(error);
		
	}

	@Override
	public void error(String message, Throwable exception) {
		logger.error(message, exception);
		
	}

	@Override
	public void error(String message) {
		logger.debug(message);
		
	}

	@Override
	public void error(Throwable error) {
		logger.debug(error);
		
	}

	@Override
	public void info(String message, Throwable exception) {
		logger.info(message, exception);
		
	}

	@Override
	public void info(String message) {
		logger.info(message);
		
	}

	@Override
	public void info(Throwable error) {
		logger.info(error);
		
	}

	@Override
	public void trace(String message, Throwable exception) {
		logger.trace(message, exception);
		
	}

	@Override
	public void trace(String message) {
		logger.trace(message);
		
	}

	@Override
	public void trace(Throwable error) {
		logger.trace(error);
		
	}

	@Override
	public void warn(String message, Throwable exception) {
		logger.warn(message, exception);
		
	}

	@Override
	public void warn(String message) {
		logger.warn(message);
		
	}

	@Override
	public void warn(Throwable error) {
		logger.warn(error);
		
	}

	

}
