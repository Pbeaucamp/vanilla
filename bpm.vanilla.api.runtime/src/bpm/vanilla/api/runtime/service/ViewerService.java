package bpm.vanilla.api.runtime.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.vanilla.api.core.IAPIManager.ViewerMethod;
import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.api.runtime.dto.CommentInformations;
import bpm.vanilla.api.runtime.dto.CommentRestitutionInformations;
import bpm.vanilla.api.runtime.dto.CommentValidationInformations;
import bpm.vanilla.api.runtime.dto.CommentsInformations;
import bpm.vanilla.api.runtime.dto.OpenDirectories;
import bpm.vanilla.api.runtime.dto.FasdItem;
import bpm.vanilla.api.runtime.dto.GedItem;
import bpm.vanilla.api.runtime.dto.HistorizeConfig;
import bpm.vanilla.api.runtime.dto.LaunchReportInformations;
import bpm.vanilla.api.runtime.dto.OLAPDirectory;
import bpm.vanilla.api.runtime.dto.PortailItemReportsGroup;
import bpm.vanilla.api.runtime.dto.PortailRepositoryItem;
import bpm.vanilla.api.runtime.dto.PortalViewerItem;
import bpm.vanilla.api.runtime.dto.ViewerItem;
import bpm.vanilla.api.runtime.dto.RepositoryComponent;
import bpm.vanilla.api.runtime.utils.Constants;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.UserRunConfigurationParameter;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition.TypeComment;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.comments.CommentValue.CommentStatus;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceConfiguration;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceHolder;
import bpm.vanilla.platform.core.beans.validation.UserValidation;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.components.historic.HistoricRuntimeConfiguration;
import bpm.vanilla.platform.core.components.historic.HistorizationConfig.HistorizationTarget;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ViewerService {

	private IVanillaSecurityManager vanillaSecurityManager;
	private IRepositoryManager repositoryManager;
	private IVanillaContext vanillaCtx;

	public ViewerService() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String root = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		this.vanillaCtx = new BaseVanillaContext(url, root, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);

		this.vanillaSecurityManager = vanillaApi.getVanillaSecurityManager();
		this.repositoryManager = vanillaApi.getVanillaRepositoryManager();

		// IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx,
		// session.getGroup(), session.getRepository());
	}

	public String dispatchAction(String method, JSONArray parameters) throws Exception {
		ViewerMethod viewerMEthod = ViewerMethod.valueOf(method);

		switch (viewerMEthod) {
		case LOAD_VIEWERS:
			return loadViewers(parameters);
		case GET_VIEWER_INFORMATION:
			return getViewerInformations(parameters);
		case HISTORIZE_VIEWER:
			return historizeViewer(parameters);
		case SAVE_VIEWER_CONFIG:
			return saveViewerConfig(parameters);
		default:
			break;
		}

		throw new VanillaApiException(VanillaApiError.METHOD_DOES_NOT_EXIST);
	}

	public InputStream dispatchStreamAction(String method, JSONArray parameters) throws Exception {
		ViewerMethod viewerMEthod = ViewerMethod.valueOf(method);

		switch (viewerMEthod) {
		case OPEN_VIEWER:
			return openViewer(parameters);
		default:
			break;
		}

		throw new VanillaApiException(VanillaApiError.METHOD_DOES_NOT_EXIST);
	}

	public String loadViewers(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		String mode = parameters.getString(2);
		String openedDirs = parameters.getString(3);

		RemoteRepositoryApi api;
		try {
			Group group = null;
			try {
				group = vanillaSecurityManager.getGroupById(groupID);
			} catch (Exception e) {
				e.printStackTrace();
				throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
			}
			if (group == null) {
				throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
			}
			Repository repo = null;
			try {
				repo = repositoryManager.getRepositoryById(repositoryID);
			} catch (Exception e) {
				e.printStackTrace();
				throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
			}
			IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, group, repo);
			api = new RemoteRepositoryApi(ctx);

		} catch (Exception e1) {
			e1.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
		}

		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		List<RepositoryComponent> repBrowser = new ArrayList<>();

		switch (mode) {
		case Constants.REPOSITORY:
			OpenDirectories dirOpen = mapper.readValue(openedDirs, OpenDirectories.class);
			repBrowser = browseRepository(api, rep, (RepositoryDirectory) null, dirOpen);
			break;
		case Constants.LAST_USED:
			repBrowser = browseItems(api, api.getWatchListService().getLastConsulted());
			break;
		default:
			break;
		}

		return mapper.writeValueAsString(repBrowser);
	}

	public String getViewerInformations(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		String userLogin = parameters.getString(2);
		int reportID = parameters.getInt(3);

		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(userLogin);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}

		RemoteRepositoryApi api = getApi(repositoryID, groupID);
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);
		PortailRepositoryItem reportItem = new PortailRepositoryItem(rep.getItem(reportID), "REPORT");

		LaunchReportInformations infos = getLaunchReportInformations(reportItem, api.getContext().getGroup(), api.getContext().getRepository(), user);
		JSONObject obj = new JSONObject(infos);
		return obj.toString();

	}

	public InputStream openViewer(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		String userLogin = parameters.getString(2);
		int reportID = parameters.getInt(3);
		String format = parameters.getString(4);
		String groupParametersStr = parameters.getString(5);

		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(userLogin);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}

		RemoteRepositoryApi api = getApi(repositoryID, groupID);
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);

		RepositoryItem item = rep.getItem(reportID);
		ObjectMapper mapper = new ObjectMapper();

		List<VanillaGroupParameter> groupParameters;
		if ((groupParametersStr == null) || (groupParametersStr.equals(""))) {
			groupParameters = null;
		}
		else {
			groupParameters = mapper.readValue(groupParametersStr, new TypeReference<List<VanillaGroupParameter>>() {
			});
		}

		// TODO: Manage format
		if(item.isReport()) {
			return runReport(item, api.getContext().getRepository(), api.getContext().getGroup(), user, format, groupParameters);
		}
		else {
			switch (item.getType()) {
			case IRepositoryApi.GED_ENTRY:
				return openGedDocument(item, api.getContext().getRepository(), api.getContext().getGroup());
			default:
				throw new VanillaException("The item type " + IRepositoryApi.TYPES_NAMES[item.getType()] + " is not supported");
			}		
		}


	}

	public String historizeViewer(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		String userLogin = parameters.getString(2);
		int reportID = parameters.getInt(3);
		String histoConfigStr = parameters.getString(4);

		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(userLogin);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}
		
		RemoteRepositoryApi api = getApi(repositoryID, groupID);
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);

		RepositoryItem item = rep.getItem(reportID);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);

		HistorizeConfig histoConfig = mapper.readValue(histoConfigStr, HistorizeConfig.class);
		if(histoConfig.getHistoId() == -1) histoConfig.setHistoId(null);

		Repository repo = api.getContext().getRepository();
		Group group = api.getContext().getGroup();
		
		if (item.getType() != IRepositoryApi.GED_ENTRY) {
			InputStream viewerIs = runReport(item, repo, group, user, histoConfig.getOutputFormat(), histoConfig.getGroupParameters());
			byte currentXMLBytes[] = IOUtils.toByteArray(viewerIs);
			ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
			
			HistoricRuntimeConfiguration runtimeConfig = configHistorizedViewer(item, repo, group, user, histoConfig);
			RemoteHistoricReportComponent historicComponent = new RemoteHistoricReportComponent(vanillaCtx);
			int id = historicComponent.historizeReport(runtimeConfig, byteArrayIs);
			RemoteGedComponent gedComponent = new RemoteGedComponent(vanillaCtx);
			
			if(item.isRealtimeGed()) {
				runtimeConfig.setDirectoryTargetId(item.getDirectoryId() > 0 ? item.getDirectoryId() : null);
				gedComponent.indexExistingFile(runtimeConfig, id, item.isCreateEntry());
			}
			
			DocumentVersion docVersion = gedComponent.getDocumentVersionById(id);
			GedDocument gedDoc = gedComponent.getDocumentDefinitionById(docVersion.getDocumentId());
			return mapper.writeValueAsString(new GedItem(gedDoc));
		}
		
		JSONObject json = new JSONObject();
		return json.toString();
	}
	
	
	public String saveViewerConfig(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		String userLogin = parameters.getString(2);
		int reportID = parameters.getInt(3);
		String configName = parameters.getString(4);
		String configDescription = parameters.getString(5);
		String groupParametersStr = parameters.getString(6);
		
		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(userLogin);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}
		
		RemoteRepositoryApi api = getApi(repositoryID, groupID);
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);

		RepositoryItem item = rep.getItem(reportID);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		List<VanillaGroupParameter> groupParameters;
		if ((groupParametersStr == null) || (groupParametersStr.equals(""))) {
			groupParameters = null;
		}
		else {
			groupParameters = mapper.readValue(groupParametersStr, new TypeReference<List<VanillaGroupParameter>>() {
			});
		}
		
		Repository repo = api.getContext().getRepository();
		Group group = api.getContext().getGroup();
		
		System.out.println(mapper.writeValueAsString(groupParameters));
		
		UserRunConfiguration config = createViewerConfig(repo,user,item,configName,configDescription,groupParameters);
		if(config != null) {
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
			System.out.println(mapper.writeValueAsString(config));
			vanillaApi.getVanillaPreferencesManager().addUserRunConfiguration(config);
		}
		
		return mapper.writeValueAsString(getLaunchReportInformations(new PortailRepositoryItem(item,"REPORT"),group,repo,user));
	}

	// get api object
	private RemoteRepositoryApi getApi(int repositoryID, int groupID) throws Exception {
		Group group = null;
		try {
			group = vanillaSecurityManager.getGroupById(groupID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		if (group == null) {
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		Repository repo = null;
		try {
			repo = repositoryManager.getRepositoryById(repositoryID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
		}

		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, group, repo);
		RemoteRepositoryApi api = new RemoteRepositoryApi(ctx);

		return api;
	}

	// Browse repository
	private List<RepositoryComponent> browseRepository(RemoteRepositoryApi api, IRepository rep, RepositoryDirectory currentDir, OpenDirectories dirOpen) {
		List<RepositoryComponent> repComponents = new ArrayList<>();

		try {
			for (RepositoryDirectory childDir : rep.getChildDirectories(currentDir)) {
				if (childDir.isShowed()) {
					List<RepositoryComponent> children = null;
					if ((dirOpen.isOpenAll()) || (isOnDirList(dirOpen, childDir))) {
						children = browseRepository(api, rep, childDir, dirOpen);
					}
					repComponents.add(new OLAPDirectory(childDir, children));
				}
			}

			repComponents.addAll(browseItems(api,rep.getItems((RepositoryDirectory)currentDir)));

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.FILE_NOT_FOUND);
		}

		return repComponents;

	}

	private List<RepositoryComponent> browseItems(RemoteRepositoryApi api, List<RepositoryItem> itList) throws Exception {
		List<RepositoryComponent> repBrowser = new ArrayList<>();

		for (RepositoryItem it : itList) {
			if (it.isDisplay() && it.getName() != null) {
				if (it.getType() == IRepositoryApi.FASD_TYPE) {
					String fasdxml = api.getRepositoryService().loadModel(it);
					repBrowser.add(new FasdItem(it, fasdxml));
				}
				else if (it.isReport()) {
					repBrowser.add(new ViewerItem(it, "Report"));
				}
				else if (IRepositoryApi.TYPES_NAMES[it.getType()] == "PORTAL") {
					String portalxml = api.getRepositoryService().loadModel(it);
					repBrowser.add(new PortalViewerItem(it,portalxml));
				}
				else if (it.getType() != IRepositoryApi.FAV_TYPE) {
					repBrowser.add(new ViewerItem(it, IRepositoryApi.TYPES_NAMES[it.getType()]));
				}
			}
		}

		return repBrowser;
	}

	public boolean isOnDirList(OpenDirectories dirList, RepositoryDirectory dir) {
		for (int dirID : dirList.getOpenedDirs()) {
			if (dirID == dir.getId())
				return true;
		}

		return false;
	}

	public LaunchReportInformations getLaunchReportInformations(PortailRepositoryItem item, Group selectedGroup, Repository repo, User user) throws Exception {
		// CommonSession session = getSession();
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, selectedGroup, repo);
		RemoteRepositoryApi api = new RemoteRepositoryApi(ctx);
		IRepositoryApi socket = new RemoteRepositoryApi(ctx);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
		IRuntimeConfig config = null;
		AlternateDataSourceHolder alternate = null;

		List<String> availableLocales = new ArrayList<String>();
		List<UserRunConfiguration> configs = null;

		List<VanillaGroupParameter> groupParams = new ArrayList<VanillaGroupParameter>();
		List<LaunchReportInformations> launchReports = new ArrayList<LaunchReportInformations>();
		List<GedDocument> gedDocs = null;

		// TODO enlever la gestion des groupes
		if (item instanceof PortailItemReportsGroup) {
			List<PortailRepositoryItem> reports = ((PortailItemReportsGroup) item).getReports();
			if (reports != null) {
				for (PortailRepositoryItem report : reports) {
					try {
						LaunchReportInformations itemInfo = getLaunchReportInformations(report, selectedGroup, repo, user);
						if (itemInfo != null) {
							launchReports.add(itemInfo);
						}
						else {
							// logger.error("Unable to find runtime informations
							// for item " + report.getName());
						}
					} catch (Exception e) {
						e.printStackTrace();
						// logger.error("Unable to find runtime informations for
						// item " + report.getName());
					}
				}
			}
		}
		else {
			ObjectIdentifier ident = new ObjectIdentifier(repo.getId(), item.getId());
			
			if(item.getType() != IRepositoryApi.GED_ENTRY) {
				RemoteHistoricReportComponent historicComponent = new RemoteHistoricReportComponent(vanillaCtx);
				gedDocs = historicComponent.getReportHistoric(ident, selectedGroup.getId());			
			}
			
			if (item.isReport()) {
				config = new ReportRuntimeConfig(ident, null, selectedGroup.getId());
				ReportingComponent reportingComponent = new RemoteReportRuntime(vanillaCtx);
				
				alternate = reportingComponent.getAlternateDataSourcesConnections((ReportRuntimeConfig) config, user);
				availableLocales = getReportAvailableLocales(api, item.getItem(), selectedGroup);
			}
			else if (item.getType() == IRepositoryApi.GTW_TYPE || item.getType() == IRepositoryApi.BIW_TYPE || item.getType() == IRepositoryApi.R_MARKDOWN_TYPE) {
				config = new RuntimeConfiguration(selectedGroup.getId(), ident, null);
			}

			if (config != null) {
				VanillaParameterComponent paramComponent = new RemoteVanillaParameterComponent(vanillaCtx);
				groupParams = paramComponent.getParameters(config);

				try {

					configs = vanillaApi.getVanillaPreferencesManager().getUserRunConfigurationsByUserIdObjectId(user.getId(), ident);
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception(e.getMessage());
				}
			}
		}

		List<Integer> groupIds;
		try {

			groupIds = socket.getAdminService().getAllowedGroupId(item.getItem());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to get the allowed group for item with id = " + item.getItem().getId() + ": " + e.getMessage());
		}

		List<Integer> availableGroupIds = new ArrayList<Integer>();
		try {
			if (groupIds != null) {
				for (Integer groupId : groupIds) {
					if (socket.getAdminService().canRun(item.getItem().getId(), groupId)) {
						availableGroupIds.add(groupId);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to get the allowed group for item with id = " + item.getItem().getId() + ": " + e.getMessage());
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

		// TODO enlever la partie validation
		Validation validation;
		try {
			validation = socket.getRepositoryService().getValidation(item.getId());
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new Exception("Unable to get the available validation for the item " + item.getName());
		}

		CommentsInformations commentsInformations = getRestitutionComments(repo.getId(), user.getId(), vanillaApi, item.getItem(), validation, false);

		List<ItemMetadataTableLink> links;
		try {
			links = socket.getRepositoryService().getMetadataLinks(item.getId());
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new Exception("Unable to get the metadata links for the item " + item.getName());
		}

		LaunchReportInformations launchReport = new LaunchReportInformations(item, selectedGroup, groupParams, configs, commentsInformations, validation, availableLocales, alternate, availableGroups, links,gedDocs);
		launchReport.setReports(launchReports);
		return launchReport;
	}

	private List<String> getReportAvailableLocales(RemoteRepositoryApi api, RepositoryItem item, Group selectedGroup) throws Exception {
		List<String> availableLocales = new ArrayList<String>();

		// logger.info("Getting Resources for itemId " + item.getName());
		try {
			List<LinkedDocument> docs = api.getRepositoryService().getLinkedDocumentsForGroup(item.getId(), selectedGroup.getId());

			// logger.info("Found " + docs.size() + " linked doc(s)");

			for (LinkedDocument doc : docs) {
				String[] tokens = doc.getName().split("_");
				if (tokens.length < 2 || !doc.getName().contains(".properties")) {
					// logger.info("Ignoring linkeddoc : " + doc.getName());
					continue;
				}
				String locale = tokens[tokens.length - 2] + "_" + tokens[tokens.length - 1].replace(".properties", "");
				availableLocales.add(locale);
			}
			// logger.info("Found " + availableLocales.size() + " supported
			// locales.");
		} catch (Exception e) {
			String errMsg = "Failed to retrieve LinkedDocuments for item " + item.getId() + ",reason : " + e.getMessage();
			// logger.error(errMsg, e);
			throw new Exception(errMsg, e);
		}

		return availableLocales;
	}

	private CommentsInformations getRestitutionComments(int repId, int userId, IVanillaAPI vanillaApi, RepositoryItem item, Validation validation, boolean includeValidate) throws Exception {
		// int userId = session.getUser().getId();
		// int repId = session.getCurrentRepository().getId();

		List<CommentDefinition> commentsDefinition;
		try {
			commentsDefinition = vanillaApi.getCommentService().getCommentDefinitions(item.getId(), repId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to get the available comments for the item " + item.getName());
		}

		if (validation != null && (includeValidate || !validation.isValid())) {

			boolean canValidate = false;
			if (validation.getValidators() != null) {
				for (UserValidation valid : validation.getValidators()) {
					if (valid.getUserId() == userId) {
						canValidate = true;
						break;
					}
				}
			}

			List<CommentInformations> commentInformations = new ArrayList<CommentInformations>();
			if (commentsDefinition != null) {
				for (CommentDefinition definition : commentsDefinition) {

					boolean canComment = false;
					CommentValue comment;
					try {
						comment = vanillaApi.getCommentService().getCommentNotValidate(definition.getId(), repId);
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception("Unable to get the last comment for the item " + item.getName());
					}

					int nextUser = getNextUser(validation, comment);
					canComment = nextUser == userId;
					canValidate = canValidate && nextUser == -1;
					boolean canModify = !canComment && comment != null && comment.getUserId() == userId;
					boolean lastCommentUnvalidate = comment != null && comment.getStatus() == CommentStatus.UNVALIDATE;

					commentInformations.add(new CommentValidationInformations(definition, comment, canComment, canModify, lastCommentUnvalidate, nextUser));
				}
			}

			boolean isAdmin = validation.getAdminUserId() == userId;

			return new CommentsInformations(validation, TypeComment.VALIDATION, commentInformations, canValidate, isAdmin);
		}
		else if (validation == null) {
			List<CommentInformations> commentInformations = new ArrayList<CommentInformations>();
			if (commentsDefinition != null) {
				for (CommentDefinition definition : commentsDefinition) {

					try {
						List<CommentValue> comments = vanillaApi.getCommentService().getComments(definition.getId(), repId, userId);
						commentInformations.add(new CommentRestitutionInformations(definition, comments));
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception("Unable to get the last comments for the item " + item.getName());
					}
				}
			}
			return new CommentsInformations(validation, TypeComment.RESTITUTION, commentInformations, false, false);
		}

		return null;
	}

	private int getNextUser(Validation validation, CommentValue comment) {
		if (comment == null) {
			return validation.getCommentators().size() > 0 ? validation.getCommentators().get(0).getUserId() : -1;
		}

		boolean found = false;
		for (UserValidation commentator : validation.getCommentators()) {
			if (found) {
				return commentator.getUserId();
			}

			if (commentator.getUserId() == comment.getUserId()) {
				found = true;
			}
		}

		return -1;
	}

	public InputStream openGedDocument(RepositoryItem item, Repository repo, Group selectedGroup) throws Exception {
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, selectedGroup, repo);
		IRepositoryApi socket = new RemoteRepositoryApi(ctx);

		String xml;
		IGedComponent gedComponent;

		gedComponent = new RemoteGedComponent(vanillaCtx);
		xml = socket.getRepositoryService().loadModel(item);
		System.out.println(xml);

		if (xml.indexOf("<indexid>") >= 0) {
			int versionId = Integer.parseInt(xml.substring(xml.indexOf("<indexid>") + 9, xml.indexOf("</indexid>")));

			DocumentVersion version = gedComponent.getDocumentVersionById(versionId);
			GedDocument definition = version.getParent();

			GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(definition, socket.getContext().getGroup().getId());
			return gedComponent.loadGedDocument(config);
		}
		else {
			throw new VanillaException("The xml of the ged entry is malformed. Please check with an administrator.");
		}
	}

	public InputStream runReport(RepositoryItem item, Repository repo, Group selectedGroup, User user, String format, List<VanillaGroupParameter> groupParameters) throws Exception {
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, selectedGroup, repo);
		IRepositoryApi socket = new RemoteRepositoryApi(ctx);

		ReportingComponent reportingComponent = new RemoteReportRuntime(vanillaCtx);

		if (item.isReport()) {

		}

		if (!item.isOn()) {
			throw new Exception("Item '" + item.getName() + "' is disabled. Enable with ES if you want to run it.");
		}

		try {
			return executeReport(item, format, selectedGroup.getId(), repo, user, reportingComponent, socket, groupParameters);
			// byte currentXMLBytes[] = IOUtils.toByteArray(is);
			// return new ByteArrayInputStream(currentXMLBytes);
		} catch (Exception e) {
			String msg = "Failed to run report, reason : " + e.getMessage();
			// logger.error(msg, e);
			throw new Exception(msg);
		}
	}

	private InputStream executeReport(RepositoryItem item, String outputFormat, int groupId, Repository repo, User user, ReportingComponent reportingComponent, IRepositoryApi repositoryConnection, List<VanillaGroupParameter> groupParameters) throws Exception {
		// TODO: Manage locale
		// Locale locale = null;
		// if (itemInfo.getLocale() != null && !itemInfo.getLocale().isEmpty())
		// {
		// for (Locale loc : Locale.getAvailableLocales()) {
		// if (loc.toString().equals(itemInfo.getLocale())) {
		// //logger.info("Using specified Locale = " + loc.toString());
		// locale = loc;
		// break;
		// }
		// }
		// }
		//
		// if (locale == null) {
		// //logger.warn("No valid locale specified, will use the default
		// Locale.");
		// }

		// TODO: Manage locale
		AlternateDataSourceConfiguration alternateConfiguration = null; // itemInfo.getAlternateDataSourceConfig();

		try {
			// ReportingComponent reportingComponent =
			// session.getReportingComponent();
			int repositoryId = repo.getId();
			// ReportingComponent reportingComponent =
			// session.getReportingComponent();

			ObjectIdentifier ident = new ObjectIdentifier(repositoryId, item.getId());

			ReportRuntimeConfig config = new ReportRuntimeConfig();

			config.setObjectIdentifier(ident);
			config.setVanillaGroupId(groupId);
			// TODO: Manage locale
			// if (locale != null) {
			// config.setLocale(locale);
			// }
			config.setAlternateDataSourceConfiguration(alternateConfiguration);

			// TODO: Manage parameters
			if ((groupParameters != null) && (!groupParameters.isEmpty())) {
				config.setParameters(groupParameters);
			}

			if (outputFormat.equalsIgnoreCase("pht")) {
				config.setOutputFormat("ppt");
			}
			else {
				config.setOutputFormat(outputFormat);
			}

			ObjectMapper mapper = new ObjectMapper();
			System.out.println(mapper.writeValueAsString(config.getParametersValues()));
			// TODO: manage properties
			// config.setMaxRowsPerQuery(itemInfo.getLimitRows());
			// config.setDisplayComments(itemInfo.displayComments());

			// TODO: Manage background report
			// if (itemInfo.getTypeRun() == TypeRun.BACKGROUND) {
			// //logger.info("Running report in background...");
			// RunIdentifier runId = reportingComponent.runReportAsynch(config,
			// user);
			//
			// int userId = user.getId();
			//
			// ReportBackground report = new ReportBackground();
			// report.setItemId(itemInfo.getItem().getId());
			// report.setName(itemInfo.getItem().getName());
			// report.setOutputFormat(outputFormat);
			// report.setCreationDate(new Date());
			// report.setTaskKey(runId.getKey());
			// report.setTaskId(runId.getTaskId());
			// report.setRunning(true);
			// report.setUserId(userId);
			// report.setGroupId(groupId);
			//
			// //session.getRepositoryConnection().getReportHistoricService().addOrUpdateReportBackground(report);
			// repositoryConnection.getReportHistoricService().addOrUpdateReportBackground(report);
			// return null;
			// }
			// else {

			return reportingComponent.runReport(config, user);
			// }
		} catch (Exception e) {
			String msg = "Failed to run report, reason : " + e.getMessage();
			// logger.error(msg, e);
			throw new Exception(msg);
		}
	}

	public HistoricRuntimeConfiguration configHistorizedViewer(RepositoryItem item, Repository repo, Group group, User user, HistorizeConfig histoConfig) throws Exception {
		HistoricRuntimeConfiguration runtimeConfig = null;

		ObjectIdentifier obj = new ObjectIdentifier(repo.getId(), item.getId());

		String gedName = histoConfig.getGedName();
		if (histoConfig.isHistoForMe()) {
			runtimeConfig = new HistoricRuntimeConfiguration(obj, group.getId(), HistorizationTarget.User, histoConfig.getGroupIds(), gedName, histoConfig.getOutputFormat(), user.getId(), histoConfig.getHistoId());
		}
		else {
			runtimeConfig = new HistoricRuntimeConfiguration(obj, group.getId(), HistorizationTarget.Group, histoConfig.getGroupIds(), gedName, histoConfig.getOutputFormat(), user.getId(), histoConfig.getHistoId());
		}

		String peremptionDateStr = histoConfig.getPeremptionDate();
		if (peremptionDateStr != null) {
			SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
			runtimeConfig.setPeremptionDate(dateParser.parse(peremptionDateStr));
		}
		else {
			runtimeConfig.setPeremptionDate(null);
		}

		return runtimeConfig;
	}
	
	private UserRunConfiguration createViewerConfig(Repository repo,User user,RepositoryItem item,String configName,String configDescription,List<VanillaGroupParameter> groupParameters){
		List<UserRunConfigurationParameter> configParameters = new ArrayList<>();
		
		for(VanillaGroupParameter groupParameter : groupParameters) {
			for (VanillaParameter parameter : groupParameter.getParameters()) {
				String paramName = parameter.getName();
				List<String> values = parameter.getSelectedValues();
				configParameters.add(new UserRunConfigurationParameter(paramName, values));
			}
		}
		
		if(!configParameters.isEmpty()) {
			UserRunConfiguration config = new UserRunConfiguration(configName, user.getId(), repo.getId(), item.getId(), configParameters);
			config.setDescription(configDescription);
			return config;
		}
		
		return null;
	}

}
