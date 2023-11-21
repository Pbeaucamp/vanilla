package bpm.vanilla.server.gateway.server;

import java.util.Properties;

import bpm.vanilla.server.commons.server.FactoryServerConfig;
import bpm.vanilla.server.commons.server.ServerConfig;

public class FactoryGatewayServerConfig extends FactoryServerConfig{

	/* (non-Javadoc)
	 * @see bpm.vanilla.server.commons.server.FactoryServerConfig#createServerConfig(java.util.Properties)
	 */
	@Override
	public ServerConfig createServerConfig(Properties properties) {
		return new GatewayServerConfig(properties);
	}

}
