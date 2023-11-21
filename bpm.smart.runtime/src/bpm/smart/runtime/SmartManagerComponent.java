package bpm.smart.runtime;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.service.http.HttpService;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bpm.smart.core.model.Constants;
import bpm.smart.core.xstream.ISmartManager;
import bpm.smart.core.xstream.ISmartWorkflowManager;
import bpm.smart.runtime.dao.SmartDao;
import bpm.smart.runtime.r.RClusterManager;
import bpm.smart.runtime.r.RExternalServlet;
import bpm.smart.runtime.r.RServer;
import bpm.smart.runtime.security.AirSession;
import bpm.smart.runtime.workflow.SchedulerThreadManager;
import bpm.smart.runtime.workflow.WorkflowManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;
import bpm.workflow.commons.security.IComponent;
import bpm.workflow.commons.security.SessionHolder;

public class SmartManagerComponent implements IComponent {
	
	public static final String PLUGIN_ID = "bpm.vanilla.air";

	private IVanillaLogger logger;
	private HttpService httpService;
	
	private IVanillaAPI api;
	private SmartDao dao;

	private WorkflowManager workflowManager;
	private RClusterManager clusterManager;
	
	private SessionHolder<SmartManagerComponent, AirSession> sessionHolder = null;

	public SmartManagerComponent() { }

	private void init() throws Exception {
		try {
			ApplicationContext factory = new ClassPathXmlApplicationContext("/bpm/smart/runtime/dao/smart_context.xml") {

				@Override
				protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
					super.initBeanDefinitionReader(reader);
					reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
					// This the important line and available since Equinox
					// 3.7
					ClassLoader loader = SmartManagerComponent.this.getClass().getClassLoader();
					reader.setBeanClassLoader(loader);
				}

			};

			dao = (SmartDao) factory.getBean("smartDao");

		} catch (Exception e) {
			throw new RuntimeException("Exception while init dao", e);
		}
		
		clusterManager = new RClusterManager(this);

		try {
			// get all RServer
			HashMap<Integer, String> serverUrls = new HashMap<Integer, String>();

			int nbServerR = 0;
			while (true) {
				nbServerR++;
				String serverUrl = null;
				try {
					serverUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_SMART_R_SERVER_URL + nbServerR);
					if (serverUrl == null || serverUrl.isEmpty()) {
						break;
					}
					serverUrls.put(nbServerR, serverUrl);
				} catch (Exception e) {
					break;
				}
			}
			nbServerR--;

			for (int i = 1; i <= nbServerR; i++) {
				String url = serverUrls.get(i);
				clusterManager.addRServer(url);
			}

			// rServer = new RServer(this);
		} catch (Exception e) {
			throw new RuntimeException("Exception while init R server", e);
		}
		
		checkUsers();

		this.workflowManager = new WorkflowManager(this);
		
		new SchedulerThreadManager(this, logger, workflowManager, getVanillaApi()).start();
	}

	private void checkUsers() {
		User user = new User();
		user.setLogin("system");
		user.setPassword("system");
		user.setBusinessMail("info@bpm-conseil.com");
		user.setFunction("Master");
		user.setSuperUser(true);
		user.setLocale("en");
		try {
			List<User> users = dao.getUser();
			if (users == null || users.isEmpty()) {
				dao.manageUser(user, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IVanillaAPI getVanillaApi() {
		if (api == null) {
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			api = new RemoteVanillaPlatform(config.getVanillaServerUrl(), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		}
		return api;
	}

	public IVanillaLogger getLogger() {
		return logger;
	}

	public void bind(IVanillaLoggerService service) {
		this.logger = service.getLogger(PLUGIN_ID);
		this.logger.info("IVanillaLoggerService binded");

	}

	public void unbind(IVanillaLoggerService service) {
		this.logger.info("IVanillaLoggerService unbinded");
		this.logger = null;
	}

	public void bind(HttpService httpService) {
		sessionHolder = new SessionHolder<>(this);
		sessionHolder.startCleaning();

		this.httpService = httpService;
		try {
			registerServlets();
			logger.info("HttpService binded");
		} catch (Exception ex) {
			logger.error("Unable  to bind servlets " + ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
	}

	public void unbind(HttpService service) {
		unregisterServlets();
		logger.info("Servlet Released");
		this.httpService = null;
		logger.info("HttpService unbinded");

		sessionHolder.stopCleaning();
		sessionHolder = null;
	}

	public void activate() {
		try {
			init();
		} catch (Exception e) {
			Logger.getLogger(SmartManagerComponent.class).error("Unable to init SmartManagerComponent - " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void registerServlets() {
		try {
//			VanillaHttpContext httpCtx = new VanillaHttpContext(getVanillaApi().getVanillaSecurityManager(), getVanillaApi().getVanillaSystemManager());
			httpService.registerServlet(Constants.SERVLET_ADMIN_MANAGER, new AdminServlet(this), null, null);
			httpService.registerServlet(ISmartManager.SMART_MANAGER_URL, new SmartManagerServlet(this, getLogger()), null, null);
			httpService.registerServlet(ISmartWorkflowManager.SMART_WORKFLOW_MANAGER_URL, new WorkflowManagerServlet(this, getLogger()), null, null);
			httpService.registerServlet(Constants.SERVLET_EXTERNAL, new RExternalServlet(this, getLogger()), null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unregisterServlets() {
		httpService.unregister(Constants.SERVLET_ADMIN_MANAGER);
		httpService.unregister(ISmartManager.SMART_MANAGER_URL);
		httpService.unregister(ISmartWorkflowManager.SMART_WORKFLOW_MANAGER_URL);
	}

	public void deactivate() {
		//TODO: do deactivate
		for(RServer server : clusterManager.getServers()){
			server.deconnect();
		}
	}

	public SmartDao getSmartDao() {
		return dao;
	}

	public RClusterManager getClusterManager() {
		return clusterManager;
	}

	public WorkflowManager getWorkflowManager() {
		return workflowManager;
	}

	public SessionHolder<SmartManagerComponent, AirSession> getSessionHolder() {
		return sessionHolder;
	}

}
