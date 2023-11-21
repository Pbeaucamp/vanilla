package bpm.united.olap.wrapper;

import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.united.olap.api.cache.IUnitedOlapCacheManager;
import bpm.united.olap.api.communication.IModelService;
import bpm.united.olap.api.communication.IRuntimeService;
import bpm.united.olap.api.communication.IServiceProvider;
import bpm.united.olap.wrapper.fa.FdServlet;
import bpm.united.olap.wrapper.servlet.CacheManagementServlet;
import bpm.united.olap.wrapper.servlet.ModelServlet;
import bpm.united.olap.wrapper.servlet.RuntimeServlet;
import bpm.united.olap.wrapper.servlet.excel.DataPrepExcelServlet;
import bpm.united.olap.wrapper.servlet.excel.ExcelServlet;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.components.Distributable;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.UnitedOlapComponent;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.impl.AbstractVanillaComponent;
import bpm.vanilla.platform.core.components.impl.LoadEvaluationServlet;
import bpm.vanilla.platform.core.components.impl.VanillaHttpContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

public class UnitedOlapWrapperComponent extends AbstractVanillaComponent implements IServiceProvider, Distributable{

	
	public static final String ID = "bpm.united.olap.wrapper.UnitedOlapWrapperComponent";
	
	
	private IVanillaLoggerService loggerService;
	private HttpService httpService;
	private IModelService modelService;
	private IRuntimeService runtimeService;
	private IUnitedOlapCacheManager cacheManagerService;
	
	private Status status = Status.UNDEFINED;
	private ComponentContext context = null;
	
	public IVanillaLogger getLogger(){
		return loggerService.getLogger(ID);
	}
	
	public void bind(IVanillaLoggerService service){
		this.loggerService = service;
		getLogger().info("Binded IVanillaLoggerService");
	}
	
	public void unbind(IVanillaLoggerService service){
		this.loggerService = service;
		getLogger().info("Binded IVanillaLoggerService");
	}
	
	public void bind(IModelService service) {
		this.modelService = service;
		getLogger().info("Binded IModelService");
	}
	
	public void unbind(IModelService service) {
		this.modelService = null;
		getLogger().info("Unbinded IModelService");
	}
	
	public void bind(IUnitedOlapCacheManager service) {
		this.cacheManagerService = service;
		getLogger().info("Binded IUnitedOlapCacheManager");
	}
	
	public void unbind(IUnitedOlapCacheManager service) {
		this.cacheManagerService = null;
		getLogger().info("Unbinded IUnitedOlapCacheManager");
	}
	
	public void bind(IRuntimeService service) {
		this.runtimeService = service;
		getLogger().info("Binded IRuntimeService");
	}
	
	public void unbind(IRuntimeService service) {
		this.runtimeService = null;
		getLogger().info("Unbinded IRuntimeService");
	}
	

	public void bind(HttpService service){
		this.httpService = service;
		getLogger().info("Binded HttpService");
		
	}

	public void unbind(HttpService service){
		this.httpService = null;
		getLogger().info("Unbinded HttpService");
		
	}

	private void registerServlets() throws Exception {

		try {
			httpService.registerServlet(UnitedOlapComponent.SERVLET_MODEL, new ModelServlet(this), null, null);
			httpService.registerServlet(UnitedOlapComponent.SERVLET_RUNTIME, new RuntimeServlet(this), null, null);
			httpService.registerServlet(UnitedOlapComponent.SERVLET_CACHE, new CacheManagementServlet(this), null, null);
			httpService.registerServlet(UnitedOlapComponent.SERVLET_EXCEL, new ExcelServlet(this), null, null);
			httpService.registerServlet(UnitedOlapComponent.DATAPREP_SERVLET_EXCEL, new DataPrepExcelServlet(this), null, null);
			getLogger().info("Servlets registred");
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
		
		
		
		
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		IVanillaAPI api = new RemoteVanillaPlatform(config.getVanillaServerUrl(),
				config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN),
				config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));		
		
		VanillaHttpContext httpContext = new VanillaHttpContext(api.getVanillaSecurityManager(), api.getVanillaSystemManager());

		
		try{
			httpService.registerServlet("/faRuntime/FdServlet", new FdServlet(this), null, httpContext);
			getLogger().info("registered servlet /FdServlet");
		}catch (NamespaceException e) {
			// TODO: handle exception
		}catch(Exception e){
			getLogger().error("Unable to register alias /FdServlet" + e.getMessage());
			throw e;
		}
		
		//register the LoadEvaluator Servlet
		try{
			httpService.registerServlet(UnitedOlapComponent.UOLAP_LOAD_EVALUATOR_SERVLET, new LoadEvaluationServlet(this), null, httpContext);
		}catch (Exception e) {
			getLogger().error("Unable to register " + GatewayComponent.GATEWAY_LOAD_EVALUATOR_SERVLET + " alias - " + e.getMessage());
			throw e;
		}
	}

	private void unregisterServlets() {
		httpService.unregister(UnitedOlapComponent.SERVLET_MODEL);
		getLogger().info("unregistered servlet " + UnitedOlapComponent.SERVLET_MODEL);
		httpService.unregister(UnitedOlapComponent.SERVLET_RUNTIME);
		getLogger().info("unregistered servlet " + UnitedOlapComponent.SERVLET_RUNTIME);
		httpService.unregister(UnitedOlapComponent.SERVLET_CACHE);
		getLogger().info("unregistered servlet " + UnitedOlapComponent.SERVLET_CACHE);
		httpService.unregister("/faRuntime/FdServlet");
		getLogger().info("unregistered servlet /faRuntime/FdServlet");
		httpService.unregister(UnitedOlapComponent.UOLAP_LOAD_EVALUATOR_SERVLET);
		getLogger().info("unregistered servlet " + UnitedOlapComponent.UOLAP_LOAD_EVALUATOR_SERVLET);
		httpService.unregister(UnitedOlapComponent.SERVLET_EXCEL);
		getLogger().info("unregistered servlet " + UnitedOlapComponent.SERVLET_EXCEL);
		httpService.unregister(UnitedOlapComponent.DATAPREP_SERVLET_EXCEL);
		getLogger().info("unregistered servlet " + UnitedOlapComponent.DATAPREP_SERVLET_EXCEL);
	}
	

	@Override
	public void configure(IVanillaContext ctx) {}


	public IUnitedOlapCacheManager getCacheManager() {
		return cacheManagerService;
	}

	@Override
	public IModelService getModelProvider() {
		return modelService;
	}

	@Override
	public IRuntimeService getRuntimeProvider() {
		return runtimeService;
	}
	
	
	public void activate(ComponentContext ctx) throws Exception{
		
		status = Status.STARTING;
		context = ctx;
		
		Properties p = null;
		if (System.getProperty("bpm.vanilla.configurationFile") != null){
    		getLogger().info("Loading configuration with bpm.vanilla.configurationFile=" + System.getProperty("bpm.vanilla.configurationFile"));
    		try{
    			FileInputStream fis = new FileInputStream(System.getProperty("bpm.vanilla.configurationFile"));
    			p = new Properties();
    			p.load(fis);
    			getLogger().info("Configuration loaded");
    		}catch(Exception ex){
    			getLogger().warn("Loading configuration failed " + ex.getMessage(), ex);
    			p = null;
    		}
    	}
		
		if (p == null){
			getLogger().error("No configuration file found");
			throw new Exception("bpm.vanilla.configurationFile not specified or the file does not exist");
		}
		
		getLogger().info("JDBC JAR location : " + IConstants.getJdbcJarFolder());
		getLogger().info("JDBC JAR descriptor : " + IConstants.getJdbcDriverXmlFile());
		
		try{
			registerServlets();
		}catch(Exception ex){
			getLogger().error("Error when regsitering servlets - " + ex.getMessage(), ex);
			this.httpService = null;
			status = Status.ERROR;
			throw new RuntimeException(ex);
		}
		
		// get the port from the HttpService
		ServiceReference ref = ctx.getBundleContext().getServiceReference(HttpService.class.getName());
		String port = (String)ref.getProperty("http.port");
	
		try{
			registerInVanilla(VanillaComponentType.COMPONENT_UNITEDOLAP,
					"UnitedOlapEngine",
					port);
			status = Status.STARTED;
		}catch(Exception ex){
			getLogger().error("Error when registring ComponentFd within VanillaPlatform - " + ex.getMessage(), ex);
			status = Status.ERROR;
			this.httpService = null;
			throw new RuntimeException(ex);
		}
		//required by FdServlet 
		UnitedOlapServiceProvider.getInstance().init(runtimeService, modelService);
		Logger.getLogger(getClass()).info("inited fa's UnitedOlapServiceProvider");
		
	}
	
	public void desactivate(ComponentContext ctx) throws Exception {
		if (ctx.getBundleContext().getBundle().getState() == Bundle.STOPPING) {
			status = Status.STOPPING;
			unregisterServlets();
			unregisterFromVanilla(getIdentifier());
			status = Status.STOPPED;
		}
		else if (ctx.getBundleContext().getBundle().getState() == Bundle.ACTIVE) {
			status = Status.STOPPING;
			unregisterServlets();
			IVanillaComponentIdentifier ident = getIdentifier();
			ident.setComponentStatus(Status.STOPPED.getStatus());
			updateInVanilla(ident);
			status = Status.STOPPED;
		}
	}

	@Override
	public int computeLoadEvaluation() throws Exception {
		
		return getModelProvider().getLoadedSchema().size();
	}
	
	@Override
	public Status getStatus() {
		return status;
	}
	
	@Override
	protected void doStop() throws Exception {
		context.disableComponent(ID);
	}

	@Override
	protected void doStart() throws Exception {
		context.enableComponent(ID);
	}

	@Override
	public void notify(IVanillaEvent event) {
		
		
	}
}
