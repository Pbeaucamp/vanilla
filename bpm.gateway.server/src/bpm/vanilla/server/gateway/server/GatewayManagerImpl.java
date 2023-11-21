package bpm.vanilla.server.gateway.server;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import bpm.gateway.core.DocumentGateway;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.IRuntimeState;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.UserRunConfigurationParameter;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceHolder;
import bpm.vanilla.platform.core.beans.tasks.ITaskState;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.components.RunIdentifier;
import bpm.vanilla.platform.core.components.gateway.GatewayModelGeneration4Fmdt;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.gateway.IGatewayRuntimeConfig;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.commons.server.ServerConfig;
import bpm.vanilla.server.commons.server.commands.CreateTaskCommand;
import bpm.vanilla.server.commons.server.tasks.GatewayState;
import bpm.vanilla.server.commons.server.tasks.ITask;
import bpm.vanilla.server.gateway.server.commands.RunGatewayCommand;
import bpm.vanilla.server.gateway.server.tasks.TaskGateway;

public class GatewayManagerImpl implements GatewayComponent {

	private Logger logger = Logger.getLogger(this.getClass());

	private IVanillaComponent component;
	private GatewayServer server;
	private IVanillaAPI vanillaApi;
	private String sessionId;

	public GatewayManagerImpl(IVanillaComponent component, GatewayServer server) {
		this.component = component;
		this.server = server;
		this.vanillaApi = new RemoteVanillaPlatform(buildRootVanillaContext());
	}

	public void init(String sessionId) {
		this.sessionId = sessionId;
	}

	private IVanillaContext buildRootVanillaContext() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = config.getVanillaServerUrl();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		return new BaseVanillaContext(vanillaUrl, login, password);
	}

	@Override
	public byte[] generateFmdtExtractionTransformation(GatewayModelGeneration4Fmdt config, User user) throws Exception {
		VanillaConfiguration vanillaConf = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = vanillaConf.getVanillaServerUrl();
		BaseVanillaContext vCtx = new BaseVanillaContext(vanillaUrl, user.getLogin(), user.getPassword());

		BaseRepositoryContext repCtx = new BaseRepositoryContext(vCtx, vanillaApi.getVanillaSecurityManager().getGroupById(config.getGroupId()), vanillaApi.getVanillaRepositoryManager().getRepositoryById(config.getFmdtRepositoryId()));
		DocumentGateway doc = server.createTransformation(repCtx, config);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		doc.write(bos);
		return Base64.encodeBase64(bos.toByteArray());
	}

	@Override
	public AlternateDataSourceHolder getAlternateDataSourcesConnections(IReportRuntimeConfig runtimeConfig) throws Exception {
		return null;
	}

	@Override
	public GatewayRuntimeState getRunState(IRunIdentifier runIdentifier) throws Exception {
		GatewayRuntimeState state = new GatewayRuntimeState();
		try {
			ITaskState info = server.getTaskManager().getState(((RunIdentifier) runIdentifier).getTaskId());
	
			if (info == null/* || info.getTaskState().equals(ActivityState.WAITING)*/) {
				return state;
			}
	
			if (info instanceof GatewayState && ((GatewayState) info).getTask() instanceof TaskGateway) {
				state = ((TaskGateway) ((GatewayState) info).getTask()).getStepsInfosMessage();
			}
	
			if (info.getFailingCause() != null && !info.getFailingCause().isEmpty()) {
				state.setState(ActivityState.FAILED);
			}
			else {
				switch (info.getTaskState()) {
				case RUNNING:
					state.setState(ActivityState.RUNNING);
					break;
				case ENDED:
					state.setState(ActivityState.ENDED);
					break;
				case WAITING:
					state.setState(ActivityState.WAITING);
					break;
				case UNKNOWN:
					state.setState(ActivityState.UNKNOWN);
					break;
				default:
					state.setState(ActivityState.UNKNOWN);
					break;
				}
			}

			return state;
		} catch(Exception e) {
			state.setState(ActivityState.WAITING);
			return state;
		}
	}

	@Override
	public GatewayRuntimeState runGateway(IGatewayRuntimeConfig runtimeConfig, User user) throws Exception {
		RunIdentifier id = runGatewayAsynch(runtimeConfig, user);
		GatewayRuntimeState state = null;
		int noStateCount = 0;
		if (id == null) {
			throw new Exception("Null Gateway runIdentifier");
		}
		do {
			try {
				Thread.sleep(10000);
				state = getRunState(id);
				if (state == null || state.getState() == null) {
					noStateCount++;
				}
			} catch (Exception ex) {
				noStateCount++;
				state = null;
				if(noStateCount > 2) {
					throw ex;
				}
			}

		} while (noStateCount < 5 && (state != null && (state.getState() != ActivityState.ENDED && state.getState() != ActivityState.FAILED)));

		return state;
	}

	@Override
	public RunIdentifier runGatewayAsynch(IGatewayRuntimeConfig runtimeConfig, User user) throws Exception {
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(runtimeConfig.getVanillaGroupId());

		RepositoryItem item = getItem(runtimeConfig, group);

		checkRole(item, group);

		if (item != null) {

			// save the user runtime configuration
			if (runtimeConfig instanceof GatewayRuntimeConfiguration) {
				if (((GatewayRuntimeConfiguration) runtimeConfig).isSaveConfig()) {
					saveGatewayRunConfig(user, runtimeConfig);
				}
			}

			RunGatewayCommand cmd = new RunGatewayCommand(runtimeConfig, server, user.getLogin(), user.getPassword(), sessionId);
			int taskId = ((Long) cmd.addTaskToQueue()).intValue();

			RunIdentifier id = new RunIdentifier(component.getIdentifier(), taskId);
			return id;
		}

		throw new Exception("Unable to find the item to run.");
	}

	private RepositoryItem getItem(IRuntimeConfig conf, Group group) throws Exception {
		Repository rep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(conf.getObjectIdentifier().getRepositoryId());

		IRepositoryApi sock = new RemoteRepositoryApi(new BaseRepositoryContext(buildRootVanillaContext(), group, rep));

		return sock.getRepositoryService().getDirectoryItem(conf.getObjectIdentifier().getDirectoryItemId());
	}

	private void checkRole(RepositoryItem it, Group group) throws VanillaException, Exception {
		for (Role r : vanillaApi.getVanillaSecurityManager().getRolesForGroup(group)) {
			try {
				if (r.getType().equals(IRepositoryApi.TYPES_NAMES[it.getType()]) && r.getGrants().contains("E")) {
					return;
				}
			} catch(Exception e) {
				throw e;
			}
		}

		throw new VanillaException("No Role with the Execute grant has been associated to the Group " + group.getName() + " on " + IRepositoryApi.TYPES_NAMES[it.getType()] + " Objects");
	}

	private void saveGatewayRunConfig(User user, IGatewayRuntimeConfig config) {
		GatewayRuntimeConfiguration conf = (GatewayRuntimeConfiguration) config;

		try {
			UserRunConfiguration userRunConfiguration = new UserRunConfiguration();
			userRunConfiguration.setDescription(conf.getSaveDescription());
			userRunConfiguration.setIdItem(conf.getObjectIdentifier().getDirectoryItemId());
			userRunConfiguration.setIdRepository(conf.getObjectIdentifier().getRepositoryId());
			userRunConfiguration.setIdUser(user.getId());
			userRunConfiguration.setName(conf.getSaveName());

			for (VanillaGroupParameter grpParam : conf.getParametersValues()) {
				for (VanillaParameter param : grpParam.getParameters()) {
					UserRunConfigurationParameter p = new UserRunConfigurationParameter();
					p.setName(param.getName());
					p.setValues(param.getSelectedValues());
					userRunConfiguration.addParameter(p);
				}
			}

			vanillaApi.getVanillaPreferencesManager().addUserRunConfiguration(userRunConfiguration);
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("could not save the run configuration : " + e.getMessage());
		}
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
	public ServerConfigInfo getServerConfig() throws Exception {
		ServerConfig config = server.getConfig();

		Properties props = new Properties();
		for (String s : config.getPropertiesName()) {
			props.setProperty(s, config.getPropertyValue(s) + "");
		}

		return new ServerConfigInfo(props);
	}

	@Override
	public void historize() throws Exception {
		try {
			server.getTaskManager().historize();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Error when generating historization : " + ex.getMessage());
		}
	}

	@Override
	public boolean isStarted() throws Exception {
		return server.isStarted();
	}

	@Override
	public void removeTask(IRunIdentifier identifier) throws Exception {
		if (identifier instanceof RunIdentifier) {
			RunIdentifier iden = (RunIdentifier) identifier;

			CreateTaskCommand task = null;
			for (CreateTaskCommand t : server.getTaskManager().getWaitingTasks()) {
				if (t.getTaskId() == iden.getTaskId()) {
					task = t;
					break;
				}
			}

			if (task == null) {
				throw new Exception("StopTask error : no running task with id=" + iden.getTaskId());
			}

			try {
				server.getTaskManager().removeWaitingTask(task);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new Exception("StopTask error : id=" + iden.getTaskId() + " " + ex.getCause().getMessage());
			}
		}
	}

	@Override
	public void resetServerConfig(ServerConfigInfo serverConfigInfo) throws Exception {
		ServerConfig conf = server.getFactoryServerConfig().createServerConfig(serverConfigInfo.getProperties());
		server.reInit(conf);
	}

	@Override
	public void startServer() throws Exception {
		server.start();

		// component.start();
	}

	@Override
	public void stopServer() throws Exception {
		server.stop();

		// component.stop();
	}

	@Override
	public void stopTask(IRunIdentifier identifier) throws Exception {
		if (identifier instanceof RunIdentifier) {
			RunIdentifier iden = (RunIdentifier) identifier;

			ITask task = null;
			for (ITask t : server.getTaskManager().getRunningTasks()) {
				if (t.getId() == iden.getTaskId()) {
					task = t;
					break;
				}
			}

			if (task == null) {
				throw new Exception("StopTask error : no running task with id=" + iden.getTaskId());
			}

			try {
				task.stopTask();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new Exception("StopTask error : id=" + iden.getTaskId() + " " + ex.getCause().getMessage());
			}
		}
	}

	@Override
	public List<TaskInfo> getRunningTasks() throws Exception {
		List<TaskInfo> infos = new ArrayList<TaskInfo>();

		for (ITask task : server.getTaskManager().getRunningTasks()) {
			TaskInfo info = buildTaskInfo(task);
			infos.add(info);
		}

		return infos;
	}

	@Override
	public List<TaskInfo> getTasksInfo() throws Exception {
		List<TaskInfo> infos = new ArrayList<TaskInfo>();

		List<ITask> tasks = server.getTaskManager().getAllTasks();
		for (ITask task : tasks) {
			TaskInfo info = buildTaskInfo(task);
			infos.add(info);
		}

		return infos;
	}

	@Override
	public TaskInfo getTasksInfo(IRunIdentifier identifier) throws Exception {
		if (identifier instanceof RunIdentifier) {
			RunIdentifier iden = (RunIdentifier) identifier;

			ITask task = server.getTaskManager().getTask(iden.getTaskId());

			if (task == null) {
				throw new Exception("GetTaskInfo error : no existing task with id=" + iden.getTaskId());
			}

			TaskInfo info = buildTaskInfo(task);
			return info;
		}

		return null;
	}

	private TaskInfo buildTaskInfo(ITask task) {
		String itemName = "Unknown";
		if (task.getObjectIdentifier() != null && task.getObjectIdentifier().getRepositoryId() > 0 && task.getObjectIdentifier().getDirectoryItemId() > 0) {
			try {
				IRepositoryApi repApi = getRootRepositoryApi(task.getObjectIdentifier().getRepositoryId());
				itemName = repApi != null ? repApi.getRepositoryService().getDirectoryItem(task.getObjectIdentifier().getDirectoryItemId()).getName() : "Unknown";
			} catch (Exception e) {
				e.printStackTrace();
				itemName = "Unknown";
			}
		}
		
		int itemId = task.getObjectIdentifier() != null ? task.getObjectIdentifier().getDirectoryItemId() : -1;
		int repositoryId = task.getObjectIdentifier() != null ? task.getObjectIdentifier().getRepositoryId() : -1;

		TaskInfo info = new TaskInfo(String.valueOf(task.getId()), task.getClass().getName(), itemId, itemName);

		info.setCreationDate(task.getTaskState().getCreationDate());
		info.setDurationTime(task.getTaskState().getDuration());
		info.setElapsedTime(task.getTaskState().getElapsedTime());
		info.setFailureCause(task.getTaskState().getFailingCause());
		info.setGroupName(String.valueOf(task.getGroupId()));
		info.setRepositoryId(repositoryId);
		info.setPriority(task.getTaskPriority().getLabel());
		info.setResult(task.getTaskState().getTaskResult());
		info.setStartedDate(task.getTaskState().getStartedDate());
		info.setState(task.getTaskState().getTaskState());
		info.setStoppedDate(task.getTaskState().getStoppedDate());

		return info;
	}

	@Override
	public List<TaskInfo> getWaitingTasks() throws Exception {
		List<TaskInfo> infos = new ArrayList<TaskInfo>();

		for (CreateTaskCommand task : server.getTaskManager().getWaitingTasks()) {
			TaskInfo info = new TaskInfo(String.valueOf(task.getTaskId()), task.getClass().getName(), -1, "Unknown");
			infos.add(info);
		}

		return infos;
	}

	@Override
	public String getUrl() {
		return "";
	}

	@Override
	public List<IRuntimeState> getPreviousInfos(int repositoryId, int start, int end) throws Exception {
		logger.info("Get previous task from gateway server.");

		IRepositoryApi repositoryApi = getRootRepositoryApi(repositoryId);
		List<ItemInstance> instances = repositoryApi.getAdminService().getItemInstances(start, end, IRepositoryApi.GTW_TYPE);

		List<IRuntimeState> taskInfos = new ArrayList<IRuntimeState>();
		if (instances != null) {
			for(ItemInstance instance : instances) {
				taskInfos.add(instance.getState());
			}
		}
		return taskInfos;
	}

	private IRepositoryApi getRootRepositoryApi(int repositoryId) throws Exception {
		IVanillaContext ctx = buildRootVanillaContext();

		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ctx);

		Group grp = new Group();
		grp.setId(-1);

		Repository rep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(repositoryId);

		return new RemoteRepositoryApi(new BaseRepositoryContext(ctx, grp, rep));
	}
}
