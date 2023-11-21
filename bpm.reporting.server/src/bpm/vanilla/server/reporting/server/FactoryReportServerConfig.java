package bpm.vanilla.server.reporting.server;

import java.util.Properties;

import bpm.vanilla.server.commons.server.FactoryServerConfig;
import bpm.vanilla.server.commons.server.ServerConfig;

public class FactoryReportServerConfig extends FactoryServerConfig{

	/* (non-Javadoc)
	 * @see bpm.vanilla.server.commons.server.FactoryServerConfig#createServerConfig(java.util.Properties)
	 */
	@Override
	public ServerConfig createServerConfig(Properties properties) {
		return new ReportingServerConfig(properties);
	}

}
