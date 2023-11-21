package bpm.metadata.tools;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;


public class Log {
	private static Logger logger;

	public static Logger getLogger(){
		if(logger == null) {
			logger = Logger.getLogger("bpm.metadata.api");
			
			if (!logger.getAllAppenders().hasMoreElements()){
				logger.addAppender(new ConsoleAppender(new SimpleLayout()));
			}
		}
		return logger;
	}
	
	public static void info(String str) {
		getLogger();
		logger.info(str);
	}
	
	public static void debug(String str) {
		getLogger();
		logger.debug(str);
	}
	
	public static void warning(String str) {
		getLogger();
		logger.warn(str);
	}
	
	public static void error(String str, Throwable e) {
		getLogger();
		if (e != null)
			logger.error(str, e);
		else
			logger.error(str);
	}
	
	
}
