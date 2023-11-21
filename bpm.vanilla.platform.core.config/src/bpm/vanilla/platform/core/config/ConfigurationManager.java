package bpm.vanilla.platform.core.config;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * General configuration manager for vanilla platform
 * 
 * Do not store, use getInstance, so reloadConfigs will actually do something...
 * 
 * Includes own logger (might not have started the proper one)
 * 
 * It will try to configure logger!
 * 
 * @author manu
 *
 */
public class ConfigurationManager {

	private String configurationFile;
	
	private VanillaConfiguration vanillaConfig;
	
	private static Logger logger = Logger.getLogger(ConfigurationManager.class);
	
	private static ConfigurationManager config = null;
		
	/**
	 * sync'd to protect against concurrent access at boot
	 * @return
	 * @throws ConfigurationException(extends RuntimeException)
	 */
	public synchronized static ConfigurationManager getInstance() /*throws ConfigurationException*/ {
		if (config == null) {
			if (System.getProperty(ConfigurationConstants.configurationFile)!= null) {
				config = new ConfigurationManager(System.getProperty(ConfigurationConstants.configurationFile));
			}
			else {
				Logger.getLogger(ConfigurationManager.class).warn("The property bpm.vanilla.configurationFile has not been set");
				Logger.getLogger(ConfigurationManager.class).info("Trying to find the vanilla.properties file within ./resources ...");
				
				File f = new File("resources");
				if (f.exists() && f.isDirectory()){
					Logger.getLogger(ConfigurationManager.class).info("Found folder ./resources");
					
					for(String s : f.list()){
						if (s.equals("vanilla.properties")){
							File vPropFile = new File(f,s);
							if (vPropFile.isFile()){
								Logger.getLogger(ConfigurationManager.class).info("Found file ./resources/vanilla.properties");
								config = new ConfigurationManager(vPropFile.getAbsolutePath());
								return config;
							}
							
						}
					}
				}
				
				throw new ConfigurationException("Could not find bpm.vanilla.configurationFile, likely fatal", null);
			}
		}
		
		
		return config;
	}
	/**
	 * may throw a RuntimeException
	 * @param configFile
	 */
	private ConfigurationManager(String configFile) {
		logger.info("Creating configuration manager using configuration file " + configFile);
		configurationFile = configFile;
		vanillaConfig = new VanillaConfiguration(configFile, logger);
		logger.info("ConfigurationManager created.");
	}
	
	public VanillaConfiguration getVanillaConfiguration() {
		return vanillaConfig;
	}
	
	/**
	 * Not really used for now
	 * 
	 * @throws ConfigurationException
	 */
	public void reloadConfiguration() throws ConfigurationException {
		logger.info("Reloading configuration");
		vanillaConfig = new VanillaConfiguration(configurationFile, logger);
		logger.info("Configuration reloaded");
	}
	
	public static String getProperty(String propertyName) {
		return getInstance().getVanillaConfiguration().getProperty(propertyName);
	}
	
}
