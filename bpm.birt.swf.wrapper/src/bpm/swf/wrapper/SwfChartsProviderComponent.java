package bpm.swf.wrapper;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

/**
 * The activator class controls the plug-in life cycle
 * 
 * This plugins need to be in the configuration file (Vanilla Tomcat -> webapps -> VanillaRuntime -> Web-Inf -> Eclipse -> Configuration)
 * with bpm.swf.wrapper@5:start
 * 
 */
public class SwfChartsProviderComponent {

	// The plug-in ID
	public static final String ID = "bpm.swf.wrapper";
	
	// The shared instance
	private BundleContext context;
	
	private IVanillaLoggerService loggerService;
	private HttpService httpService;
	private SwfHttpContext httpContext;
	
	public void bind(HttpService httpService){
		httpContext = new SwfHttpContext(this);
		
		httpContext.setBundleContext(context);
		
		this.httpService = httpService;
		getLogger().info("HttpService binded");
		try {
			this.httpService.registerResources("/Charts", "/Charts", httpContext);
			getLogger().info("HttpService alias /Charts for swf resources");
		} catch (NamespaceException e) {
		}catch(Exception ex){
			getLogger().error("Unable to alias /Charts - " + ex.getMessage(), ex);
			this.httpService = null;
			throw new RuntimeException(ex);
		}
		
	}
	public void unbind(HttpService httpService){
		httpContext.setBundleContext(null);
		httpContext = null;
		httpService.unregister("/Charts");
		getLogger().info("un aliased /Charts");
		getLogger().info("HttpService unbinded");
		this.httpService = null;
	}
	
	public void bind(IVanillaLoggerService loggerService){
		this.loggerService = loggerService;
		getLogger().info("IVanillaLoggerService binded");
		
	}
	
	public void unbind(IVanillaLoggerService loggerService){
		getLogger().info("IVanillaLoggerService unbinded");
		this.loggerService =  null;
	}
	
	public IVanillaLogger getLogger(){
		return loggerService.getLogger(ID);
	}
	
	/**
	 * The constructor
	 */
	public SwfChartsProviderComponent() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(ComponentContext context) throws Exception {
		this.context = context.getBundleContext();
		if (httpContext != null){
			httpContext.setBundleContext(this.context);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(ComponentContext context) throws Exception {
		this.context = null;
		if (httpContext != null){
			httpContext.setBundleContext(null);
		}
		httpContext = null;
	}

	
}
