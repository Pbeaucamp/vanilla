package bpm.vanilla.api.runtime;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import bpm.vanilla.api.core.IAPIManager;
import bpm.vanilla.api.runtime.service.VanillaApiService;
import bpm.vanilla.api.runtime.servlet.APIServlet;
import bpm.vanilla.api.runtime.servlet.StreamServlet;
import bpm.vanilla.api.runtime.servlet.VanillaApiXStreamServlet;
import bpm.vanilla.api.runtime.utils.Constants;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

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
public class ComponentAPI implements IComponent {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.vanilla.api";

	private HttpService httpService;

	private IVanillaLogger logger;

	private VanillaApiService apiService;

	public ComponentAPI() {
	}

	private void init() throws Exception {
		this.apiService = new VanillaApiService();
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
	}

	public IVanillaLogger getLogger() {
		return logger;
	}

	private void unregisterServlets() {
		httpService.unregister(Constants.SERVLET_API);
	}

	private void registerServlets() throws Exception {
		try {
			httpService.registerServlet(IAPIManager.SERVLET_API, new APIServlet(this), null, null);
			logger.info("Register " + IAPIManager.SERVLET_API);
		} catch (NamespaceException ex) {
		}
		
		try {
			httpService.registerServlet(IAPIManager.STREAM_API, new StreamServlet(), null, null);
			logger.info("Register " + IAPIManager.STREAM_API);
		} catch (NamespaceException ex) {
		}
		
		try {
			httpService.registerServlet(IAPIManager.SERVLET_API_XSTREAM, new VanillaApiXStreamServlet(this), null, null);
			logger.info("Register " + IAPIManager.SERVLET_API_XSTREAM);
		} catch (NamespaceException ex) {
		}

		logger.info("Registered servlets");
	}

	public void activate() {
		try {
			init();
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void deactivate() {
		//TODO: do deactivate
	}
	
	public VanillaApiService getApiService() {
		return apiService;
	}
}
