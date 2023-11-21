package bpm.freemetrics.api.utils;

import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import bpm.freemetrics.api.features.alerts.AlertsChecker;

public class Log {
	private static Logger logger = Logger.getLogger(AlertsChecker.class);
	private static ConsoleAppender console = new ConsoleAppender(new PatternLayout());

	
	@SuppressWarnings("unchecked")
	public static void setup(Class c) {
		logger = Logger.getLogger(c);
		logger.addAppender(console);
		try {
			logger.addAppender(new FileAppender(new PatternLayout(), "freemetric.log", false));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
