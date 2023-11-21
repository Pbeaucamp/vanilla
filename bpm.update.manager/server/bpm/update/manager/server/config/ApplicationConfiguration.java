package bpm.update.manager.server.config;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

public class ApplicationConfiguration {

	public static final String MANAGER_URL = "bpm.tomcat.manager.url";
	public static final String MANAGER_LOGIN = "bpm.tomcat.manager.login";
	public static final String MANAGER_PASSWORD = "bpm.tomcat.manager.password";

	public static final String RUNTIME_INSTALLATION_PATH = "bpm.runtime.installation.path";
	public static final String WEBAPP_INSTALLATION_PATH = "bpm.application.installation.path";
	public static final String SAVE_PATH = "bpm.application.save.path";
	public static final String HISTORY_PATH = "bpm.history.path";
	public static final String IS_UNIX = "bpm.application.isUnix";
	public static final String RESTART_COMMAND = "bpm.application.restartCommand";
	
	public static final String UPDATE_URL = "bpm.update.url";
	public static final String UPDATE_PORT = "bpm.update.port";
	public static final String UPDATE_LOGIN = "bpm.update.login";
	public static final String UPDATE_PASSWORD = "bpm.update.password";

	public static final String RUNTIME_URL = "bpm.runtime.url";
	public static final String RUNTIME_LOGIN = "bpm.runtime.login";
	public static final String RUNTIME_PASSWORD = "bpm.runtime.password";

	public static final String PATCH_NAME = "bpm.patch.name";
	public static final String PATCH_VERSION = "bpm.patch.version";
	
	public static final String APPLICATION_NAME = "bpm.application.name";
	public static final String APPLICATION_IS_RUNTIME = "bpm.application.isRuntime";

	private Properties configProperties;
	private String fileName;

	public ApplicationConfiguration(String configFile, Logger logger) throws ConfigurationException {
		this.fileName = configFile;
		logger.info("Loading configuration with " + ConfigurationManager.CONFIGURATION_FILE + "=" + configFile);
		try {
			FileInputStream fis = new FileInputStream(configFile);
			configProperties = new Properties();
			configProperties.load(fis);
			logger.info("Configuration file loaded.");
		} catch (Exception ex) {
			String msg = "Failed to load configuration file @" + configFile + ", reason : " + ex.getMessage();
			logger.fatal(msg, ex);
			throw new ConfigurationException(msg, ex);
		}

		logger.info("Parsing configuration file");
	}

	public String getProperty(String propertyName) {
		return configProperties.getProperty(propertyName);
	}
	
	/**
	 * should not be used except by RCP applications it is to update the
	 * configuration VanillaUrl withn the properties file
	 * 
	 * @param propertyName
	 * @param propertyValue
	 */
	public void setProperty(String propertyName, String propertyValue) {
		try {
			PropertiesConfiguration config = new PropertiesConfiguration(fileName);
			config.setProperty(propertyName, propertyValue);
			config.save();

			FileInputStream fis = new FileInputStream(fileName);
			configProperties = new Properties();
			configProperties.load(fis);
		} catch (Exception ex) {
			Logger.getLogger(getClass()).error("error saving vanilla properties file - " + ex.getMessage(), ex);
		}
	}
}
