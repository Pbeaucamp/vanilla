package bpm.gwt.commons.server.security;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;

import bpm.data.viz.core.RemoteDataVizComponent;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.document.management.core.IVdmManager;
import bpm.document.management.core.model.VdmContext;
import bpm.document.management.remote.RemoteVdmManager;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.report.ObjectInputStream;
import bpm.gwt.commons.shared.Email;
import bpm.gwt.commons.shared.GedInformations;
import bpm.gwt.commons.shared.ILoginManager;
import bpm.gwt.commons.shared.InfoShare;
import bpm.gwt.commons.shared.InfoShareMail;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.InfoWebapp;
import bpm.gwt.commons.shared.utils.ExportResult;
import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.smart.core.xstream.ISmartManager;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.update.manager.api.remote.RemoteUpdateManager;
import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.ICommunicationManager;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IResourceManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSystemManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.feedback.Feedback;
import bpm.vanilla.platform.core.beans.feedback.UserInformations;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.components.FreeDashboardComponent;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.system.IMailConfig;
import bpm.vanilla.platform.core.components.system.MailConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteCommunicationManager;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.RemoteVanillaSystemManager;
import bpm.vanilla.platform.core.remote.impl.components.RemoteFdRuntime;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.workflow.commons.beans.IWorkflowManager;

public abstract class CommonSession {
	public static final String SESSION_ID = "sessionID";

	private Logger logger = Logger.getLogger(this.getClass());

	private String sessionId;
	private String serverSessionId;

	private User user;
	private InfoUser infoUser;
	private bpm.document.management.core.model.User aklaboxUser;

	private Group currentGroup;
	private Repository currentRepository;

	private IVanillaContext vanillaContext;

	private IRepositoryApi socket;
	private IRepository repository;

	private IVanillaAPI vanillaApi;
	private IVdmManager aklaManager;
	private ILoginManager loginManager;
	private RemoteUpdateManager updateManager;
	private IMapDefinitionService mapRemote;

	private InputStream pendingNewVersion;

	// Usbed to stock the inputstream of a report every time its needed
	private HashMap<String, ObjectInputStream> reports = new HashMap<String, ObjectInputStream>();

	// Used to stock the metadata for FMDT Driller
	private HashMap<Integer, List<IBusinessModel>> metadatas = new HashMap<Integer, List<IBusinessModel>>();

	// Used to stock streams of file which can be downloaded
	private HashMap<String, ObjectInputStream> streams = new HashMap<String, ObjectInputStream>();

	private String cube;

	// Usefull for GED
	private HashMap<String, DocumentVersion> documents = new HashMap<String, DocumentVersion>();

	// Informations for the Ged Index File
	private GedInformations gedInformations;

	// Manager for workflow's application, they need to be set by the Session of
	// the app
	private IWorkflowManager workflowManager;
	private IResourceManager resourceManager;
	private ISmartManager smartManager;

	public CommonSession() {
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setServerSessionId(String serverSessionId) {
		this.serverSessionId = serverSessionId;
	}

	public String getServerSessionId() {
		return serverSessionId;
	}

	public void setSessionInformations(User user, String vanillaUrl) {
		this.user = user;

		if (vanillaUrl != null && !vanillaUrl.isEmpty()) {
			vanillaApi = new RemoteVanillaPlatform(vanillaUrl, user.getLogin(), user.getPassword());
			vanillaContext = new BaseVanillaContext(vanillaUrl, user.getLogin(), user.getPassword());
		}
	}

	public void initSession(Group group, Repository repository) throws ServiceException {
		this.currentGroup = group;
		this.currentRepository = repository;

		if (group != null && repository != null) {
			logger.info("Creating socket to repository...");
			IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, group, repository);

			socket = new RemoteRepositoryApi(ctx);
			logger.info("Connecting to repository at " + repository.getUrl() + "...");
			initRepository(null);
			logger.info("PortalSession created.");
		}
	}

	public void initAklaboxSession(InfoUser infoUser, boolean raiseException) throws ServiceException {
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		String aklaboxUrl = conf.getProperty(VanillaConfiguration.P_AKLABOX_RUNTIME_URL);
		String aklaboxWebappUrl = conf.getProperty(VanillaConfiguration.P_AKLABOX_WEBAPP_URL);

		if (aklaboxUrl == null || aklaboxUrl.isEmpty() || aklaboxWebappUrl == null || aklaboxWebappUrl.isEmpty()) {
			logger.info("The Aklabox URLs are not configure in 'vanilla.properties' file.");

			infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.AKLABOX, false, false, ""));

			if (raiseException) {
				throw new ServiceException("The Aklabox URLs are not configure in 'vanilla.properties' file.");
			}
			else {
				return;
			}
		}

		VdmContext context = new VdmContext(aklaboxUrl, user.getAklaboxLogin(), user.getAklaboxPassword(), -1);

		logger.info("Connecting to aklabox at " + aklaboxUrl + "...");

		bpm.document.management.core.model.User aklaUser = new bpm.document.management.core.model.User();
		aklaUser.setEmail(user.getAklaboxLogin());
		aklaUser.setPassword(user.getAklaboxPassword());

		try {
			IVdmManager aklaManager = new RemoteVdmManager(context);
			this.aklaboxUser = aklaManager.connect(aklaUser);
			this.aklaManager = aklaManager;
		} catch (Exception e) {
			logger.error("Aklabox connection can't be made: " + e.getMessage());
			infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.AKLABOX, false, true, aklaboxWebappUrl));

			if (raiseException) {
				throw new ServiceException("Aklabox connection can't be made: " + e.getMessage());
			}
			else {
				return;
			}
		}

		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.AKLABOX, true, true, aklaboxWebappUrl));

		logger.info("Aklabox session created.");
	}

	public String initAklaboxConnection(CommonSession session) throws Exception {
		String hash = String.valueOf(new Object().hashCode());
		aklaManager.initSessionFromVanilla(hash, aklaboxUser.getEmail());
		return hash;
	}

	public void sendInfoUser(String login, String password) {
		CommonConfiguration config = CommonConfiguration.getInstance();
		boolean isFeedbackAllowed = config.isFeedbackAllowed();
		String feedbackUrl = config.getFeedbackUrl();

		if (isFeedbackAllowed && feedbackUrl != null && !feedbackUrl.isEmpty()) {
			UserInformations userInfos = new UserInformations();
			userInfos.setApplication(getApplicationId());
			userInfos.setLogin(login);
			userInfos.setOs(getOs());

			ICommunicationManager communicationManager = new RemoteCommunicationManager(feedbackUrl, login, password);
			try {
				communicationManager.sendInfos(userInfos);
			} catch (Exception e) {
				logger.warn("Unable to send feedback informations.");
			}
		}
	}

	public void sendFeedbackMessage(User user, String mail, String message, boolean isSupport) throws ServiceException {
		CommonConfiguration config = CommonConfiguration.getInstance();
		String feedbackUrl = config.getFeedbackUrl();

		Feedback feedback = new Feedback();
		feedback.setEmail(mail);
		feedback.setComment(message);
		feedback.setType(isSupport ? 1 : 0);

		ICommunicationManager communicationManager = new RemoteCommunicationManager(feedbackUrl, user.getLogin(), user.getPassword());
		try {
			communicationManager.sendFeedback(feedback);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Unable to send feedback informations.");
			throw new ServiceException(e.getMessage());
		}
	}

	private String getOs() {
		boolean isLinux = SystemUtils.IS_OS_LINUX;
		boolean isMac = SystemUtils.IS_OS_MAC;

		return isLinux ? UserInformations.OS_LINUX : isMac ? UserInformations.OS_MAC : UserInformations.OS_WINDOWS;
	}

	public void initRepository(Integer type) throws ServiceException {
		if (type == null) {
			try {
				this.repository = new bpm.vanilla.platform.core.repository.Repository(socket);
			} catch (Exception e) {
				String msg = "Repository at " + currentRepository.getUrl() + " could not be contacted : " + e.getMessage();
				logger.error(msg, e);
				throw new ServiceException(msg);
			}
		}
		else {
			try {
				this.repository = new bpm.vanilla.platform.core.repository.Repository(socket, type);
			} catch (Exception e) {
				String msg = "Repository at " + currentRepository.getUrl() + " could not be contacted : " + e.getMessage();
				logger.error(msg, e);
				throw new ServiceException(msg);
			}
		}
	}

	public String getSessionId() {
		return sessionId;
	}

	public User getUser() {
		return user;
	}

	public bpm.document.management.core.model.User getAklaboxUser() {
		return aklaboxUser;
	}

	public Group getCurrentGroup() {
		return currentGroup;
	}

	public Repository getCurrentRepository() {
		return currentRepository;
	}

	public IRepository getRepository() {
		return repository;
	}

	public IRepositoryApi getRepositoryConnection() {
		return socket;
	}

	public String getVanillaRuntimeUrl() {
		return vanillaApi.getVanillaUrl();
	}
	
	public String getVanillaRuntimeExternalUrl() {
		return vanillaApi.getVanillaExternalUrl();
	}

	public IVanillaAPI getVanillaApi() {
		return vanillaApi;
	}

	public IVanillaContext getVanillaContext() {
		return vanillaContext;
	}

	public ReportingComponent getReportingComponent() {
		return new RemoteReportRuntime(getVanillaContext());
	}

	public IGedComponent getGedComponent() {
		return new RemoteGedComponent(getVanillaContext());
	}

	public IVanillaSystemManager getSystemManager() {
		return getVanillaApi().getVanillaSystemManager();
	}

	public VanillaParameterComponent getParameterComponent() {
		return new RemoteVanillaParameterComponent(getVanillaContext());
	}

	public FreeDashboardComponent getDashboardComponent() {
		return new RemoteFdRuntime(getVanillaRuntimeUrl(), getUser().getLogin(), getUser().getPassword());
	}

	public WorkflowService getWorkflowComponent() {
		return new RemoteWorkflowComponent(getVanillaContext());
	}

	public GatewayComponent getGatewayComponent() {
		return new RemoteGatewayComponent(getVanillaContext());
	}

	public ReportHistoricComponent getHistoricComponent() {
		return new RemoteHistoricReportComponent(getVanillaContext());
	}

	public String getApplicationPassword() {
		return null;
	}

	public boolean hasLicence() {
		return false;
	}

	public abstract String getApplicationId();

	public void addReport(String name, ObjectInputStream report) {
		this.reports.put(name, report);
	}

	public ObjectInputStream getReport(String name) {
		return this.reports.get(name);
	}

	public void clearReports() {
		this.reports.clear();
	}

	public void removeReport(String name) {
		this.reports.remove(name);
	}

	public void addMetadata(int metadataId, List<IBusinessModel> bModels) {
		this.metadatas.put(metadataId, bModels);
	}

	public List<IBusinessModel> getMetadata(int metadataId) {
		return this.metadatas.get(metadataId);
	}

	public void clearMetadatas() {
		this.metadatas.clear();
	}

	public String getVanillaSessionId() {
		String currentSession = ((RemoteVanillaSystemManager) getVanillaApi().getVanillaSystemManager()).getCurrentSessionId();
		return currentSession;
	}

	public IVdmManager getAklaboxManager() {
		return aklaManager;
	}

	public void addStream(String name, String format, ByteArrayInputStream stream) {
		if (streams == null) {
			streams = new HashMap<String, ObjectInputStream>();
		}

		ObjectInputStream objectInputStream = streams.get(name);
		if (objectInputStream == null) {
			objectInputStream = new ObjectInputStream();
			objectInputStream.addStream(format, stream);

			this.streams.put(name, objectInputStream);
		}
		else {
			objectInputStream.addStream(format, stream);
		}
	}

	public ByteArrayInputStream getStream(String name, String format) {
		return streams.get(name).getStream(format);
	}

	public ObjectInputStream getStreamAndRemove(String name) {
		return streams.remove(name);
	}

	public ExportResult sendMails(InfoShare infoShare, String key, TypeExport typeExport) {
		logger.info("Sending : " + infoShare.getItemName());

		String currentUserName = user.getName();

		int sentEmail = 0;
		List<String> errors = new ArrayList<>();
		List<String> warnings = new ArrayList<>();

		HashMap<String, ByteArrayInputStream> reports = new HashMap<String, ByteArrayInputStream>();
		if (typeExport == TypeExport.REPORT_GROUP) {
			InfoShareMail infoShareReport = (InfoShareMail) infoShare;
			HashMap<String, String> selectedReports = infoShareReport.getSelectedFolders();
			for (String report : selectedReports.keySet()) {
				ObjectInputStream reportStream = getReport(selectedReports.get(report));
				reports.put(report, reportStream.getStream(infoShare.getFormat()));
			}
		}
		else if (typeExport == TypeExport.MARKDOWN) {
			ByteArrayInputStream stream = getStream(infoShare.getItemName() + "0", infoShare.getFormat());
			reports.put(infoShare.getItemName(), stream);
		}
		else {
			ObjectInputStream reportStream = getReport(key);
			reports.put(infoShare.getItemName(), reportStream.getStream(infoShare.getFormat()));
		}

		IVanillaAPI rootVanillaApi = CommonConfiguration.getInstance().getRootVanillaApi();

		for (Group group : infoShare.getSelectedGroups()) {
			try {
				List<User> users = rootVanillaApi.getVanillaSecurityManager().getUsersForGroup(group);
				for (User user : users) {
					if (user.getBusinessMail() != null || !user.getBusinessMail().isEmpty()) {
						logger.info("Send email : Preparing email for " + user.getName());

						HashMap<String, InputStream> attachement = new HashMap<String, InputStream>();
						for (String reportName : reports.keySet()) {
							String attachementName = reportName + "_" + group.getName() + "." + infoShare.getFormat();

							reports.get(reportName).reset();
							attachement.put(attachementName, reports.get(reportName));
						}

						// text replacements for email string
						String htmlText = infoShare.getEmailText();
						htmlText = htmlText.replace("{TARGET_USERNAME}", user.getName());
						htmlText = htmlText.replace("{TARGET_GROUP}", group.getName());
						htmlText = htmlText.replace("{CURRENT_USERNAME}", currentUserName);
						htmlText = htmlText.replace("{REPORT_NAME}", infoShare.getItemName());

						IMailConfig config = new MailConfig(user.getBusinessMail(), currentUserName, htmlText, "Vanilla Viewer : " + infoShare.getItemName(), true);

						logger.info("Sending email...");
						String res = getSystemManager().sendEmail(config, attachement);
						if (res.contains("<error>")) {
							throw new ServiceException(res.substring("<error>".length(), res.length() - "</error>".length()));
						}
						logger.info("Mailing system says : " + res);

						sentEmail++;
					}
					else {
						String warning = "Ignoring user '" + user.getName() + "' since he doesnt have a business email registered.";
						logger.warn(warning);
						warnings.add(warning);
					}
				}
			} catch (ServiceException e) {
				logger.error(e.getMessage(), e);
				errors.add(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				errors.add(e.getMessage());
			}
		}

		for (Email email : infoShare.getSelectedEmails()) {
			logger.info("Send email : Preparing email for " + email.getEmail());

			HashMap<String, InputStream> attachement = new HashMap<String, InputStream>();
			for (String reportName : reports.keySet()) {
				String attachementName = reportName + "." + infoShare.getFormat();

				reports.get(reportName).reset();
				attachement.put(attachementName, reports.get(reportName));
			}

			// text replacements for email string
			String htmlText = infoShare.getEmailText();
			htmlText = htmlText.replace("{TARGET_USERNAME}", email.getEmail());
			htmlText = htmlText.replace("{TARGET_GROUP}", "Unknown");
			htmlText = htmlText.replace("{CURRENT_USERNAME}", currentUserName);
			htmlText = htmlText.replace("{REPORT_NAME}", infoShare.getItemName());

			IMailConfig config = new MailConfig(email.getEmail(), currentUserName, htmlText, "Vanilla Viewer : " + infoShare.getItemName(), true);

			try {
				logger.info("Sending email...");
				String res = getSystemManager().sendEmail(config, attachement);

				if (res.contains("<error>")) {
					throw new ServiceException(res.substring("<error>".length(), res.length() - "</error>".length()));
				}
				logger.info("Mailing system says : " + res);

				sentEmail++;
			} catch (ServiceException e) {
				logger.error(e.getMessage(), e);
				errors.add(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				errors.add(e.getMessage());
			}
		}

		return new ExportResult(sentEmail, errors, warnings);
	}

	public void setCurrentGroup(Group group) {
		this.currentGroup = group;
	}

	public String getCube() {
		return cube;
	}

	public void setCube(String cube) {
		this.cube = cube;
	}

	public UpdateInformations hasUpdate() throws Exception {
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		String managerUrl = conf.getProperty(VanillaConfiguration.P_UPDATE_MANAGER_URL);

		if (updateManager == null) {
			updateManager = new RemoteUpdateManager(managerUrl);
		}
		UpdateInformations updateInfos = updateManager.hasUpdate();
		return updateInfos;
	}

	public void setLoginManager(ILoginManager loginManager) {
		this.loginManager = loginManager;
	}

	public ILoginManager getLoginManager() {
		return loginManager;
	}

	public boolean isConnectedToVanilla() throws Exception {
		return loginManager == null || loginManager.isConnectedToVanilla();
	}

	public void setWebapplicationRights(InfoUser infoUser) throws Exception {
		CommonConfiguration config = CommonConfiguration.getInstance();
		int groupId = infoUser.getGroup() != null ? infoUser.getGroup().getId() : -1;

		boolean accessFaweb = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.FAWEB);
		String fawebUrl = config.getFawebUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.FAWEB, true, accessFaweb, fawebUrl));

		boolean accessFwr = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.FWR);
		String fwrUrl = config.getFwrUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.FWR, true, accessFwr, fwrUrl));

		boolean accessFdweb = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.FDWEB);
		String fdWebUrl = config.getFdwebUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.FDWEB, true, accessFdweb, fdWebUrl));

		boolean accessMetadataWeb = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.FMDT_WEB);
		String metadataUrl = config.getMetadataUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.FMDT_WEB, true, accessMetadataWeb, metadataUrl));

		boolean accessArchitectWeb = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.ARCHITECT_WEB);
		String architectUrl = config.getArchitectUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.ARCHITECT_WEB, true, accessArchitectWeb, architectUrl));

		boolean accessDataPreparation = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.DATA_PREPARATION);
		String dataPreparationUrl = config.getDataPreparationUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.DATA_PREPARATION, true, accessDataPreparation, dataPreparationUrl));

		boolean accessFmuser = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.FMUSERWEB);
		String fmUserUrl = config.getFmuserwebUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.FMUSERWEB, true, accessFmuser, fmUserUrl));

		boolean accessFmloader = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.FMLOADERWEB);
		String fmLoaderUrl = config.getFmloaderwebUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.FMLOADERWEB, true, accessFmloader, fmLoaderUrl));

		boolean accessFmdesigner = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.FMDESIGNER);
		String fmDesignerUrl = config.getFmDesignerWebUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.FMDESIGNER, true, accessFmdesigner, fmDesignerUrl));

		boolean accessKpiMap = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.FMKPIMAP);
		String fmKpiMapUrl = config.getKpiMapUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.FMKPIMAP, true, accessKpiMap, fmKpiMapUrl));

		boolean accessUpdateManager = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.UPDATE_MANAGER);
		String updateManagerUrl = config.getUpdateManagerUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.UPDATE_MANAGER, true, accessUpdateManager, updateManagerUrl));

		boolean accessAir = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.AIR);
		String airUrl = config.getAirUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.AIR, true, accessAir, airUrl));

		boolean accessVanillaHub = getVanillaApi().getVanillaSecurityManager().canAccessApp(groupId, IRepositoryApi.HUB);
		String hubUrl = config.getHubUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.HUB, true, accessVanillaHub, hubUrl));

		boolean useBirtViewer = config.isUseBirtViewer();
		String birtViewerUrl = config.getBirtViewerUrl();
		infoUser.registerWebapp(new InfoWebapp(IRepositoryApi.BIRT_VIEWER, useBirtViewer, true, birtViewerUrl));

		boolean connectedToCkan = config.getCkanUrl() != null && !config.getCkanUrl().isEmpty();
		String ckanUrl = config.getCkanUrl();
		infoUser.registerWebapp(new InfoWebapp(VanillaConfiguration.P_CKAN_URL, connectedToCkan, true, ckanUrl));
	}

	public GedInformations getGedInformations() {
		return gedInformations;
	}

	public void setGedInformations(GedInformations gedInformations) {
		this.gedInformations = gedInformations;
	}

	public String addDocumentVersion(DocumentVersion docVersion) {
		String key = docVersion.getId() + new Object().hashCode() + "";
		documents.put(key, docVersion);
		return key;
	}

	public DocumentVersion getDocumentVersion(String key) {
		return documents.get(key);
	}

	public IWorkflowManager getWorkflowManager(Locale locale) throws Exception {
		return workflowManager;
	}

	public void setWorkflowManager(IWorkflowManager workflowManager) {
		this.workflowManager = workflowManager;
	}

	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public ISmartManager getSmartManager() {
		return smartManager;
	}

	public void setSmartManager(ISmartManager smartManager) {
		this.smartManager = smartManager;
	}

	public InfoUser getInfoUser() {
		return infoUser;
	}

	public void setInfoUser(InfoUser infoUser) {
		this.infoUser = infoUser;
	}

	public void createDataPreparation(String name, int datasetId) throws ServiceException {
		try {
			int userId = user.getId();

			DataPreparation dataPrep = new DataPreparation();
			dataPrep.setName(name);
			dataPrep.setDatasetId(datasetId);
			dataPrep.setUserId(userId);

			RemoteDataVizComponent dataviz = new RemoteDataVizComponent(getRepositoryConnection());
			dataviz.saveDataPreparation(dataPrep);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a DataPreparation : " + e.getMessage());
		}
	}

	public IMapDefinitionService getMapService() {
		if (mapRemote == null) {
			RemoteMapDefinitionService mapRemote = new RemoteMapDefinitionService();
			mapRemote.setVanillaRuntimeUrl(getVanillaRuntimeUrl());
			this.mapRemote = mapRemote;
		}
		return mapRemote;
	}

	public InputStream getPendingNewVersion() {
		return pendingNewVersion;
	}

	public void setPendingNewVersion(InputStream pendingNewVersion) {
		this.pendingNewVersion = pendingNewVersion;
	}
}
