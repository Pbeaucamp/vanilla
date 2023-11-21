package bpm.vanilla.server.reporting.server.tasks;

import java.util.Date;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.report.ReportRuntimeState;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.tasks.ITask;

public abstract class TaskGenerateReport implements ITask {

	private static final long serialVersionUID = 5707405014724474617L;

	protected transient Server server;
	private String sessionId;
	private String outputFileName;

	public TaskGenerateReport(Server server, String sessionId) {
		this.server = server;
		this.sessionId = sessionId;
	}

	protected Server getServer() {
		return server;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	protected void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	@Override
	public IVanillaComponentIdentifier getComponentIdentifier() {
		return getServer().getComponentIdentifier();
	}

	@Override
	public int getGroupId() {
		if(getPoolableModel() != null) {
			return getPoolableModel().getItemKey().getRepositoryContext().getGroup().getId();
		}
		else {
			return 0;
		}
	}

	@Override
	public IObjectIdentifier getObjectIdentifier() {
		if(getPoolableModel() != null && getPoolableModel().getItemKey() != null) {
			return new ObjectIdentifier(getPoolableModel().getItemKey().getRepositoryContext().getRepository().getId(), getPoolableModel().getItemKey().getDirectoryItemId());
		}
		else {
			return null;
		}
	}
	
	@Override
	public String getSessionId() {
		return sessionId;
	}
	
	@Override
	public void stopTask() throws Exception {
		ReportRuntimeState state = buildState();
		
		try {
			IRepositoryApi repositoryApi = getRootRepositoryApi(getObjectIdentifier().getRepositoryId());

			ItemInstance instance = new ItemInstance();
			instance.setItemId(getObjectIdentifier().getDirectoryItemId());
			instance.setGroupId(getGroupId());
			instance.setItemType(IRepositoryApi.CUST_TYPE);
			instance.setResult(getTaskState().getTaskResult());
			instance.setDuration(getTaskState().getDuration());
			instance.setRunDate(new Date());
			instance.setState(state);
			
			repositoryApi.getAdminService().addItemInstance(instance);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private ReportRuntimeState buildState() {
		ReportRuntimeState state = new ReportRuntimeState();
		state.setName(getReportName());
		state.setGroupId(getGroupId());
		state.setStartedDate(getTaskState().getStartedDate());
		state.setStoppedDate(getTaskState().getStoppedDate());
		state.setState(getTaskState().getTaskState());
		state.setFailureCause(getTaskState().getFailingCause() != null ? getTaskState().getFailingCause() : "");
		return state;
	}
	
	private String getReportName() {
		String itemName = "Unknown";
		if(getObjectIdentifier() != null && getObjectIdentifier().getRepositoryId() > 0 && getObjectIdentifier().getDirectoryItemId() > 0) {
			try {
				IRepositoryApi repApi = getRootRepositoryApi(getObjectIdentifier().getRepositoryId());
				itemName = repApi != null ? repApi.getRepositoryService().getDirectoryItem(getObjectIdentifier().getDirectoryItemId()).getName() : "Unknown";
			} catch (Exception e) {
				itemName = "Unknown";
			}
		}
		return itemName;
	}
	
	private IRepositoryApi getRootRepositoryApi(int repositoryId) throws Exception {
		IVanillaContext ctx = buildRootVanillaContext();
		
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ctx);
		
		Group grp = new Group();
		grp.setId(-1);
		
		Repository rep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(repositoryId);
		
		return new RemoteRepositoryApi(new BaseRepositoryContext(ctx, grp, rep));
	}

	private IVanillaContext buildRootVanillaContext() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = config.getVanillaServerUrl();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		return new BaseVanillaContext(vanillaUrl, login, password);
	}

	protected abstract PoolableModel<?> getPoolableModel();
}
