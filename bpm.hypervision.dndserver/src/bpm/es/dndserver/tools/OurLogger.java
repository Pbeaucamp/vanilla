package bpm.es.dndserver.tools;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import bpm.es.dndserver.Messages;

public class OurLogger {

	private static Logger logger = Logger.getLogger(OurLogger.class);
	private static ConsoleAppender console = new ConsoleAppender(new PatternLayout());
	
	public static void init() {
		logger.addAppender(console);
		try {
			logger.addAppender(new FileAppender(new PatternLayout(), "plugin logs" + File.separator + "objectmigration.log", false)); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		logger.setLevel(Level.INFO);
	}
	
	public static void info(String msg) {
		logger.info(msg);
	}
	
	public static void error(String msg, Exception e) {
		if (e != null)
			logger.error(msg, e);
		else
			logger.error(msg);
	}
}
