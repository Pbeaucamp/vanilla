package bpm.vanilla.api.runtime.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.SaveOptions;
import bpm.vanilla.api.core.IAPIManager.FWRMethod;
import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.api.runtime.dto.OLAPDirectory;
import bpm.vanilla.api.runtime.dto.ReportSheet;
import bpm.vanilla.api.runtime.dto.RepositoryComponent;
import bpm.vanilla.api.runtime.dto.ViewerItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;

public class FWRService {


	private IVanillaSecurityManager vanillaSecurityManager;
	private IRepositoryManager repositoryManager;
	private IVanillaContext vanillaCtx;

	public FWRService() {
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
		FWRMethod fwrMethod = FWRMethod.valueOf(method);
			
		switch (fwrMethod) {
		case GET_FWR_REPORTS:
			return getFWRReports(parameters);
		case SAVE_REPORT:
			return saveReport(parameters);
		case LOAD_REPORT:
			return loadReport(parameters);
		default:
			break;
		}
		//TODO: Manage errors
		throw new VanillaApiException(VanillaApiError.METHOD_DOES_NOT_EXIST);
	}
	
	public InputStream dispatchStreamAction(String method, JSONArray parameters) throws Exception {
		FWRMethod fwrMethod = FWRMethod.valueOf(method);

		switch (fwrMethod) {
		case PREVIEW_REPORT:
			return previewReport(parameters);
		default:
			break;
		}

		throw new VanillaApiException(VanillaApiError.METHOD_DOES_NOT_EXIST);
	}
	
	
	public InputStream previewReport(JSONArray parameters) throws Exception {
		int repositoryID = Integer.parseInt(parameters.getString(0));
		int groupID = Integer.parseInt(parameters.getString(1));
		String userLogin = parameters.getString(2);
		String format = parameters.getString(3);
		String reportSheetStr = parameters.getString(4);
		
		RemoteRepositoryApi api = getApi(repositoryID,groupID);
		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(userLogin);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}
	
		ReportSheet reportSheet = new ReportSheet(reportSheetStr);
		FWRReport fwrReport = reportSheet.generateFWRReport(api.getContext().getRepository(),api.getContext().getGroup(),user);
		fwrReport.setOutput(format);
		XStream xstream = new XStream();
		String xml = xstream.toXML(fwrReport);
		System.out.println(xml);
		return runFWRReport(api,user,fwrReport);
	}
	
	public String getFWRReports(JSONArray parameters) throws Exception {
		int repositoryID = Integer.parseInt(parameters.getString(0));
		int groupID = Integer.parseInt(parameters.getString(1));
		RemoteRepositoryApi api = getApi(repositoryID,groupID);
		
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api,IRepositoryApi.FWR_TYPE);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		List<RepositoryComponent> repBrowser = browseRepository(rep,(RepositoryDirectory) null);
		
		return mapper.writeValueAsString(repBrowser);
	}
	
	public String saveReport(JSONArray parameters) throws Exception {
		int repositoryID = Integer.parseInt(parameters.getString(0));
		int groupID = Integer.parseInt(parameters.getString(1));
		String userLogin = parameters.getString(2);
		int directoryID = parameters.getInt(3);
		int reportID = parameters.getInt(4);
		String format = parameters.getString(5);
		String reportSheetStr = parameters.getString(6);
		
		RemoteRepositoryApi api = getApi(repositoryID,groupID);
		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(userLogin);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api,IRepositoryApi.FWR_TYPE);
		
		ReportSheet reportSheet = new ReportSheet(reportSheetStr);
		FWRReport fwrReport = reportSheet.generateFWRReport(api.getContext().getRepository(),api.getContext().getGroup(),user);
		fwrReport.setOutput(format);
		
		if(fwrReport.getSaveOptions() == null) {
			fwrReport.setSaveOptions(createSaveOptions(api.getContext().getGroup(),directoryID,reportID,reportSheet.getName()));
		}
		
		
		SaveOptions saveOpt = fwrReport.getSaveOptions();
		saveOpt.setName(reportSheet.getName());
		RepositoryItem reportItem = null;
		if(reportID > -1) {
			reportItem = rep.getItem(reportID);
		}
		
		XStream xstream = new XStream();
		String xml = xstream.toXML(fwrReport);
		
		if(reportItem == null) {
			RepositoryDirectory targetDir = rep.getDirectory(saveOpt.getDirectoryId());
			reportItem = api.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FWR_TYPE, -1, targetDir, saveOpt.getName(),saveOpt.getComment(), saveOpt.getInternalVersion(),saveOpt.getPublicVerson(), xml, true);
			Group grp = this.vanillaSecurityManager.getGroupByName(saveOpt.getGroup());
			SecuredCommentObject secComment = new SecuredCommentObject();
			secComment.setGroupId(grp.getId());
			secComment.setObjectId(reportItem.getId());
			secComment.setType(Comment.ITEM);
			api.getDocumentationService().addSecuredCommentObject(secComment);
		}
		else {
			reportItem.setItemName(saveOpt.getName());
			api.getRepositoryService().updateModel(reportItem, xml);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		//List<RepositoryComponent> repBrowser = browseRepository(rep,(RepositoryDirectory) null);
		
		return mapper.writeValueAsString(reportItem);
	}
	
	
	public String loadReport(JSONArray parameters) throws Exception {
		int repositoryID = Integer.parseInt(parameters.getString(0));
		int groupID = Integer.parseInt(parameters.getString(1));
		int reportID = parameters.getInt(2);
		RemoteRepositoryApi api = getApi(repositoryID,groupID);
		
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api,IRepositoryApi.FWR_TYPE);
		RepositoryItem reportItem = rep.getItem(reportID);
		XStream xStream = new XStream();
		
		try {
			String reportXML = api.getRepositoryService().loadModel(reportItem);
			System.out.println(reportXML);
			FWRReport fwrReport = (FWRReport) xStream.fromXML(reportXML);
			ReportSheet reportSheet = new ReportSheet(fwrReport,reportID);
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			return mapper.writeValueAsString(reportSheet);
		}catch(Exception e){
			throw new VanillaApiException(VanillaApiError.UNABLE_LOAD_REPORT);
		}
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
		

	private InputStream runFWRReport(RemoteRepositoryApi api,User user,FWRReport fwrReport) throws Exception {
		Repository repo = api.getContext().getRepository();
		Group group = api.getContext().getGroup();
		
		ObjectIdentifier objectId = new ObjectIdentifier(repo.getId(), -1);
		
		XStream xstream = new XStream();
		String reportXML = xstream.toXML(fwrReport);
		InputStream reportModel = IOUtils.toInputStream(reportXML, "UTF-8");
		
		
		ReportingComponent reportingComponent = new RemoteReportRuntime(vanillaCtx);
		ReportRuntimeConfig config = new ReportRuntimeConfig(objectId, null,group.getId());
		config.setOutputFormat(fwrReport.getOutput().toLowerCase());
		
		return reportingComponent.runReport(config, reportModel, user, false);
	}
	
	
	// Browse repository
	private List<RepositoryComponent> browseRepository(IRepository rep, RepositoryDirectory currentDir) {
		List<RepositoryComponent> repComponents = new ArrayList<>();

		try {
			for (RepositoryDirectory childDir : rep.getChildDirectories(currentDir)) {
				if (childDir.isShowed()) {
					List<RepositoryComponent> children = browseRepository(rep, childDir);
					repComponents.add(new OLAPDirectory(childDir, children));
				}
			}

			for(RepositoryItem item : rep.getItems(currentDir)) {
				repComponents.add(new ViewerItem(item,IRepositoryApi.TYPES_NAMES[item.getType()]));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.FILE_NOT_FOUND);
		}

		return repComponents;

	}
	
	private SaveOptions createSaveOptions(Group group,int directoryID,int reportID,String reportName) {
		SaveOptions saveOpt = new SaveOptions();
		
		saveOpt.setGroup(group.getName());
		saveOpt.setName(reportName);
		saveOpt.setDirectoryId(directoryID);
		saveOpt.setDirectoryItemid(reportID);
		saveOpt.setComment("");
		saveOpt.setInternalVersion("");
		saveOpt.setPublicVerson("");
		
		return saveOpt;
	}
	
	
}
