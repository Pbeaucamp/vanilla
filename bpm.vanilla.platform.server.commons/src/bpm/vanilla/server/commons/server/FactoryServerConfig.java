package bpm.vanilla.server.commons.server;

import java.util.Properties;

/**
 * Base class used to create a ServerConfig from a properties Object
 * Each server have its own Factory.
 * 
 * Server subclass should have their own FactoryServerCOnfig for
 * specific options
 * 
 * FactoryServerConfig are used when performing a ResetServerConfigCommand
 * 
 * 
 * @author ludo
 *
 */
public class FactoryServerConfig {
	
	/**
	 * create a ServerConfog from a properties
	 * 
	 * subclass should Override this method
	 * 
	 * @param properties
	 * @return
	 */
	public ServerConfig createServerConfig(Properties properties){
		return new ServerConfig(properties);
	}
}
