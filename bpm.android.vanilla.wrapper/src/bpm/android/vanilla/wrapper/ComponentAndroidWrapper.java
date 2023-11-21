package bpm.android.vanilla.wrapper;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import bpm.android.vanilla.core.IAndroidConstant;
import bpm.android.vanilla.wrapper.dispatcher.ConnectionServlet;
import bpm.android.vanilla.wrapper.dispatcher.ReportingServlet;
import bpm.android.vanilla.wrapper.dispatcher.RepositoryDispatcherServlet;
import bpm.android.vanilla.wrapper.servlets.ChangeDimensionServlet;
import bpm.android.vanilla.wrapper.servlets.ChangeMeasureServlet;
import bpm.android.vanilla.wrapper.servlets.ContentRepositoryServlet;
import bpm.android.vanilla.wrapper.servlets.DrillCubeServlet;
import bpm.android.vanilla.wrapper.servlets.ExecuteItemParameterQueryServlet;
import bpm.android.vanilla.wrapper.servlets.GetLastViewItemServlet;
import bpm.android.vanilla.wrapper.servlets.HideNullCubeServlet;
import bpm.android.vanilla.wrapper.servlets.InitCubeServlet;
import bpm.android.vanilla.wrapper.servlets.LoadCubeServlet;
import bpm.android.vanilla.wrapper.servlets.LoadDashBoardServlet;
import bpm.android.vanilla.wrapper.servlets.LoadItemInGedServlet;
import bpm.android.vanilla.wrapper.servlets.LoadViewCubeServlet;
import bpm.android.vanilla.wrapper.servlets.RepositoryServlet;
import bpm.android.vanilla.wrapper.servlets.RunItemServlet;
import bpm.android.vanilla.wrapper.servlets.SearchInGedServlet;
import bpm.android.vanilla.wrapper.servlets.SwapCubeServlet;
import bpm.android.vanilla.wrapper.tools.SessionHolder;
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
public class ComponentAndroidWrapper {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.android.vanilla.wrapper";
	
	private IVanillaLogger logger;
	
	private HttpService httpService;
	
	
	private SessionHolder sessionHolder = null;
	
	public  IVanillaLogger getLogger(){
		return logger;
	}
	
	/**
	 * The constructor
	 */
	public ComponentAndroidWrapper() {
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
		sessionHolder = new SessionHolder(this);
		sessionHolder.startCleaning();
		
		
		this.httpService = httpService;
		try{
			registerServlets();
			logger.info("HttpService binded");
		}catch(Exception ex){
			logger.error("Unable  to bind servlets " + ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
	}
	
	public void unbind(HttpService service){
		unregisterServlets();
		logger.info("Servlet Released");
		this.httpService = null;
		logger.info("HttpService unbinded");
		
		sessionHolder.stopCleaning();
		sessionHolder = null;
	}
	
	
	private void unregisterServlets(){
		httpService.unregister(IAndroidConstant.SERVLET_CONNECTION);
		httpService.unregister(IAndroidConstant.SERVLET_REPORTING);
		httpService.unregister(IAndroidConstant.SERVLET_REPOSITORY_DISPATCHER);
	}
	
	private void registerServlets() throws Exception{
		try{
			httpService.registerServlet("/ChangeDimensionServlet", new ChangeDimensionServlet(this), null, null);
			logger.info("Register /ChangeDimensionServlet");
		}catch(NamespaceException ex){}
		
		try{
			httpService.registerServlet("/ChangeMeasureServlet", new ChangeMeasureServlet(this), null, null);
			logger.info("Register /ChangeMeasureServlet");
		}catch(NamespaceException ex){}
		
		try{
			httpService.registerServlet("/ContentRepositoryServlet", new ContentRepositoryServlet(this), null, null);
			logger.info("Register /ContentRepositoryServlet");
		}catch(NamespaceException ex){}
		
		try{
			httpService.registerServlet("/DrillCubeServlet", new DrillCubeServlet(this), null, null);
			logger.info("Register /DrillCubeServlet");
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet("/ExecuteItemParameterQueryServlet", new ExecuteItemParameterQueryServlet(this), null, null);
			logger.info("Register /ExecuteItemParameterQueryServlet");
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet("/GetLastViewItemServlet", new GetLastViewItemServlet(this), null, null);
			logger.info("Register /GetLastViewItemServlet");
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet("/HideNullCubeServlet", new HideNullCubeServlet(this), null, null);
			logger.info("Register /HideNullCubeServlet");
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet("/InitCubeServlet", new InitCubeServlet(this), null, null);
			logger.info("Register /InitCubeServlet");
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet("/LoadCubeServlet", new LoadCubeServlet(this), null, null);
			logger.info("Register /LoadCubeServlet");
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet("/LoadDashBoardServlet", new LoadDashBoardServlet(this), null, null);
			logger.info("Register /LoadDashBoardServlet");
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet("/LoadItemInGedServlet", new LoadItemInGedServlet(this), null, null);
			logger.info("Register /LoadItemInGedServlet");
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet("/LoadViewCubeServlet", new LoadViewCubeServlet(this), null, null);
			logger.info("Register /LoadViewCubeServlet");
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet("/RepositoryServlet", new RepositoryServlet(this), null, null);
			logger.info("Register /RepositoryServlet");
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet("/RunItemServlet", new RunItemServlet(this), null, null);
			logger.info("Register /RunItemServlet");
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet("/SearchInGedServlet", new SearchInGedServlet(this), null, null);
			logger.info("Register /SearchInGedServlet");
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet("/SwapCubeServlet", new SwapCubeServlet(this), null, null);
			logger.info("Register /SwapCubeServlet");
		}catch(NamespaceException ex){}
		
		
		try{
			httpService.registerServlet(IAndroidConstant.SERVLET_CONNECTION, new ConnectionServlet(this), null, null);
			logger.info("Register " + IAndroidConstant.SERVLET_CONNECTION);
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet(IAndroidConstant.SERVLET_REPORTING, new ReportingServlet(this), null, null);
			logger.info("Register " + IAndroidConstant.SERVLET_REPORTING);
		}catch(NamespaceException ex){}
		try{
			httpService.registerServlet(IAndroidConstant.SERVLET_REPOSITORY_DISPATCHER, new RepositoryDispatcherServlet(this), null, null);
			logger.info("Register " + IAndroidConstant.SERVLET_REPOSITORY_DISPATCHER);
		}catch(NamespaceException ex){}
		
		logger.info("Registered queryServlet");
	}
	
	
	public SessionHolder getSessionHolder(){
		return sessionHolder;
	}
	
}
