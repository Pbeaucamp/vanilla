package bpm.update.manager.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import bpm.update.manager.api.utils.Constants;
import bpm.update.manager.runtime.servlets.UpdateServlet;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

/**
 * When the HttpService services is binded, just before all Servlets are registered,
 * a SessionHolder is created within this component and its Cleaner Thread is Started.
 * 
 * When the HttpService is unregistered, the SessionHolder Cleaner's thread is stopped
 * and then its SessionHolder is destroyed.
 * 
 * The 
 * 
 */
public class ComponentUpdateRuntime {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.update.manager.runtime";
	
	private IVanillaLogger logger;

	private Status status = Status.UNDEFINED;
	
	private ComponentContext context;
	private HttpService httpService;
	
	public  IVanillaLogger getLogger(){
		return logger;
	}
	
	/**
	 * The constructor
	 */
	public ComponentUpdateRuntime() {
	}
	
	public void activate(ComponentContext ctx){
		status = Status.STARTING;
		this.context = ctx;

		try {
			registerServlets();
		} catch (Exception ex) {
			getLogger().error("Error when regsitering servlets - " + ex.getMessage(), ex);
			status = Status.ERROR;
			this.httpService = null;
			throw new RuntimeException(ex);
		}
	}
	
	public void deactivate(ComponentContext ctx){
		if (ctx.getBundleContext().getBundle().getState() == Bundle.STOPPING) {
			status = Status.STOPPING;
			unregisterServlets();
			status = Status.STOPPED;
		}
		else if (ctx.getBundleContext().getBundle().getState() == Bundle.ACTIVE) {
			status = Status.STOPPING;
			try {
				unregisterServlets();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			status = Status.STOPPED;
		}
	}
	
	public void bind(IVanillaLoggerService service){
		this.logger = service.getLogger(PLUGIN_ID);
		this.logger.info("IVanillaLoggerService binded");
		
	}
	public void unbind(IVanillaLoggerService service){
		this.logger.info("IVanillaLoggerService unbinded");
		this.logger = null;
	}
	
	public void bind(HttpService httpService){
		this.httpService = httpService;
		getLogger().info("IVanillaLoggerService binded");
	}
	
	public void unbind(HttpService service){
		this.httpService = null;
		logger.info("HttpService unbinded");
	}
	
	
	private void unregisterServlets(){
		httpService.unregister(Constants.UPDATE_MANAGER_SERVLET);
	}
	
	private void registerServlets() throws Exception{
		try{
			httpService.registerServlet(Constants.UPDATE_MANAGER_SERVLET, new UpdateServlet(this), null, null);
			logger.info("Register " + Constants.UPDATE_MANAGER_SERVLET);
		}catch(NamespaceException ex){}
	}
	
	public List<Bundle> getBundles() {
		return context.getBundleContext() != null ? Arrays.asList(context.getBundleContext().getBundles()) : new ArrayList<Bundle>();
	}
	
	public void shutdown() throws BundleException {
		logger.info("Shutdown OSGI");
		context.getBundleContext().getBundle(0).stop();
	}
}
