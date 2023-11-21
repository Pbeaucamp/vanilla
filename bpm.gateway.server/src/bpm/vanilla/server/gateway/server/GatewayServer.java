package bpm.vanilla.server.gateway.server;

import java.io.File;

import org.apache.log4j.Logger;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.runtime2.RuntimeEngine;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.components.gateway.GatewayModelGeneration4Fmdt;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.gateway.pool.GatewayPool;
import bpm.vanilla.server.gateway.pool.GatewayPoolableFactory;
import bpm.vanilla.server.gateway.server.generators.TransfoFmdtGenerator;

public class GatewayServer extends Server {

	private Logger runtimeLogger = Logger.getLogger(RuntimeEngine.class);

	public GatewayServer(IVanillaComponent component, Logger logger, String url) {
		super(component, logger, url);

		setFactoryConfig(new FactoryGatewayServerConfig());
	}

	@Override
	protected void createPool() throws Exception {
		/*
		 * AdminAcess from ServerCOnfig
		 */
		String url = getConfig().getVanillaUrl();

		if (url == null) {
			logger.error("missing VanillaServer url property in ServerConfig");
			throw new Exception("Bad ReportingServerConfig, missing VanillaServer url");
		}
		else {
			logger.info("Reporting Server VanillaUrl=" + url);
		}

		/*
		 * SocketPool requested to be bale to load XML from
		 * repositpryCXonnection
		 */

		int poolSize = ((GatewayServerConfig) getConfig()).getRepositoryPoolSize();

		/*
		 * ReportPool creation
		 */

		GatewayPoolableFactory factory = new GatewayPoolableFactory(this, logger);
		logger.info("GatewayPoolableFactory created");

		// poolSize = ((ReportingServerConfig)getConfig()).getReportPoolSize();
		GatewayPool pool = new GatewayPool(poolSize, factory, logger);
		setPool(pool);
		logger.info("GatewayPoolableFactory defined");

	}

	@Override
	protected void customizedInit() throws Exception {
		GatewayServerConfig conf = (GatewayServerConfig) getConfig();
		File f = new File(conf.getHomeFolder());
		if (!f.exists()) {
			f.mkdirs();
			logger.info("Gateway Home created at :" + f.getAbsolutePath());
		}

		f = new File(conf.getTempFolder());
		if (!f.exists()) {
			f.mkdirs();
			logger.info("Gateway Temp created at :" + f.getAbsolutePath());
		}

	}

	public Logger getServerLogger() {
		return logger;
	}

	public Logger getRuntimeLogger() {
		return runtimeLogger;
	}

	public DocumentGateway createTransformation(IRepositoryContext repCtx, GatewayModelGeneration4Fmdt fmdtConfig) throws Exception {
		return new TransfoFmdtGenerator(repCtx, fmdtConfig).createTransfo();
	}

}
