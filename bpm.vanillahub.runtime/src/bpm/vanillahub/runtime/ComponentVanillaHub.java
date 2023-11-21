package bpm.vanillahub.runtime;

import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationConstants;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;
import bpm.vanillahub.core.Constants;
import bpm.vanillahub.core.IHubResourceManager;
import bpm.vanillahub.core.IHubWorkflowManager;
import bpm.vanillahub.runtime.dao.ResourceDao;
import bpm.vanillahub.runtime.dao.WorkflowDao;
import bpm.vanillahub.runtime.managers.FileManager;
import bpm.vanillahub.runtime.run.SchedulerThreadManager;
import bpm.vanillahub.runtime.run.WorkflowProgressManager;
import bpm.vanillahub.runtime.security.HubSession;
import bpm.vanillahub.runtime.servlet.AdminServlet;
import bpm.vanillahub.runtime.servlet.ResourceServlet;
import bpm.vanillahub.runtime.servlet.WorkflowServlet;
import bpm.workflow.commons.security.IComponent;
import bpm.workflow.commons.security.SessionHolder;

/**
 * When the HttpService services is binded, just before all Servlets are
 * registered, a SessionHolder is created within this component and its Cleaner
 * Thread is Started.
 * 
 * When the HttpService is unregistered, the SessionHolder Cleaner's thread is
 * stopped and then its SessionHolder is destroyed.
 * 
 * 
 */
public class ComponentVanillaHub implements IComponent {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.vanilla.hub";

	private HttpService httpService;

	private IVanillaLogger logger;
	private SessionHolder<ComponentVanillaHub, HubSession> sessionHolder = null;
	
	private WorkflowDao workflowDao;
	private ResourceDao resourceDao;
	private FileManager fileManager;
	
	private WorkflowProgressManager progressManager;

	public ComponentVanillaHub() {
	}

	private void init() throws Exception {
		ClassPathResource configFile = new ClassPathResource("/bpm/vanillahub/runtime/vanillahub_context.xml", ComponentVanillaHub.class.getClassLoader());
		XmlBeanFactory factory = new XmlBeanFactory(configFile);

		try {
			PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
			FileSystemResource resource = new FileSystemResource(System.getProperty(ConfigurationConstants.configurationFile));
			cfg.setLocation(resource);

			cfg.postProcessBeanFactory(factory);
		} catch(Exception ex) {
			ex.printStackTrace();
			Logger.getLogger(this.getClass()).warn("Error when using PropertyPlaceHolder on bpm.vanilla.configurationFile=" + System.getProperty(ConfigurationConstants.configurationFile), ex);
		}

		try {
			resourceDao = (ResourceDao) factory.getBean("resourceDao");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("ComponentVanillaHub Unable to load SpringBean resourceDao", ex);
		}
		
		try {
			workflowDao = (WorkflowDao) factory.getBean("workflowDao");
			workflowDao.setComponent(this);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("ComponentVanillaHub Unable to load SpringBean workflowDao", ex);
		}
		

		// Create a trust manager that does not validate certificate chains for OpenData HTTPS connection
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
		    public X509Certificate[] getAcceptedIssuers(){return null;}
		    public void checkClientTrusted(X509Certificate[] certs, String authType){}
		    public void checkServerTrusted(X509Certificate[] certs, String authType){}
		}};

		// Install the all-trusting trust manager
		try {
		    SSLContext sc = SSLContext.getInstance("TLS");
		    sc.init(null, trustAllCerts, new SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {}
		

		VanillaConfiguration appConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		String filePath = appConfig.getProperty(VanillaConfiguration.HUB_FILE_PATH);

		this.fileManager = new FileManager(filePath);
		this.progressManager = new WorkflowProgressManager();
		
		checkUsers();

		new SchedulerThreadManager(logger, workflowDao, fileManager, resourceDao, progressManager).start();
		Security.addProvider(new BouncyCastleProvider());
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
			List<User> users = resourceDao.getUser();
			if (users == null || users.isEmpty()) {
				resourceDao.manageUser(user, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public IVanillaLogger getLogger() {
		return logger;
	}

	public SessionHolder<ComponentVanillaHub, HubSession> getSessionHolder() {
		return sessionHolder;
	}

	private void unregisterServlets() {
		httpService.unregister(Constants.SERVLET_ADMIN_MANAGER);
		httpService.unregister(IHubResourceManager.VANILLA_HUB_SERVLET);
		httpService.unregister(IHubWorkflowManager.VANILLA_HUB_SERVLET);
	}

	private void registerServlets() throws Exception {
		try {
			httpService.registerServlet(Constants.SERVLET_ADMIN_MANAGER, new AdminServlet(this), null, null);
			logger.info("Register " + Constants.SERVLET_ADMIN_MANAGER);
		} catch (NamespaceException ex) {
		}

		try {
			httpService.registerServlet(IHubResourceManager.VANILLA_HUB_SERVLET, new ResourceServlet(this), null, null);
			logger.info("Register " + IHubResourceManager.VANILLA_HUB_SERVLET);
		} catch (NamespaceException ex) {
		}

		try {
			httpService.registerServlet(IHubWorkflowManager.VANILLA_HUB_SERVLET, new WorkflowServlet(this), null, null);
			logger.info("Register " + IHubWorkflowManager.VANILLA_HUB_SERVLET);
		} catch (NamespaceException ex) {
		}

		logger.info("Registered queryServlet");
	}

	public void activate() {
		try {
			init();
		} catch (Throwable e) {
			Logger.getLogger(ComponentVanillaHub.class).error("Unable to init ComponentVanillaHub - " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void deactivate() {
		//TODO: do deactivate
	}
	
	public FileManager getFileManager() {
		return fileManager;
	}
	
	public WorkflowDao getWorkflowDao() {
		return workflowDao;
	}
	
	public ResourceDao getResourceDao() {
		return resourceDao;
	}
	
	public WorkflowProgressManager getProgressManager() {
		return progressManager;
	}
}
