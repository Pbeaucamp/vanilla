package bpm.vanilla.server.reporting.server;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.FileInformations;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.IRuntimeState;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.UserRunConfigurationParameter;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.disco.DiscoPackage;
import bpm.vanilla.platform.core.beans.disco.DiscoReportConfiguration;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceHolder;
import bpm.vanilla.platform.core.beans.tasks.ITaskState;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.CustomRunIdentifier;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.RunIdentifier;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.commons.server.ServerConfig;
import bpm.vanilla.server.commons.server.commands.CreateTaskCommand;
import bpm.vanilla.server.commons.server.tasks.ITask;
import bpm.vanilla.server.reporting.server.viewer.VanillaViewerBirtCommand;
import bpm.vanilla.server.reporting.server.viewer.VanillaViewerFWRCommand;
import bpm.vanilla.server.reporting.server.viewer.VanillaViewerJasperCommand;

import com.thoughtworks.xstream.XStream;

public class ReportingManagerImpl implements ReportingComponent {

	private Logger logger = Logger.getLogger(this.getClass());

	private XStream xstream;

	private IVanillaComponent component;
	private ReportingServer server;
	private IVanillaAPI vanillaApi;
	private String sessionId;

	public ReportingManagerImpl(IVanillaComponent component, ReportingServer server) {
		this.component = component;
		this.server = server;
		this.vanillaApi = new RemoteVanillaPlatform(buildRootVanillaContext());
	}

	public void init(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public InputStream buildRptDesignFromFWR(IReportRuntimeConfig runtimeConfig, InputStream reportModel) throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		SaveFWRAsBirtCommand cmd = new SaveFWRAsBirtCommand(server, login, password, runtimeConfig, reportModel);
		return cmd.buildRPTDesign();
	}

	@Override
	public boolean checkRunAsynchState(IRunIdentifier runIdentifier) throws Exception {
		int taskId = -1;
		if (runIdentifier instanceof RunIdentifier) {
			taskId = ((RunIdentifier) runIdentifier).getTaskId();
		}
		else if (runIdentifier instanceof CustomRunIdentifier) {
			taskId = ((RunIdentifier) runIdentifier).getTaskId();
		}
		
		ITaskState info = server.getTaskManager().getState(taskId);
		if (info.getTaskState() != ActivityState.ENDED) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public InputStream generateDiscoPackage(IObjectIdentifier objectIdentifier, int groupId, User user) throws Exception {
		Repository rep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(objectIdentifier.getRepositoryId());

		VanillaConfiguration vconf = ConfigurationManager.getInstance().getVanillaConfiguration();

		Group group = new Group();
		group.setId(groupId);

		IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), vconf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), vconf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD)), group, rep));

		RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(objectIdentifier.getDirectoryItemId());
		String modelXml = repositoryApi.getRepositoryService().loadModel(item);

		DiscoPackage discoPackage = null;
		try {
			discoPackage = (DiscoPackage) xstream.fromXML(modelXml);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to deserialize the disco package.", e);
		}

		if (discoPackage != null) {

			ByteArrayOutputStream dest = new ByteArrayOutputStream();
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

			for (DiscoReportConfiguration config : discoPackage.getConfigs()) {

				RepositoryItem itemToRun = config.getItem();

				if (itemToRun.getType() == IRepositoryApi.CUST_TYPE || itemToRun.getType() == IRepositoryApi.FWR_TYPE) {

					for (String format : config.getSelectedFormats()) {

						IObjectIdentifier identifier = new ObjectIdentifier(rep.getId(), config.getItem().getId());

						ReportRuntimeConfig runtimeConfig = new ReportRuntimeConfig(identifier, config.getSelectedParameters(), config.getGroup().getId());
						runtimeConfig.setOutputFormat(format);

						try {
							InputStream is = runReport(runtimeConfig, user);

							byte data[] = new byte[2048];
							ZipEntry entry = new ZipEntry(config.getItem().getItemName() + "." + format.toLowerCase());
							out.putNextEntry(entry);
							int count;
							while ((count = is.read(data, 0, 2048)) != -1) {
								out.write(data, 0, count);
							}
							is.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				else if (itemToRun.getType() == IRepositoryApi.FAV_TYPE) {
					
				}
				else {
					throw new Exception("This item is not supported for this package.");
				}
			}
			out.flush();
			out.close();
			return new ByteArrayInputStream(dest.toByteArray());
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public AlternateDataSourceHolder getAlternateDataSourcesConnections(IReportRuntimeConfig config, User user) throws Exception {
		Repository rep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(config.getObjectIdentifier().getRepositoryId());
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(config.getVanillaGroupId());
		IRepositoryApi sock = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), user.getLogin(), user.getPassword()), group, rep));

		RepositoryItem item = sock.getRepositoryService().getDirectoryItem(config.getObjectIdentifier().getDirectoryItemId());

		List<RepositoryItem> requested = null;
		try {
			requested = sock.getRepositoryService().getNeededItems(item.getId());
		} catch (Exception ex) {
			throw new Exception("Unable to find the Report DirectoryItem dependancies - " + ex.getMessage(), ex);
		}

		HashMap<String, List<String>> alternateConnectionMap = new HashMap<String, List<String>>();

		if (item.getType() != IRepositoryApi.FWR_TYPE && (item.getType() != IRepositoryApi.CUST_TYPE) && item.getSubtype() != IRepositoryApi.BIRT_REPORT_SUBTYPE) {
			// ONLY FWR are supported
			return new AlternateDataSourceHolder(alternateConnectionMap);
		}

		HashMap<Integer, HashMap<String, List<String>>> fmdtDetail = new HashMap<Integer, HashMap<String, List<String>>>();

		for (RepositoryItem it : requested) {
			if (it.getType() == IRepositoryApi.FMDT_TYPE) {

				String xml = null;
				try {
					xml = sock.getRepositoryService().loadModel(it);
				} catch (Exception ex) {
					throw new Exception("Unable to load FMDT Model - " + ex.getMessage(), ex);
				}

				try {
					Document xmlDoc = DocumentHelper.parseText(xml);

					// <fmdtDataSourceName, List<ConnectionNames> >
					HashMap<String, List<String>> fmdtDsInfo = new HashMap<String, List<String>>();

					for (Element eDataSource : (List<Element>) xmlDoc.getRootElement().selectNodes("//sqlDataSource")) {

						List<Element> eConnection = eDataSource.elements("sqlConnection");

						if (eConnection.size() > 1) {

							List<String> connectionNames = new ArrayList<String>();
							for (Element e : eConnection) {
								connectionNames.add(e.element("name").getText());
							}
							fmdtDsInfo.put(eDataSource.element("name").getText(), connectionNames);
						}

					}

					fmdtDetail.put(it.getId(), fmdtDsInfo);

				} catch (Exception ex) {
					throw new Exception("Error when parsing FMDT Model - " + ex.getMessage(), ex);
				}
			}
		}

		// load FWR
		if (item.getType() == IRepositoryApi.FWR_TYPE) {
			String xml = sock.getRepositoryService().loadModel(item);
			Document xmlDoc = DocumentHelper.parseText(xml);

			for (Element eDataSource : (List<Element>) xmlDoc.getRootElement().selectNodes("//datasource")) {

				// that the data source name according to FWR, not fmdt?
				String fmdtDataSourceName = eDataSource.elementText("fmdtDataSourceName");
				//
				String fwrDataSourceName = eDataSource.elementText("name");
				Integer metadataId = Integer.parseInt(eDataSource.elementText("itemId"));

				for (Integer k : fmdtDetail.keySet()) {
					if (metadataId != null && k.intValue() == metadataId.intValue()) {
						List<String> connectionNames = null;
						if (fmdtDataSourceName != null) {
							connectionNames = fmdtDetail.get(k).get(fmdtDataSourceName);

						}
						else {
							for (String ks : fmdtDetail.get(k).keySet()) {
								connectionNames = fmdtDetail.get(k).get(ks);
							}
						}
						alternateConnectionMap.put(fwrDataSourceName, connectionNames);

					}

				}
			}
		}

		return new AlternateDataSourceHolder(alternateConnectionMap);
	}

	@Override
	public VanillaParameter getReportParameterValues(User user, IReportRuntimeConfig runtimeConfig, String parameterName, List<VanillaParameter> dependanciesValues) throws Exception {
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(runtimeConfig.getVanillaGroupId());

		RepositoryItem item = getItem(runtimeConfig, group);

		if (item != null) {
			if (item.getType() == IRepositoryApi.CUST_TYPE && item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
				VanillaViewerBirtCommand cmd = new VanillaViewerBirtCommand(server, runtimeConfig, user);
				return cmd.getParameterValuesWithCascading(parameterName, dependanciesValues);
			}
			else if (item.getType() == IRepositoryApi.CUST_TYPE && item.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
				VanillaViewerJasperCommand cmd = new VanillaViewerJasperCommand(server, runtimeConfig, user);
				return cmd.getParameterValuesWithCascading(parameterName, dependanciesValues);
			}
			else if (item.getType() == IRepositoryApi.FWR_TYPE) {
				VanillaViewerFWRCommand cmd = new VanillaViewerFWRCommand(server, runtimeConfig, user);
				return cmd.getParameterValuesWithCascading(parameterName, dependanciesValues);
			}
		}

		throw new Exception("Unable to get parameters for this report.");
	}

	@Override
	public List<VanillaGroupParameter> getReportParameters(User user, IReportRuntimeConfig runtimeConfig) throws Exception {
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(runtimeConfig.getVanillaGroupId());

		RepositoryItem item = getItem(runtimeConfig, group);

		if (item != null) {
			if (item.getType() == IRepositoryApi.CUST_TYPE && item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
				VanillaViewerBirtCommand cmd = new VanillaViewerBirtCommand(server, runtimeConfig, user);
				return cmd.getReportParameters();
			}
			else if (item.getType() == IRepositoryApi.CUST_TYPE && item.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
				VanillaViewerJasperCommand cmd = new VanillaViewerJasperCommand(server, runtimeConfig, user);
				return cmd.getReportParameters();
			}
			else if (item.getType() == IRepositoryApi.FWR_TYPE) {
				VanillaViewerFWRCommand cmd = new VanillaViewerFWRCommand(server, runtimeConfig, user);
				return cmd.getReportParameters();
			}
		}

		throw new Exception("Unable to get parameters for this report.");
	}

	@Override
	public InputStream loadGeneratedReport(IRunIdentifier identifier) throws Exception {
		String fName = ((ReportingServerConfig) server.getConfig()).getGenerationFolder();
		
		if (fName == null) {
			logger.error("Unable to find the Task with id " + ((RunIdentifier) identifier).getTaskId() + ". Cannot upload the generated file.");
		}

		File report = new File(fName + "/" + ((RunIdentifier) identifier).getTaskId());

		if (report == null || !report.exists()) {
			logger.error("Unable to find the Task with id " + ((RunIdentifier) identifier).getTaskId() + ". Cannot upload the generated file.");
		}

		FileInputStream fis = new FileInputStream(report);
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(IOUtils.toByteArray(fis));
			return bis;
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			fis.close();
			report.delete();
		}
	}

	@Override
	public InputStream runReport(IReportRuntimeConfig runtimeConfig, User user) throws Exception {
		RunIdentifier run = runReportAsynch(user, runtimeConfig, null, false);

		ITaskState info = null;
		while ((info = server.getTaskManager().getState(((RunIdentifier) run).getTaskId())).getTaskState() != ActivityState.ENDED) {
			Thread.sleep(500);
		}

		if (info != null) {
			if (info.getTaskResult() == ActivityResult.FAILED) {
				throw new VanillaException(info.getFailingCause());
			}
		}

		return loadGeneratedReport(run);
	}

	private void saveReportConfig(User user, IReportRuntimeConfig runtimeConfig) {
		try {
			ReportRuntimeConfig conf = (ReportRuntimeConfig) runtimeConfig;
			UserRunConfiguration userRunConfiguration = new UserRunConfiguration();
			userRunConfiguration.setDescription(conf.getSaveDescription());
			userRunConfiguration.setIdItem(runtimeConfig.getObjectIdentifier().getDirectoryItemId());
			userRunConfiguration.setIdRepository(conf.getObjectIdentifier().getRepositoryId());
			userRunConfiguration.setIdUser(user.getId());
			userRunConfiguration.setName(conf.getSaveName());

			for (VanillaGroupParameter grpParam : runtimeConfig.getParametersValues()) {
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
	public InputStream runReport(IReportRuntimeConfig runtimeConfig, InputStream reportModel, User user, boolean isDisco) throws Exception {
		String modelXMl = new String(Base64.decodeBase64(IOUtils.toByteArray(reportModel)));

		RunIdentifier run = runReportAsynch(user, runtimeConfig, modelXMl, isDisco);
		return loadGeneratedReport(run);
	}

	@Override
	public RunIdentifier runReportAsynch(IReportRuntimeConfig runtimeConfig, User user) throws Exception {
		return runReportAsynch(user, runtimeConfig, null, false);
	}

	private RunIdentifier runReportAsynch(User user, IReportRuntimeConfig runtimeConfig, String model, boolean isDisco) throws Exception {
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(runtimeConfig.getVanillaGroupId());

		RepositoryItem item = getItem(runtimeConfig, group);

		if (item != null) {
			checkRole(item, group);
			canBeRun(runtimeConfig, group);

			// save the user runtime configuration
			if (runtimeConfig instanceof ReportRuntimeConfig) {
				if (((ReportRuntimeConfig) runtimeConfig).isSaveConfig()) {
					saveReportConfig(user, runtimeConfig);
				}
			}

			GenerateReportCommand cmd = new GenerateReportCommand(runtimeConfig, server, user.getLogin(), user.getPassword(), sessionId);
			int taskId = ((Long) cmd.addTaskToQueue()).intValue();

			RunIdentifier id = new RunIdentifier(component.getIdentifier(), taskId);
			return id;
		}
		else if (model != null && !model.isEmpty()) {
			GenerateReportFromModelCommand cmd = new GenerateReportFromModelCommand(runtimeConfig, server, user.getLogin(), user.getPassword(), model, sessionId, isDisco);
			int taskId = ((Long) cmd.addTaskToQueu()).intValue();

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
			if (r.getType().equals(IRepositoryApi.TYPES_NAMES[it.getType()]) && r.getGrants().contains("E")) {
				return;
			}
		}

		throw new VanillaException("No Role with the Execute grant has been associated to the Group " + group.getName() + " on " + IRepositoryApi.TYPES_NAMES[it.getType()] + " Objects");
	}

	public void canBeRun(IRuntimeConfig runtimeConfig, Group group) throws Exception {
		Repository rep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(runtimeConfig.getObjectIdentifier().getRepositoryId());

		if (rep == null) {
			throw new VanillaException("The Repository with id=" + runtimeConfig.getObjectIdentifier().getRepositoryId() + " is not available.");
		}

		IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(buildRootVanillaContext(), group, rep));
		if (!repositoryApi.getAdminService().isDirectoryItemAccessible(runtimeConfig.getObjectIdentifier().getDirectoryItemId(), runtimeConfig.getVanillaGroupId())) {
			throw new VanillaException("The VanillaObject " + runtimeConfig.getObjectIdentifier().toString() + " is not available for the Group " + group.getName());
		}

		if (!repositoryApi.getAdminService().canRun(runtimeConfig.getObjectIdentifier().getDirectoryItemId(), runtimeConfig.getVanillaGroupId())) {
			throw new VanillaException("The VanillaObject " + runtimeConfig.getObjectIdentifier().toString() + " cannot be run by the Group " + group.getName());
		}
	}

	private IVanillaContext buildRootVanillaContext() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = config.getVanillaServerUrl();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		return new BaseVanillaContext(vanillaUrl, login, password);
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

		for (ITask task : server.getTaskManager().getAllTasks()) {
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

		TaskInfo info = new TaskInfo(String.valueOf(task.getId()), task.getClass().getName(), task.getObjectIdentifier().getDirectoryItemId(), itemName);

		info.setCreationDate(task.getTaskState().getCreationDate());
		info.setDurationTime(task.getTaskState().getDuration());
		info.setElapsedTime(task.getTaskState().getElapsedTime());
		info.setFailureCause(task.getTaskState().getFailingCause());
		info.setGroupName(String.valueOf(task.getGroupId()));
		info.setRepositoryId(task.getObjectIdentifier().getRepositoryId());
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
		logger.info("Get previous task from reporting server.");
		IRepositoryApi repositoryApi = getRootRepositoryApi(repositoryId);
		List<ItemInstance> instances = repositoryApi.getAdminService().getItemInstances(start, end, IRepositoryApi.CUST_TYPE);

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

	@Override
	public VanillaParameter getReportParameterValues(User user, IReportRuntimeConfig runtimeConfig, String parameterName, List<VanillaParameter> dependanciesValues, InputStream model) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VanillaGroupParameter> getReportParameters(User user, IReportRuntimeConfig runtimeConfig, InputStream model) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileInformations stockReportBackground(IRunIdentifier identifier) throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String backgroundFolderPath = config.getProperty(VanillaConfiguration.P_VANILLA_SERVER_BACKGROUND_FOLDER);
		
		File backgroundFolder = new File(backgroundFolderPath);
		if (!backgroundFolder.exists()) {
			backgroundFolder.mkdirs();
		}
		
		String fName = ((ReportingServerConfig) server.getConfig()).getGenerationFolder();
		if (fName == null) {
			throw new Exception("Unable to find the Task with id " + ((CustomRunIdentifier) identifier).getTaskId() + ". Cannot upload the generated file.");
		}

		String sourcePath = fName + "/" + ((CustomRunIdentifier) identifier).getTaskId();
		File report = new File(sourcePath);
		if (report == null || !report.exists()) {
			throw new Exception("Unable to find the Task with id " + ((CustomRunIdentifier) identifier).getTaskId() + ". Cannot upload the generated file.");
		}
		
		int key = new Object().hashCode();
		String targetPath = backgroundFolderPath.endsWith("/") ? backgroundFolderPath + key : backgroundFolderPath + "/" + key;

		Path pathSource = Paths.get(sourcePath);
		Path pathTarget = Paths.get(targetPath);
		Files.copy(pathSource, pathTarget, StandardCopyOption.REPLACE_EXISTING);

		return new FileInformations(targetPath, report.length());
	}
}
