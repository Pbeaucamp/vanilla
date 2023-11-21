package bpm.vanilla.api.runtime.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;

import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.api.core.IVanillaAPIManager;
import bpm.vanilla.api.core.model.ItemRunInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceConfiguration;
import bpm.vanilla.platform.core.beans.resources.AbstractD4CIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractType;
import bpm.vanilla.platform.core.beans.resources.KPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.gateway.IGatewayRuntimeConfig;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.components.system.IMailConfig;
import bpm.vanilla.platform.core.components.system.MailConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanillahub.core.IHubWorkflowManager;
import bpm.vanillahub.remote.RemoteAdminManager;
import bpm.vanillahub.remote.RemoteWorkflowManager;
import bpm.workflow.commons.beans.Schedule;
import bpm.workflow.commons.beans.Schedule.Period;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;
import bpm.workflow.commons.remote.IAdminManager;

public class VanillaApiService implements IVanillaAPIManager {

	private IVanillaContext vanillaCtx;

	private IVanillaAPI vanillaApi;
	
	private GatewayComponent gatewayApi;
	private WorkflowService workflowApi;
	private IMdmProvider mdmRemote;
	private VanillaParameterComponent parameterApi;

	public VanillaApiService() {

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		this.vanillaCtx = new BaseVanillaContext(url, login, password);
		this.vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
		
		this.gatewayApi = new RemoteGatewayComponent(vanillaCtx);
		this.workflowApi = new RemoteWorkflowComponent(vanillaCtx);
		this.mdmRemote = new MdmRemote(login, password, url);
		this.parameterApi = new RemoteVanillaParameterComponent(vanillaCtx);
	}

	private HubManagerInfos getWorkflowManager() throws Exception {
		// TODO: Change user with connected user

		IAdminManager manager = new RemoteAdminManager(vanillaCtx.getVanillaUrl(), null, null);
		User launcher = manager.login(vanillaCtx.getLogin(), vanillaCtx.getPassword(), null);
		String sessionId = manager.connect(launcher);

		return new HubManagerInfos(new RemoteWorkflowManager(vanillaCtx.getVanillaUrl(), sessionId, null), launcher);
	}

	private IRepositoryContext getRepositoryContext(IVanillaContext vanillaContext, int groupId, int repositoryId) throws Exception {
		Group group;
		try {
			group = vanillaApi.getVanillaSecurityManager().getGroupById(groupId);
			if (group == null) {
				throw new Exception("Unable to find group for groupId = " + groupId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to find group for groupId = " + groupId, e);
		}

		Repository repository;
		try {
			repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(repositoryId);
			if (repository == null) {
				throw new Exception("Unable to find repository for repositoryId = " + groupId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to find repository for repositoryId = " + groupId, e);
		}

		return new BaseRepositoryContext(vanillaContext, group, repository);
	}

	@Override
	public ContractIntegrationInformations generateIntegration(int repositoryId, int groupId, AbstractD4CIntegrationInformations integrationInfos, boolean modifyMetadata, boolean modifyIntegration) throws Exception {
		IRepositoryContext ctx = getRepositoryContext(vanillaCtx, groupId, repositoryId);
		return vanillaApi.getResourceManager().generateIntegration(ctx, integrationInfos, modifyMetadata, modifyIntegration);
	}

	@Override
	public ContractIntegrationInformations getIntegrationProcessByLimesurvey(String limesurveyId) throws Exception {
		ContractIntegrationInformations integration = mdmRemote.getIntegrationInfosByLimesurvey(limesurveyId);
		buildIntegration(integration);
		return integration;
	}

	@Override
	public ContractIntegrationInformations getIntegrationProcessByContract(int contractId) throws Exception {
		ContractIntegrationInformations integration = mdmRemote.getIntegrationInfosByContract(contractId);
		buildIntegration(integration);
		return integration;
	}

	@Override
	public ContractIntegrationInformations generateKpi(int repositoryId, int groupId, KPIGenerationInformations infos) throws Exception {
		IRepositoryContext ctx = getRepositoryContext(vanillaCtx, groupId, repositoryId);
		return vanillaApi.getResourceManager().generateKPI(ctx, infos);
	}

//	@Override
//	public ContractIntegrationInformations generateSimpleKpi(int repositoryId, int groupId, SimpleKPIGenerationInformations infos) throws Exception {
//		IRepositoryContext ctx = getRepositoryContext(vanillaCtx, groupId, repositoryId);
//		return vanillaApi.getResourceManager().generateIntegration(ctx, infos);
//	}

	@Override
	public List<ContractIntegrationInformations> getIntegrationKPIByDatasetId(String datasetId) throws Exception {
		List<ContractIntegrationInformations> integrations = mdmRemote.getKpiInfosByDatasetId(datasetId);
		buildIntegrations(integrations);
		return integrations;
	}

	@Override
	public List<ContractIntegrationInformations> getIntegrationByOrganisation(String organisation, ContractType type) throws Exception {
		List<ContractIntegrationInformations> integrations = mdmRemote.getIntegrationInfosByOrganisation(organisation, type);
		buildIntegrations(integrations);
		return integrations;
	}

	@Override
	public List<String> getValidationSchemas() throws Exception {
		return vanillaApi.getResourceManager().getValidationSchemas();
	}

	@Override
	public void deleteIntegration(int repositoryId, int groupId, ContractIntegrationInformations infos) throws Exception {
		IRepositoryContext ctx = getRepositoryContext(vanillaCtx, groupId, repositoryId);
		vanillaApi.getResourceManager().deleteIntegration(ctx, infos);
	}
	
	@Override
	public List<Workflow> getVanillaHubs() throws Exception {
		HubManagerInfos hubInfos = getWorkflowManager();
		IHubWorkflowManager hubManager = hubInfos.getHubManager();
		return hubManager.getWorkflows();
	}

	@Override
	public String runVanillaHub(int workflowId, List<Parameter> parameters) throws Exception {
		HubManagerInfos hubInfos = getWorkflowManager();
		IHubWorkflowManager hubManager = hubInfos.getHubManager();

		Workflow workflow = hubManager.getWorkflow(workflowId, true);
		String uuid = hubManager.initWorkflow(workflow);
		hubManager.runWorkflow(workflow, uuid, hubInfos.getLauncher(), parameters);

		return uuid;
	}

	@Override
	public WorkflowInstance getVanillaHubProgress(int workflowId, String uuid) throws Exception {
		HubManagerInfos hubInfos = getWorkflowManager();
		IHubWorkflowManager hubManager = hubInfos.getHubManager();

		Workflow workflow = new Workflow();
		workflow.setId(workflowId);
		return hubManager.getWorkflowProgress(workflow, uuid);
	}
	
	@Override
	public ItemRunInformations getItemInformations(User user, int repositoryId, int groupId, int itemId) throws Exception {
		Repository selectedRepository = new Repository();
		selectedRepository.setId(repositoryId);
		
		Group selectedGroup = new Group();
		selectedGroup.setId(groupId);
		
		IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(vanillaCtx, selectedGroup, selectedRepository));
		
		RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(itemId);

		ObjectIdentifier ident = new ObjectIdentifier(repositoryId, itemId);
		List<VanillaGroupParameter> groupParams = new ArrayList<VanillaGroupParameter>();
		
		IRuntimeConfig config = null;
		if (item.isReport()) {
			config = new ReportRuntimeConfig(ident, null, groupId);
		}
		else if (item.getType() == IRepositoryApi.GTW_TYPE || item.getType() == IRepositoryApi.BIW_TYPE || item.getType() == IRepositoryApi.R_MARKDOWN_TYPE) {
			config = new RuntimeConfiguration(groupId, ident, null);
		}
		if (config != null) {
			groupParams = parameterApi.getParameters(config);
		}

		List<Integer> groupIds;
		try {
			groupIds = repositoryApi.getAdminService().getAllowedGroupId(item);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to get the allowed group for item with id = " + item.getId() + ": " + e.getMessage());
		}

		List<Integer> availableGroupIds = new ArrayList<Integer>();
		try {
			if (groupIds != null) {
				for (Integer grId : groupIds) {
					if (repositoryApi.getAdminService().canRun(item.getId(), grId)) {
						availableGroupIds.add(grId);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to get the allowed group for item with id = " + item.getId() + ": " + e.getMessage());
		}

		List<Group> userGroups;
		try {
			userGroups = vanillaApi.getVanillaSecurityManager().getGroups(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to get the available groups for the user " + user.getLogin());
		}

		List<Group> availableGroups = new ArrayList<Group>();
		for (Group gr : userGroups) {
			for (Integer grId : availableGroupIds) {
				if (gr.getId().equals(grId)) {
					availableGroups.add(gr);
					break;
				}
			}
		}
		
		return new ItemRunInformations(item, groupParams, availableGroups);
	}
	
	@Override
	public String runETL(User user, int repositoryId, int groupId, int itemId, List<VanillaGroupParameter> parameters) throws Exception {
		ObjectIdentifier ident = new ObjectIdentifier(repositoryId, itemId);
		IGatewayRuntimeConfig runtime = new GatewayRuntimeConfiguration(ident, parameters, groupId);

		IRunIdentifier runId = gatewayApi.runGatewayAsynch(runtime, user);
		return runId.getKey();
	}
	
	@Override
	public String runWorkflow(User user, int repositoryId, int groupId, int itemId, List<VanillaGroupParameter> parameters) throws Exception {
		ObjectIdentifier ident = new ObjectIdentifier(repositoryId, itemId);
		IRuntimeConfig runtime = new RuntimeConfiguration(groupId, ident, parameters);

		IRunIdentifier runId = workflowApi.startWorkflowAsync(runtime);
		return runId.getKey();
	}
	
	@Override
	public InputStream runReport(User user, int repositoryId, int groupId, int itemId, String outputName, String format, List<VanillaGroupParameter> parameters, List<String> mails) throws Exception {
		Repository selectedRepository = new Repository();
		selectedRepository.setId(repositoryId);
		
		Group selectedGroup = new Group();
		selectedGroup.setId(groupId);

		IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(vanillaCtx, selectedGroup, selectedRepository));

		RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(itemId);

		if (item.isReport()) {
			ReportingComponent reportingComponent = new RemoteReportRuntime(vanillaCtx);
			if (!item.isOn()) {
				throw new Exception("Item '" + item.getName() + "' is disabled. Enable with ES if you want to run it.");
			}

			AlternateDataSourceConfiguration alternateConfiguration = null;
			try {
				ObjectIdentifier ident = new ObjectIdentifier(repositoryId, item.getId());

				ReportRuntimeConfig config = new ReportRuntimeConfig();

				config.setObjectIdentifier(ident);
				config.setVanillaGroupId(groupId);
				config.setAlternateDataSourceConfiguration(alternateConfiguration);

				// TODO: Manage parameters
				if (parameters != null && !parameters.isEmpty()) {
					config.setParameters(parameters);
				}

				if (format.equalsIgnoreCase("pht")) {
					config.setOutputFormat("ppt");
				}
				else {
					config.setOutputFormat(format);
				}

				InputStream reportStream = reportingComponent.runReport(config, user);
				
				byte currentXMLBytes[] = IOUtils.toByteArray(reportStream);
				ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
				reportStream.close();
				
				String reportName = outputName + "." + format;
				if (mails != null && !mails.isEmpty()) {

					// We set a limit to 5 mails maximum
					if (mails.size() > 5) {
						throw new Exception("You can send a maximum of 5 mails every call.");
					}
					
					for (String mail : mails) {
						byteArrayIs.reset();
						HashMap<String, InputStream> attachement = new HashMap<String, InputStream>();
						attachement.put(reportName, byteArrayIs);

						// text replacements for email string
						StringBuffer htmlText = new StringBuffer();
						htmlText.append("Bonjour,<br/><br/>");
						htmlText.append("Veuillez trouver ci-joint le rapport '" + outputName + "'.<br/><br/>");
						htmlText.append("Cordialement,<br/>");
						htmlText.append("L'équipe BPM-Conseil");

						IMailConfig mailConfig = new MailConfig(mail, "info@bpm-conseil.com", htmlText.toString(), "Rapport Vanilla : " + outputName, true);
						String res = vanillaApi.getVanillaSystemManager().sendEmail(mailConfig, attachement);
					}
				}

				byteArrayIs.reset();
				return byteArrayIs;
			} catch (Exception e) {
				String msg = "Failed to run report, reason : " + e.getMessage();
				throw new Exception(msg);
			}
		}
		
		throw new Exception("Item type not supported yet !");
//		else {
//			switch (item.getType()) {
//			case IRepositoryApi.GED_ENTRY:
//				return openGedDocument(item, api.getContext().getRepository(), api.getContext().getGroup());
//			default:
//				throw new VanillaException("The item type " + IRepositoryApi.TYPES_NAMES[item.getType()] + " is not supported");
//			}		
//		}
	}

	@Override
	public void updateSchedule(Schedule schedule) throws Exception {
		HubManagerInfos hubInfos = getWorkflowManager();
		IHubWorkflowManager hubManager = hubInfos.getHubManager();
		
		Workflow workflow = hubManager.getWorkflow(schedule.getWorkflowId(), false);
		
		bpm.workflow.commons.beans.Schedule hubSchedule = buildSchedule(schedule, workflow.getSchedule());
		
		workflow.setSchedule(hubSchedule);
		hubManager.manageWorkflow(workflow, true);
	}

	private bpm.workflow.commons.beans.Schedule buildSchedule(Schedule schedule, bpm.workflow.commons.beans.Schedule hubSchedule) {
		if (hubSchedule == null) {
			hubSchedule = new bpm.workflow.commons.beans.Schedule();
		}
		hubSchedule.setBeginDate(schedule.getBeginDate());
		hubSchedule.setInterval(schedule.getInterval());
		hubSchedule.setOn(true);
		hubSchedule.setPeriod(Period.valueOf(schedule.getPeriod().getType()));
		hubSchedule.setStopDate(schedule.getStopDate());
		hubSchedule.setWorkflowId(schedule.getWorkflowId());
		return hubSchedule;
	}

	@Override
	public ValidationDataResult validateData(String d4cUrl, String d4cObs, String datasetId, String resourceId, int contractId, List<String> schemas) throws Exception {
		return vanillaApi.getResourceManager().validateData(d4cUrl, d4cObs, datasetId, resourceId, contractId, schemas);
	}
	
	private void buildIntegrations(List<ContractIntegrationInformations> integrations) throws Exception {
		if (integrations != null) {
			for (ContractIntegrationInformations integration : integrations) {
				buildIntegration(integration);
			}
		}
	}

	private void buildIntegration(ContractIntegrationInformations integration) throws Exception {
		if (integration != null) {
			HubManagerInfos hubInfos = getWorkflowManager();
			IHubWorkflowManager hubManager = hubInfos.getHubManager();
			
			if (integration.getHubId() > 0) {
				Workflow workflow = hubManager.getWorkflow(integration.getHubId(), false);
				if (workflow != null) {
					integration.setSchedule(workflow.getSchedule());
				}
			}
		}
	}

	private class HubManagerInfos {

		private IHubWorkflowManager hubManager;
		private User launcher;
		
		public HubManagerInfos(IHubWorkflowManager hubManager, User launcher) {
			this.hubManager = hubManager;
			this.launcher = launcher;
		}

		public IHubWorkflowManager getHubManager() {
			return hubManager;
		}

		public User getLauncher() {
			return launcher;
		}

	}
}
