package bpm.vanilla.server.reporting.pool;

import org.apache.log4j.Logger;

import bpm.vanilla.server.commons.pool.Pool;
import bpm.vanilla.server.commons.pool.PoolableModelFactory;

public class ReportPool extends Pool {

	public ReportPool(int poolSize, PoolableModelFactory factory, Logger logger) {
		super(poolSize, factory, logger);
	}

	/**
	 * @param factory
	 * @param logger
	 */
	public ReportPool(PoolableModelFactory factory, Logger logger) {
		super(factory, logger);
	}

	
}
