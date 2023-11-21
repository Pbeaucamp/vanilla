package bpm.vanilla.platform.core.components.gateway;

import bpm.vanilla.platform.core.beans.report.AlternateDataSourceConfiguration;
import bpm.vanilla.platform.core.components.IRuntimeConfig;

public interface IGatewayRuntimeConfig extends IRuntimeConfig{

	/**
	 * @return which configuration to use to run the Gateway for use DataSource
	 *  
	 */
	public AlternateDataSourceConfiguration getAlternateDataSourceConfiguration();
}
