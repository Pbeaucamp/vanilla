package bpm.vanilla.server.reporting.server;

import java.util.HashMap;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.pool.VanillaItemKey;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.tasks.ITask;
import bpm.vanilla.server.reporting.pool.BirtPoolableModel;
import bpm.vanilla.server.reporting.pool.FwrPoolableModel;

public class GenerateReportFromModelCommand extends GenerateReportCommand {

	private static final long serialVersionUID = -138986707707057680L;

	private String modelXml;
	private boolean isDisco;

	public GenerateReportFromModelCommand(IReportRuntimeConfig runtimeConfig, Server server, String login, String password, String modelXml, String sessionId, boolean isDisco) {
		super(runtimeConfig, server, login, password, sessionId);
		this.modelXml = modelXml;
		this.isDisco = isDisco;
	}

	@Override
	protected PoolableModel<?> getPoolableModel() throws Exception {
		EngineConfig config = new EngineConfig();
		IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		IReportEngine birtEngine = factory.createReportEngine(config);
		
		if (isDisco) {
			BirtPoolableModel poolModel = new BirtPoolableModel(null, modelXml, null, birtEngine);
			return poolModel;
		}
		else {
			/*
			 * create the IRepositoryContext
			 */
			BaseVanillaContext vCtx = new BaseVanillaContext(getServer().getConfig().getVanillaUrl(), login, password);
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vCtx);

			Repository repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(runtimeConfig.getObjectIdentifier().getRepositoryId());
			Group group = vanillaApi.getVanillaSecurityManager().getGroupById(runtimeConfig.getVanillaGroupId());

			BaseRepositoryContext repCtx = new BaseRepositoryContext(vCtx, group, repository);

			VanillaItemKey vanillaKey = new VanillaItemKey(repCtx, -1);
			
			FwrPoolableModel poolModel = new FwrPoolableModel(null, modelXml, vanillaKey, birtEngine);
			return poolModel;
		}
	}

	public long addTaskToQueu() throws Exception {
		try {
			ITask task = createTask();
			if (!getServer().isStarted()) {
				throw new Exception("Server is not started");
			}

			task.startTask();
			task.join();

			String fName = task.getId() + "";

			if (getServer() instanceof ReportingServer) {
				ReportingServer serv = (ReportingServer) getServer();
				HashMap<Long, String> files = serv.getReportFiles();
				if (files == null) {
					serv.setReportFiles(new HashMap<Long, String>());
				}
				serv.getReportFiles().put(task.getId(), fName);
			}

			return task.getId();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Error when adding task to server's queue :" + ex.getMessage());
		}
	}
}
