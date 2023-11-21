package bpm.vanilla.server.reporting.server;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;

import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.ServerConfig;
import bpm.vanilla.server.commons.server.exceptions.ServerException;
import bpm.vanilla.server.reporting.pool.ReportPool;
import bpm.vanilla.server.reporting.pool.ReportPoolableFactory;
import bpm.vanilla.server.reporting.server.tasks.MassTaskManager;

public class ReportingServer extends Server {
	private IReportEngine birtEngine;

	private HashMap<Long, String> reportFiles;

	public ReportingServer(IVanillaComponent component, Logger logger, String serverUrl) {
		super(component, logger, serverUrl);
		setFactoryConfig(new FactoryReportServerConfig());
	}

	public IReportEngine getBirtEngine() {
		return birtEngine;
	}

	@Override
	public void init(ServerConfig config) throws Exception {
		super.init(config);
		taskManager = new MassTaskManager(getClass().getClassLoader(), 1000, this, logger);
	}

	@Override
	protected void createPool() throws Exception {
		try {
			String url = getConfig().getVanillaUrl();

			if (url == null) {
				logger.error("missing VanillaServer url property in ServerConfig");
				throw new Exception("Bad ReportingServerConfig, missing VanillaServer url");
			}
			else {
				logger.info("Reporting Server VanillaUrl=" + url);
			}

			int poolSize = ((ReportingServerConfig) getConfig()).getRepositoryPoolSize();

			ReportPoolableFactory factory = new ReportPoolableFactory(logger, birtEngine);
			logger.info("ReportPoolableFactory created");

			poolSize = ((ReportingServerConfig) getConfig()).getReportPoolSize();
			ReportPool pool = new ReportPool(poolSize, factory, logger);
			setPool(pool);

			logger.info("ReportPoolableFactory defined");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void customizedInit() throws Exception {
		logger.info("customize init");

		EngineConfig config = new EngineConfig();

		File f = new File(((ReportingServerConfig) getConfig()).getBirtReportEngineLogsPath());
		if (!f.exists()) {
			f.mkdirs();
			logger.info("BirtReportLogsFoldercreated at :" + f.getAbsolutePath());
		}

		f = new File(((ReportingServerConfig) getConfig()).getGenerationFolder());
		if (!f.exists()) {
			f.mkdirs();
			logger.info("Reporting server Generation Folder created  :" + f.getAbsolutePath());
		}

		f = new File(((ReportingServerConfig) getConfig()).getHistorizationFolder());
		if (!f.exists()) {
			f.mkdirs();
			logger.info("Reporting server Historization Folder created  :" + f.getAbsolutePath());
		}

		// config.setEngineHome(((ReportingServerConfig)getConfig()).getBirtReportEnginePath());
		logger.info("BirtEngineLogPath=" + ((ReportingServerConfig) getConfig()).getBirtReportEngineLogsPath());
		if (ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_BIRT_LOG_LEVEL) != null) {
			config.setLogConfig(((ReportingServerConfig) getConfig()).getBirtReportEngineLogsPath(), Level.parse(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_BIRT_LOG_LEVEL)));
		}

		logger.info("initing Birt Engine");
		try {
			Platform.startup(config);
			IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			// config.setResourceLocator(new BirtResourceLocator(logger));
			birtEngine = factory.createReportEngine(config);

		} catch (Exception ex) {
			logger.error("initing Birt Engine error", ex);
			ex.printStackTrace();
		}
		logger.info("inited Birt Engine");

		// ((ReportingServerConfig)getConfig())

		// cleaning remaing files
		String generationFolder = ((ReportingServerConfig) getConfig()).getGenerationFolder();
		File ff = new File(generationFolder);
		for (String s : ff.list()) {
			new File(ff, s).delete();
		}

	}

	@Override
	public void stop() throws ServerException {
		super.stop();
	}

	public void setReportFiles(HashMap<Long, String> reportFiles) {
		this.reportFiles = reportFiles;
	}

	public HashMap<Long, String> getReportFiles() {
		return reportFiles;
	}
}
