package bpm.fmloader.server;

import java.io.File;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;

public class ConfigurationManager {
	
	private static Logger logger = Logger.getLogger(ConfigurationManager.class);
	
	private static boolean isInited = false;
	
	public static void initConfiguration() {
		if (!isInited) {
			initLogger();
			isInited = true;
		}
	}

	private static void initLogger() {
		if (!logger.getAllAppenders().hasMoreElements()) {
			logger.setLevel(Level.INFO);
			PatternLayout l = new PatternLayout("%p %C:%M %m%n");
			logger.addAppender(new ConsoleAppender(l, ConsoleAppender.SYSTEM_OUT));
		}
		logger.info("Logger is initializing...");
		
		String logFile = System.getProperty("log4j.configuration");
		
		try {
			if (logFile != null) {
				File f = new File(logFile);
				if (!f.exists()) {
					logger.warn("Logger configuration specified at " + logFile + " could not be found, logger will go to default");
				}
				else {
					logger.info("Logger is being configured using configuration at " + logFile);
					logger.removeAllAppenders();//remove all existing appenders
					DOMConfigurator.configure(logFile);
				}
			}
			else {
				logger.warn("Logger configuration was not specified, logger will go to default mode");
			}
		} catch (Exception e) {
			logger.warn("Logger has failed to initialize : " + e.getMessage() + ", will go to default mode", e);
		}
		
		logger.info("Logger has finished initalizing.");	
	}
}
