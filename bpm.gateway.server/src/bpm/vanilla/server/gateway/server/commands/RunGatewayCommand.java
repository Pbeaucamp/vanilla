package bpm.vanilla.server.gateway.server.commands;

import bpm.gateway.core.DocumentGateway;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.components.gateway.IGatewayRuntimeConfig;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.commands.CreateTaskCommand;
import bpm.vanilla.server.commons.server.tasks.ITask;
import bpm.vanilla.server.gateway.server.tasks.TaskGateway;

public class RunGatewayCommand extends CreateTaskCommand {

	private static final long serialVersionUID = -7343259295165689556L;

	private IGatewayRuntimeConfig runtimeConfig;
	private String login, password;

	private String sessionId;

	public RunGatewayCommand(IGatewayRuntimeConfig runtimeConfig, Server server, String login, String password, String sessionId) {
		super(server, runtimeConfig.getObjectIdentifier());
		this.runtimeConfig = runtimeConfig;
		this.login = login;
		this.password = password;
		this.sessionId = sessionId;
	}

	private PoolableModel<?> getPoolableModel() throws Exception {
		BaseVanillaContext vCtx = new BaseVanillaContext(getServer().getConfig().getVanillaUrl(), login, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vCtx);

		Repository repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(runtimeConfig.getObjectIdentifier().getRepositoryId());
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(runtimeConfig.getVanillaGroupId());

		BaseRepositoryContext repCtx = new BaseRepositoryContext(vCtx, group, repository);

		PoolableModel<?> poolModel = null;
		Object model = null;
		try {
			poolModel = getServer().getPool().borrow(repCtx, runtimeConfig.getObjectIdentifier().getDirectoryItemId());
			model = poolModel.getModel();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (poolModel != null) {
				try {
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
		PoolableModel<?> poolableModel = getPoolableModel();

		ITask task = null;
		if (poolableModel.getModel() instanceof DocumentGateway) {
			task = new TaskGateway(getTaskId(), runtimeConfig, getServer(), (PoolableModel<DocumentGateway>) poolableModel, sessionId);
		}

		return task;
	}

}
