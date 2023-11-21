package bpm.update.manager.server.config;

import org.apache.log4j.Logger;

public class ConfigurationManager {

	public static final String CONFIGURATION_FILE = "bpm.update.manager.configurationFile";

	private String configurationFile;
	private ApplicationConfiguration vanillaConfig;

	private static Logger logger = Logger.getLogger(ConfigurationManager.class);

	private static ConfigurationManager config = null;

	/**
	 * sync'd to protect against concurrent access at boot
	 * 
	 * @return
	 * @throws ConfigurationException
	 *             (extends RuntimeException)
	 */
	public synchronized static ConfigurationManager getInstance(){
		if (config == null) {
			if (System.getProperty(CONFIGURATION_FILE) != null) {
				config = new ConfigurationManager(System.getProperty(CONFIGURATION_FILE));
			}
			else {
				throw new ConfigurationException("Could not find bpm.update.manager.configurationFile, likely fatal", null);
			}
		}

		return config;
	}

	/**
	 * may throw a RuntimeException
	 * 
	 * @param configFile
	 */
	private ConfigurationManager(String configFile) {
		logger.info("Creating configuration manager using configuration file " + configFile);
		configurationFile = configFile;
		vanillaConfig = new ApplicationConfiguration(configFile, logger);
		logger.info("ConfigurationManager created.");
	}

	public ApplicationConfiguration getConfiguration() {
		return vanillaConfig;
	}

	/**
	 * Not really used for now
	 * 
	 * @throws ConfigurationException
	 */
	public void reloadConfiguration() throws ConfigurationException {
		logger.info("Reloading configuration");
		vanillaConfig = new ApplicationConfiguration(configurationFile, logger);
		logger.info("Configuration reloaded");
	}

}
