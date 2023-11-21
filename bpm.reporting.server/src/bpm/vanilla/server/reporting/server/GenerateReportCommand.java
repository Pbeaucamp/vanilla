package bpm.vanilla.server.reporting.server;

import java.io.OutputStream;
import java.util.Date;

import org.apache.log4j.Logger;

import bpm.fwr.api.beans.FWRReport;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.commands.CreateTaskCommand;
import bpm.vanilla.server.commons.server.tasks.ITask;
import bpm.vanilla.server.reporting.pool.BirtPoolableModel;
import bpm.vanilla.server.reporting.pool.FwrPoolableModel;
import bpm.vanilla.server.reporting.pool.JasperPoolableModel;
import bpm.vanilla.server.reporting.server.tasks.TaskGenerateBIRTReport;
import bpm.vanilla.server.reporting.server.tasks.TaskGenerateFWRReport;
import bpm.vanilla.server.reporting.server.tasks.TaskGenerateJASPERReport;

public class GenerateReportCommand extends CreateTaskCommand {

	private static final long serialVersionUID = -4474940349152782007L;

	private Date date = new Date();
	
	protected IReportRuntimeConfig runtimeConfig;
	protected String login, password;
	
	private String sessionId;
	private OutputStream os;

	public GenerateReportCommand(IReportRuntimeConfig runtimeConfig, Server server, String login, String password, String sessionId) {
		super(server, runtimeConfig.getObjectIdentifier());
		this.runtimeConfig = runtimeConfig;
		this.login = login;
		this.password = password;
		this.sessionId = sessionId;
	}
	
	public GenerateReportCommand(IReportRuntimeConfig runtimeConfig, Server server, String login, String password, String sessionId, OutputStream os) {
		super(server, runtimeConfig.getObjectIdentifier());
		this.runtimeConfig = runtimeConfig;
		this.login = login;
		this.password = password;
		this.sessionId = sessionId;
		this.os = os;
	}

	protected PoolableModel<?> getPoolableModel() throws Exception {
		PoolableModel<?> poolModel = null;
		Object model = null;

		/*
		 * create the IRepositoryContext
		 */
		BaseVanillaContext vCtx = new BaseVanillaContext(getServer().getConfig().getVanillaUrl(), login, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vCtx);

		Repository repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(runtimeConfig.getObjectIdentifier().getRepositoryId());
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(runtimeConfig.getVanillaGroupId());

		BaseRepositoryContext repCtx = new BaseRepositoryContext(vCtx, group, repository);

		try {
			// every borrow MUST be released as soon as possible whatever can
			// happen
			poolModel = getServer().getPool().borrow(repCtx, runtimeConfig.getObjectIdentifier().getDirectoryItemId());
			model = poolModel.getModel();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (poolModel != null) {
				try {
					// every borrow MUST be released as soon as possible
					// whatever can happen
					getServer().getPool().returnObject(repCtx, runtimeConfig.getObjectIdentifier().getDirectoryItemId(), poolModel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (model == null) {
			throw new Exception("error when interpreting message");
		}

		return poolModel;
	}

	@Override
	public ITask createTask() throws Exception {
		Date start = new Date();
		PoolableModel<?> poolableModel = getPoolableModel();

		Logger.getLogger(getClass()).info("##############################################################");
		Logger.getLogger(getClass()).info("Launch Report " + (poolableModel.getDirectoryItem() != null ? poolableModel.getDirectoryItem().getName() : "Unknown"));
		Logger.getLogger(getClass()).info("##############################################################");
		
		ITask task = null;
		if (poolableModel.getModel() instanceof FWRReport) {
			task = new TaskGenerateFWRReport(runtimeConfig, taskId, getServer(), (FwrPoolableModel) poolableModel, os, sessionId);
		}
		else if (poolableModel instanceof JasperPoolableModel) {
			task = new TaskGenerateJASPERReport(runtimeConfig, taskId, getServer(), (JasperPoolableModel) poolableModel, os, sessionId);
		}
		else if (poolableModel instanceof BirtPoolableModel) {
			task = new TaskGenerateBIRTReport(runtimeConfig, taskId, getServer(), (BirtPoolableModel) poolableModel, os, sessionId);
		}
		Date end = new Date();
		Logger.getLogger(getClass()).info("taskcreationtime = " + (end.getTime() - start.getTime()) + " cmdTime=" + (end.getTime() - date.getTime()));
		return task;
	}

}
