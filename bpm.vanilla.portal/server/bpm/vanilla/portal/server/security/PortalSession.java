package bpm.vanilla.portal.server.security;

import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.shared.GedInformations;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.IPreferencesManager;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAccessRequestManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHTMLForms;
import bpm.vanillahub.remote.RemoteResourceManager;
import bpm.vanillahub.remote.RemoteWorkflowManager;
import bpm.workflow.commons.beans.IWorkflowManager;

public class PortalSession extends CommonSession {
	
	private static final String P_PORTAIL = "02JOfdlOpdpmc";

	private Logger logger = Logger.getLogger(this.getClass());

	//Usefull for GED
	private HashMap<String, DocumentVersion> documents = new HashMap<String, DocumentVersion>();
	
	//Informations for the Ged Index File
	private GedInformations gedInformations;
	
	/*
	 * We use this boolean to show all the repository when an admin want it
	 */
	private boolean showAllRepository = false;
	
	private RemoteMapDefinitionService remoteMap;
	
	public void initSession(User user, String sessionId, Locale locale) throws ServiceException {
		logger.info("Init hub session...");

		IWorkflowManager workflowManager;
		try {
			workflowManager = super.getWorkflowManager(null);
			if (workflowManager == null) {
				String runtimeUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_HUB_RUNTIME_URL);
				
				setResourceManager(new RemoteResourceManager(runtimeUrl, sessionId, locale));
				setWorkflowManager(new RemoteWorkflowManager(runtimeUrl, sessionId, locale));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Session created.");
	}
	
	/**
	 * User has been allowed already, check is before.
	 * @param infoUser 
	 * 
	 * @param newGroup
	 * @throws Exception 
	 */
	public void changeCurrentGroup(InfoUser infoUser, Group newGroup) throws ServiceException {
		logger.info("Changing session's group to " + newGroup.getName());
		
		logger.info("Recreating socket to repository...");
		initSession(newGroup, getCurrentRepository());
		
		infoUser.setGroup(newGroup);
	}

	
	public IPreferencesManager getPreferencesManager() {
		return getVanillaApi().getVanillaPreferencesManager();
	}
	
	/**
	 * Acces to forms component 
	 * @return {@link RemoteHTMLForms}
	 */
	public RemoteHTMLForms getFormComponent() {
		return new RemoteHTMLForms(getVanillaContext());
	}
	
	public IVanillaAccessRequestManager getAccessRequestManager() {
		return getVanillaApi().getVanillaAccessRequestManager();
	}
	
	public String addDocumentVersion(DocumentVersion docVersion) {
		String key = docVersion.getId() + new Object().hashCode() + "";
		documents.put(key, docVersion);
		return key;
	}
	
	public DocumentVersion getDocumentVersion(String key) {
		return documents.get(key);
	}

	public void setGedInformations(GedInformations gedInformations) {
		this.gedInformations = gedInformations;
	}

	public GedInformations getGedInformations() {
		return gedInformations;
	}

	public void setShowAllRepository(boolean showAllRepository) {
		this.showAllRepository = showAllRepository;
	}

	public boolean isShowAllRepository() {
		return showAllRepository;
	}
	
	@Override
	public boolean hasLicence() {
		return true;
	}
	
	@Override
	public String getApplicationPassword() {
		return P_PORTAIL;
	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.PORTAL;
	}


	public RemoteMapDefinitionService getRemoteMap() {
		if(remoteMap == null) {
			remoteMap = new RemoteMapDefinitionService();
			remoteMap.setVanillaRuntimeUrl(getVanillaRuntimeUrl());
		}
		return remoteMap;
	}


	public void setRemoteMap(RemoteMapDefinitionService remoteMap) {
		this.remoteMap = remoteMap;
	}
	
//	@Override
//	public IWorkflowManager getWorkflowManager(Locale locale) throws Exception {
//		IWorkflowManager workflowManager = super.getWorkflowManager(null);
//		if (workflowManager == null) {
//			String hubRuntimeUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_HUB_RUNTIME_URL);
//			
//			//We initiate the connection to VanillaHub
//			RemoteAdminManager adminManager = new RemoteAdminManager(hubRuntimeUrl, null, locale);
//			User user = adminManager.login(getUser().getLogin(), getUser().getPassword(), locale != null ? locale.getLanguage() : "");
//			String sessionId = adminManager.connect(user);
//			
//			RemoteWorkflowManager manager = new RemoteWorkflowManager(hubRuntimeUrl, sessionId, locale);
//			setWorkflowManager(manager);
//		}
//		return super.getWorkflowManager(null);
//	}
}
