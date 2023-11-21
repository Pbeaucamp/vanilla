package bpm.fa.api.utils.log;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class Log {
	private static Logger logger = Logger.getLogger("bpm.fa.api");
	private static ConsoleAppender console = new ConsoleAppender(new PatternLayout());
	
	public static int DEBUG = 0;
	public static int ERROR = 1;
	
	public static void setup() {
		logger.addAppender(console);
		logger.setLevel(Level.DEBUG);
	}
	
	public static void info(String str) {
		logger.info(str);
	}
	
	public static void debug(String str) {
		logger.debug(str);
	}
	
	public static void error(String str, Throwable e) {
		if (e != null)
			logger.error(str, e);
		else
			logger.error(str);
	}
	
	public static void addAppender(Appender ap) {
		logger.addAppender(ap);
	}
	
	public static void setLevel(int lvl) {
		if (lvl == DEBUG)
			logger.setLevel(Level.DEBUG);
		else if (lvl == ERROR)
			logger.setLevel(Level.ERROR);
		else
			logger.setLevel(Level.DEBUG);
	}
}
