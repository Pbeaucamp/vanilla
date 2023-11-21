package bpm.vanilla.api.runtime.service;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.api.core.IAPIManager.RepositoriesMethod;
import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.KpiTheme;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class RepositoriesService {

	private IVanillaSecurityManager vanillaSecurityManager;
	private IRepositoryManager repositoryManager;
	private IVanillaContext vanillaCtx;
	
	public RepositoriesService() {

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String root = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		this.vanillaCtx = new BaseVanillaContext(url, root, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);

		this.vanillaSecurityManager = vanillaApi.getVanillaSecurityManager();
		this.repositoryManager = vanillaApi.getVanillaRepositoryManager();
	
	}
	public String dispatchAction(String method, JSONArray parameters) throws Exception {
		RepositoriesMethod repositoriesMethod = RepositoriesMethod.valueOf(method);
		
		switch (repositoriesMethod) {
		case GET_ITEMS:
			return getItems(parameters);
		case GET_ALL_ITEMS:
			return getAllItems(parameters);
		default:
			break;
		}
		//TODO: Manage errors
		throw new VanillaApiException(VanillaApiError.METHOD_DOES_NOT_EXIST);
	}

	public String getItems(JSONArray parameters) throws Exception {
		
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		int dirID = parameters.getInt(2);
		
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
			bpm.vanilla.platform.core.repository.Repository repository = new bpm.vanilla.platform.core.repository.Repository(new RemoteRepositoryApi(ctx), IRepositoryApi.FMDT_TYPE);
			
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
		}
		
		JSONArray jsonArray = new JSONArray();
		try {
			int groupId = api.getContext().getGroup().getId();
			
			IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);
			RepositoryDirectory d = rep.getDirectory(dirID);

			for (RepositoryDirectory dirTmp : rep.getChildDirectories(d)) {
				if (dirTmp.isShowed()) {
					JSONObject obj = new JSONObject();
					obj.put("name", dirTmp.getName());
					obj.put("id", dirTmp.getId());
					obj.put("comment", dirTmp.getComment());
					obj.put("typeID", -1);
					obj.put("type", "DIRECTORY");
					obj.put("subtype", "");
					obj.put("subtypeID", -1);
					jsonArray.put(obj);
				}
			}
			
			for (RepositoryItem di : rep.getItems(d)) {
				if (di.isDisplay()) {
					JSONObject obj = new JSONObject();
					if (di.getName() != null) {
						obj.put("name", di.getName());
						obj.put("id", di.getId());
						obj.put("type", IRepositoryApi.TYPES_NAMES[di.getType()]);
						obj.put("typeID", di.getType());
						obj.put("subtypeID", di.getSubtype());
						
						if (IRepositoryApi.TYPES_NAMES[di.getType()] == "PORTAL") {
                            String itemXml = null;
                            itemXml = api.getRepositoryService().loadModel(di);

                            XStream xstream = new XStream();
                            KpiTheme report = (KpiTheme) xstream.fromXML(itemXml);
                            obj.put("portalID",report.getThemeId());
                        }				 
                        
						if (di.getSubtype() < 0) {
							obj.put("subtype", "");
						} else {
						obj.put("subtype", IRepositoryApi.SUBTYPES_NAMES[di.getSubtype()]);
						}
						obj.put("comment", di.getComment());
						jsonArray.put(obj);
					}
				}
			}

			api.getContext().getGroup().setId(groupId);
		} catch (Exception e) {
			throw new VanillaApiException(VanillaApiError.FILE_NOT_FOUND);
		}

		return jsonArray.toString();
	}
	
	public String getAllItems(JSONArray parameters) throws Exception {
		
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		int dirID = parameters.getInt(2);
		
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
			bpm.vanilla.platform.core.repository.Repository repository = new bpm.vanilla.platform.core.repository.Repository(new RemoteRepositoryApi(ctx), IRepositoryApi.FMDT_TYPE);
			
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
		}
		
		JSONArray jsonArray = new JSONArray();
		try {
			int groupId = api.getContext().getGroup().getId();
			
			IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);
			RepositoryDirectory d = rep.getDirectory(dirID);

			for (RepositoryDirectory dirTmp : rep.getChildDirectories(d)) {
				if (dirTmp.isShowed()) {
					JSONObject obj = new JSONObject();
					obj.put("name", dirTmp.getName());
					obj.put("id", dirTmp.getId());
					obj.put("comment", dirTmp.getComment());
					obj.put("typeID", -1);
					obj.put("type", "DIRECTORY");
					obj.put("subtype", "");
					obj.put("subtypeID", -1);
					JSONArray data = new JSONArray();
					data.put(repositoryID);
					data.put(groupID);
					data.put(dirTmp.getId());
					obj.put("children", getAllItemsRec(data));
					jsonArray.put(obj);
				}
			}
			
			for (RepositoryItem di : rep.getItems(d)) {
				if (di.isDisplay()) {
					JSONObject obj = new JSONObject();
					if (di.getName() != null) {
						obj.put("name", di.getName());
						obj.put("id", di.getId());
						obj.put("type", IRepositoryApi.TYPES_NAMES[di.getType()]);
						obj.put("typeID", di.getType());
						obj.put("subtypeID", di.getSubtype());
						
						if (IRepositoryApi.TYPES_NAMES[di.getType()] == "PORTAL") {
                            String itemXml = null;
                            itemXml = api.getRepositoryService().loadModel(di);

                            XStream xstream = new XStream();
                            KpiTheme report = (KpiTheme) xstream.fromXML(itemXml);
                            obj.put("portalID",report.getThemeId());
                        }			 
                        
						if (di.getSubtype() < 0) {
							obj.put("subtype", "");
						} else {
						obj.put("subtype", IRepositoryApi.SUBTYPES_NAMES[di.getSubtype()]);
						}
						obj.put("comment", di.getComment());
						jsonArray.put(obj);
					}
				}
			}

			api.getContext().getGroup().setId(groupId);
		} catch (Exception e) {
			throw new VanillaApiException(VanillaApiError.FILE_NOT_FOUND);
		}

		return jsonArray.toString();
	}
	public JSONArray getAllItemsRec(JSONArray parameters) throws Exception {
		
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		int dirID = parameters.getInt(2);
		
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
			bpm.vanilla.platform.core.repository.Repository repository = new bpm.vanilla.platform.core.repository.Repository(new RemoteRepositoryApi(ctx), IRepositoryApi.FMDT_TYPE);
			
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
		}
		
		JSONArray jsonArray = new JSONArray();
		try {
			int groupId = api.getContext().getGroup().getId();
			
			IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);
			RepositoryDirectory d = rep.getDirectory(dirID);

			for (RepositoryDirectory dirTmp : rep.getChildDirectories(d)) {
				if (dirTmp.isShowed()) {
					JSONObject obj = new JSONObject();
					obj.put("name", dirTmp.getName());
					obj.put("id", dirTmp.getId());
					obj.put("comment", dirTmp.getComment());
					obj.put("typeID", -1);
					obj.put("type", "DIRECTORY");
					obj.put("subtype", "");
					obj.put("subtypeID", -1);
					JSONArray data = new JSONArray();
					data.put(repositoryID);
					data.put(groupID);
					data.put(dirTmp.getId());
					obj.put("children", getAllItemsRec(data));
					jsonArray.put(obj);
				}
			}
			
			for (RepositoryItem di : rep.getItems(d)) {
				if (di.isDisplay()) {
					JSONObject obj = new JSONObject();
					if (di.getName() != null) {
						obj.put("name", di.getName());
						obj.put("id", di.getId());
						obj.put("type", IRepositoryApi.TYPES_NAMES[di.getType()]);
						obj.put("typeID", di.getType());
						obj.put("subtypeID", di.getSubtype());

						
						if (IRepositoryApi.TYPES_NAMES[di.getType()] == "PORTAL") {
                            String itemXml = null;
                            itemXml = api.getRepositoryService().loadModel(di);
                            XStream xstream = new XStream();
                            KpiTheme report = (KpiTheme) xstream.fromXML(itemXml);
                            obj.put("portalID",report.getThemeId());
                        }			 
                        
						if (di.getSubtype() < 0) {
							obj.put("subtype", "");
						} else {
						obj.put("subtype", IRepositoryApi.SUBTYPES_NAMES[di.getSubtype()]);
						}
						obj.put("comment", di.getComment());
						jsonArray.put(obj);
					}
				}
			}

			api.getContext().getGroup().setId(groupId);
		} catch (Exception e) {
			throw new VanillaApiException(VanillaApiError.FILE_NOT_FOUND);
		}

		return jsonArray;
	}
}
