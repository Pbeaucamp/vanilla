package bpm.workflow.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.IRuntimeState;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunIdentifier;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.model.WorkflowDigester;
import bpm.workflow.runtime.model.WorkflowModel;

public class WorkflowRuntimeImpl implements WorkflowService {

	private Logger logger = Logger.getLogger(this.getClass());

	private IVanillaComponent componentWorkflow;
	private ConcurrentHashMap<String, WorkflowInstance> instances = new ConcurrentHashMap<String, WorkflowInstance>();

	private IVanillaAPI vanillaApi;

	public WorkflowRuntimeImpl(IVanillaComponent componentWorkflow) throws Exception {
		this.componentWorkflow = componentWorkflow;
		this.vanillaApi = new RemoteVanillaPlatform(buildRootVanillaContext());
	}

	@Override
	public IRunIdentifier startWorkflow(IRuntimeConfig config) throws Exception {
		return startWorkflow(config, false);
	}

	@Override
	public IRunIdentifier startWorkflowAsync(IRuntimeConfig config) throws Exception {
		return startWorkflow(config, true);
	}

	private IRunIdentifier startWorkflow(final IRuntimeConfig config, boolean async) throws Exception {
		Repository rep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(config.getObjectIdentifier().getRepositoryId());
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(config.getVanillaGroupId());

		IRepositoryContext repCtx = new BaseRepositoryContext(vanillaApi.getVanillaContext(), group, rep);

		IRepositoryApi sock = new RemoteRepositoryApi(repCtx);

		RepositoryItem item = sock.getRepositoryService().getDirectoryItem(config.getObjectIdentifier().getDirectoryItemId());
		if (item == null) {
			throw new VanillaException("The Workflow model is not available.");
		}
		if (!(item.isOn())) {
			throw new VanillaException("The Workflow Model has been turned off from EnterpriseServices. No process can be instanciated.");
		}

		String xml = sock.getRepositoryService().loadModel(item);

		WorkflowModel model = WorkflowDigester.getModel(this.getClass().getClassLoader(), xml);

		WorkflowInstance instance = new WorkflowInstance(model, item.getName());

		instance.execute(config, async);

		IRunIdentifier id = new SimpleRunIdentifier(instance.getUuid());
		instances.put(id.getKey(), instance);
		
		if(!async) {
			WorkflowInstanceState state = instance.getState();
			while(!state.isStopped()) {
				Thread.sleep(1000);
				state = instance.getState();
			}
		}

		return id;
	}

	@Override
	public WorkflowInstanceState getInfos(IRunIdentifier iRunIdentifier, int itemId, int repositoryId) {
		WorkflowInstanceState result = null;
		synchronized (instances) {
			WorkflowInstance instance = instances.get(iRunIdentifier.getKey());
			if (instance != null) {
				result = instance.getState();

				if (result.isStopped() && instances.size() > 50) {
					instances.remove(iRunIdentifier.getKey());
				}
				return result;
			}

			return getPreviousInfos(iRunIdentifier.getKey(), itemId, repositoryId);
		}
	}

	private WorkflowInstanceState getPreviousInfos(String key, int itemId, int repositoryId) {
		logger.info("Get previous task with id = " + key + " from workflow server.");
		
		try {
			IRepositoryApi repositoryApi = getRootRepositoryApi(repositoryId);
			ItemInstance instance = repositoryApi.getAdminService().getWorkflowInstance(itemId, key);
	
			return (WorkflowInstanceState) instance.getState();
		} catch(Exception e) {
			logger.error("Unable to get previous task with item id = " + itemId + ", repository id = " + repositoryId + " and key = " + key + " from database.");
			e.printStackTrace();
			return null;
		}
		
//		File[] files = folderPreviousTasks.listFiles();
//		if (files != null && files.length > 0) {
//			try {
//				for (File file : files) {
//					if (file.getName().contains("runtimeId_" + key + "_")) {
//						WorkflowInstanceState runtimeState = (WorkflowInstanceState) new XStream().fromXML(new FileInputStream(file));
//						return runtimeState;
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
	}

	@Override
	public List<IRuntimeState> getPreviousInfos(int repositoryId, int start, int end) throws Exception {
		logger.info("Get previous task from workflow server.");
		IRepositoryApi repositoryApi = getRootRepositoryApi(repositoryId);
		List<ItemInstance> instances = repositoryApi.getAdminService().getItemInstances(start, end, IRepositoryApi.BIW_TYPE);

		List<IRuntimeState> taskInfos = new ArrayList<IRuntimeState>();
		if (instances != null) {
			for(ItemInstance instance : instances) {
				taskInfos.add(instance.getState());
			}
		}
		return taskInfos;
	}

	private IVanillaContext buildRootVanillaContext() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = config.getVanillaServerUrl();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		return new BaseVanillaContext(vanillaUrl, login, password);
	}

	private IRepositoryApi getRootRepositoryApi(int repositoryId) throws Exception {
		IVanillaContext ctx = buildRootVanillaContext();

		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ctx);

		Group grp = new Group();
		grp.setId(-1);

		Repository rep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(repositoryId);

		return new RemoteRepositoryApi(new BaseRepositoryContext(ctx, grp, rep));
	}

	@Override
	public long[] getMemory() throws Exception {
		long[] memory = new long[3];
		memory[0] = Runtime.getRuntime().freeMemory();
		memory[1] = Runtime.getRuntime().maxMemory();
		memory[2] = Runtime.getRuntime().totalMemory();
		return memory;
	}

	@Override
	public List<TaskInfo> getRunningTasks() throws Exception {
		List<TaskInfo> infos = new ArrayList<TaskInfo>();

		for (String task : instances.keySet()) {
			WorkflowInstance worflowState = instances.get(task);
			infos.add(buildTaskInfo(worflowState));
		}

		return infos;
	}

	@Override
	public ServerConfigInfo getServerConfig() throws Exception {
		return null;
	}

	@Override
	public List<TaskInfo> getTasksInfo() throws Exception {

		List<TaskInfo> infos = new ArrayList<TaskInfo>();
		synchronized (instances) {
			for (String task : instances.keySet()) {
				WorkflowInstance worflowState = instances.get(task);
				if (worflowState != null) {
					infos.add(buildTaskInfo(worflowState));
				}
				if (worflowState.getState().isStopped() && instances.size() > 50) {
					instances.remove(task);
				}
			}
		}

		return infos;
	}

	private TaskInfo buildTaskInfo(WorkflowInstance worflowState) {
		String groupName = worflowState.getRepositoryApi() != null ? worflowState.getRepositoryApi().getContext() != null ? worflowState.getRepositoryApi().getContext().getGroup() != null ? worflowState.getRepositoryApi().getContext().getGroup().getName() : "" : "" : "";
		int itemId = worflowState.getConfig() != null && worflowState.getConfig().getObjectIdentifier() != null ? worflowState.getConfig().getObjectIdentifier().getDirectoryItemId() : -1;
		int repositoryId = worflowState.getConfig() != null && worflowState.getConfig().getObjectIdentifier() != null ? worflowState.getConfig().getObjectIdentifier().getRepositoryId() : -1;
		
		TaskInfo info = new TaskInfo(worflowState.getUuid(), worflowState.getClass().getName(), itemId, worflowState.getItemName());
		info.setCreationDate(worflowState.getState().getStartedDate());
		info.setDurationTime(worflowState.getState().getDurationTime());
		info.setElapsedTime(worflowState.getState().getElapsedTime());
		info.setFailureCause(worflowState.getState().getFailureCause());
		info.setGroupName(groupName);
		info.setRepositoryId(repositoryId);
		info.setPriority("Normal");
		info.setResult(worflowState.getState().getResult());
		info.setStartedDate(worflowState.getState().getStartedDate());
		info.setState(worflowState.getState().getState());
		info.setStoppedDate(worflowState.getState().getStoppedDate());
		return info;
	}

	@Override
	public TaskInfo getTasksInfo(IRunIdentifier identifier) throws Exception {
		WorkflowInstance instance = instances.get(identifier.getKey());
		if (instance != null) {
			return buildTaskInfo(instance);
		}
		else {
			return null;
		}
	}

	@Override
	public String getUrl() {
		return "";
	}

	@Override
	public List<TaskInfo> getWaitingTasks() throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public void historize() throws Exception {
		throw new Exception("Historize is not available for Workflows.");
	}

	@Override
	public boolean isStarted() throws Exception {
		return componentWorkflow.getStatus() == Status.STARTED;
	}

	@Override
	public void removeTask(IRunIdentifier identifier) throws Exception {
		synchronized (instances) {
			WorkflowInstance instance = instances.get(identifier.getKey());
			if (instance != null) {
				instances.remove(identifier.getKey());
			}
		}
	}

	@Override
	public void resetServerConfig(ServerConfigInfo serverConfigInfo) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public void startServer() throws Exception {
		componentWorkflow.start();
	}

	@Override
	public void stopServer() throws Exception {
		componentWorkflow.stop();
	}

	@Override
	public void stopTask(IRunIdentifier taskId) throws Exception {
		WorkflowInstance instance = instances.get(taskId.getKey());
		instance.stop();
	}
}
