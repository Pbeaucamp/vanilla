package bpm.vanilla.server.gateway.pool;

import org.apache.log4j.Logger;

import bpm.vanilla.server.commons.pool.Pool;
import bpm.vanilla.server.commons.pool.PoolableModelFactory;

public class GatewayPool extends Pool {

	public GatewayPool(int poolSize, PoolableModelFactory factory, Logger logger) {
		super(poolSize, factory, logger);
	}

	/**
	 * @param factory
	 * @param logger
	 */
	public GatewayPool(PoolableModelFactory factory, Logger logger) {
		super(factory, logger);
	}

}
