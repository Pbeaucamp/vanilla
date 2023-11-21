package bpm.vanilla.repository.beans;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.osgi.service.http.HttpService;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.VanillaComponentIdentifier;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.services.IDataProviderService;
import bpm.vanilla.platform.core.repository.services.IDocumentationService;
import bpm.vanilla.platform.core.repository.services.IMetaService;
import bpm.vanilla.platform.core.repository.services.IModelVersionningService;
import bpm.vanilla.platform.core.repository.services.IRepositoryAdminService;
import bpm.vanilla.platform.core.repository.services.IRepositoryAlertService;
import bpm.vanilla.platform.core.repository.services.IRepositoryImpactService;
import bpm.vanilla.platform.core.repository.services.IRepositoryLogService;
import bpm.vanilla.platform.core.repository.services.IRepositoryReportHistoricService;
import bpm.vanilla.platform.core.repository.services.IWatchListService;
import bpm.vanilla.repository.beans.log.ServerLoger;
import bpm.vanilla.repository.services.SecurityTool;
import bpm.vanilla.repository.services.ServiceAdminImpl;
import bpm.vanilla.repository.services.ServiceAlert;
import bpm.vanilla.repository.services.ServiceBrowseImpl;
import bpm.vanilla.repository.services.ServiceCheckingImpl;
import bpm.vanilla.repository.services.ServiceDatasourceImpl;
import bpm.vanilla.repository.services.ServiceDocumentationImpl;
import bpm.vanilla.repository.services.ServiceLogImpl;
import bpm.vanilla.repository.services.ServiceMeta;
import bpm.vanilla.repository.services.ServiceReportHistoric;
import bpm.vanilla.repository.services.ServiceWatchListImpl;
import bpm.vanilla.repository.services.servlet.ExternalServlet;
import bpm.vanilla.repository.services.servlet.ServiceServlet;

public class RepositoryRuntimeComponent {
	
	private Logger logger = Logger.getLogger(RepositoryRuntimeComponent.class);

	private HashMap<Integer, RepositoryDaoComponent> repositories = new HashMap<Integer, RepositoryDaoComponent>();
	private IVanillaAPI vanillaApi;
	private SecurityTool securityTool;
	private String context;
	private HttpService httpService;

	public void bind(HttpService service){
		logger.info("Binding Http service repository...");
		this.httpService = service;
				
		logger.info("Binded Http service repository");
	}
	
	public void unbind(HttpService service){
		this.httpService = null;
		logger.info("Unbinded Http service");
	}
	
	public RepositoryDaoComponent getRepositoryDao(int repositoryId) {
		if(repositories.get(repositoryId) == null) {
			repositories.put(repositoryId, initDao(repositoryId));
		}
		return repositories.get(repositoryId);
	}

	private RepositoryDaoComponent initDao(int repositoryId) {
		RepositoryDaoComponent dao = new RepositoryDaoComponent(repositoryId);
		dao.activate();
		return dao;
	}

	public ServiceBrowseImpl getServiceBrowse(int repositoryId, int groupId, User user, String clientIp) throws Exception {
		return new ServiceBrowseImpl(this, groupId, repositoryId, user, clientIp);
	}
	
	public void activate() {
		vanillaApi = new RemoteVanillaPlatform(
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN),
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		
		ServerLoger.init(vanillaApi.getVanillaLoggingManager());
		
		securityTool = new SecurityTool(this);
		
		try{
			httpService.registerServlet(VanillaConstants.REPOSITORY_SERVLET, new ServiceServlet(this), null, null);
		}catch(Exception ex){
			logger.error("ServletDispatcher registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("ServletDispatcher registration failed - " + ex.getMessage(), ex);
		}
		
		try{
			httpService.registerServlet("/externalRepositoryServlet", new ExternalServlet(this), null, null);
		}catch(Exception ex){
			logger.error("externalServlet registration failed - " + ex.getMessage(), ex);
			throw new RuntimeException("externalServlet registration failed - " + ex.getMessage(), ex);
		}
		
		context = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_CONTEXT);
		
		int nbRepositories = 0;
		while(true) {
			nbRepositories++;
			String filePath = null;
			try {
				filePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_REPOSITORY_FILE + nbRepositories);
				if(filePath == null || filePath.isEmpty()) {
					break;
				}
			} catch(Exception e) {
				break;
			}
		}
		nbRepositories--;
		
		for(int i = 1 ; i <= nbRepositories ; i++) {
			getRepositoryDao(i);
		}
		
	}
	
	public IVanillaAPI getVanillaRootApi() {
		return vanillaApi;
	}
	
	public SecurityTool getSecurityTool() {
		return securityTool;
	}
	
	public IVanillaComponentIdentifier getVanillaComponentIdentifier(int repositoryId) {
		VanillaComponentIdentifier id = new VanillaComponentIdentifier(
				VanillaComponentType.COMPONENT_REPOSITORY, 
				"",
				"", 
				"", 
				context, 
				repositoryId + "",
				Status.STARTED.getStatus());
		return id;
	}

	public IWatchListService getServiceWatchList(int repositoryId, int groupId, User user, String clientIp) throws Exception {
		return new ServiceWatchListImpl(this, groupId, repositoryId, user, clientIp);
	}

	public IRepositoryReportHistoricService getServiceReportHistoric(int repositoryId, int groupId, User user, String clientIp) {
		return new ServiceReportHistoric(this, groupId, repositoryId, user, clientIp);
	}

	public IRepositoryLogService getServiceLog(int repositoryId, int groupId, User user, String clientIp) {
		return new ServiceLogImpl(this, groupId, repositoryId, user, clientIp);
	}

	public IRepositoryImpactService getServiceImpact(int repositoryId, int groupId, User user, String clientIp) {
		return new ServiceDatasourceImpl(this, groupId, repositoryId, user, clientIp);
	}

	public IRepositoryAdminService getServiceAdmin(int repositoryId, int groupId, User user) throws Exception {
		return new ServiceAdminImpl(this, repositoryId, groupId, user);
	}

	public IDocumentationService getServiceDocumentation(int repositoryId, int groupId, User user, String clientIp) {
		return new ServiceDocumentationImpl(this, groupId, repositoryId, user);
	}

	public IRepositoryAlertService getServiceAlert(int repositoryId, int groupId, User user, String clientIp) {
		return new ServiceAlert(this, repositoryId);
	}

	public IModelVersionningService getServiceVersionning(int repositoryId, int groupId, User user, String clientIp) {
		return new ServiceCheckingImpl(this, groupId, repositoryId, user, clientIp);
	}

	public IDataProviderService getServiceDataprovider(int repositoryId, int groupId, User user, String clientIp) {
		return new ServiceDatasourceImpl(this, groupId, repositoryId, user, clientIp);
	}

	public IMetaService getServiceMeta(int repositoryId, int groupId, User user, String clientIp) {
		return new ServiceMeta(this, groupId, repositoryId, user, clientIp);
	}
}
