package bpm.workflow.wrapper;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;
import bpm.vanilla.platform.core.components.impl.AbstractVanillaComponent;
import bpm.vanilla.platform.core.components.impl.EventNotificationServlet;
import bpm.vanilla.platform.core.components.impl.VanillaHttpContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.listeners.event.impl.ReportExecutedEvent;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;
import bpm.workflow.runtime.IReportListenerProvider;
import bpm.workflow.runtime.ReportListener;
import bpm.workflow.runtime.WorkflowRuntimeImpl;
import bpm.workflow.wrapper.servlets.WorkflowServlet;
import bpm.workflow.wrapper.servlets.WorkflowSubmissionServlet;

public class ComponentWorkflow extends AbstractVanillaComponent implements IReportListenerProvider{
	public static final String PLUGIN_ID = "bpm.workflow.wrapper.workflowComponent";
	private IVanillaLoggerService loggingService = null;
	private WorkflowRuntimeImpl workflowRuntime;
	private HttpService httpService;
	
	private Status status = Status.UNDEFINED;
	private ComponentContext context = null;
	
	private ReportListener reportListener = new ReportListener();
	
	
	public ComponentWorkflow(){
		
	}
	public IVanillaLogger getLogger(){
		return loggingService.getLogger(PLUGIN_ID);
	}
	
	public void bind(IVanillaLoggerService service){
		this.loggingService = service;
		getLogger().info("Vanilla Logging Service binded");
	}
	
	public void unbind(IVanillaLoggerService service){
		getLogger().info("unbinding Vanilla Logging Service");
		this.loggingService = null;
	
	}
	public void bind(HttpService httpService){
		this.httpService = httpService;
	}
	public void unbind(HttpService httpService){
		this.httpService = httpService;
	}
	
	private void registerServlets(HttpContext httpContext)throws Exception{
		
		try{
			this.httpService.registerServlet(WorkflowService.SERVLET_RUNTIME, new WorkflowServlet(workflowRuntime, getLogger()), null, httpContext);
		}catch(Exception ex){
			throw new Exception("Unable to register WorkflowServlet " + ex.getMessage(), ex);
		}
		
		try{
			this.httpService.registerServlet(WorkflowService.SERVLET_TASK_SUBMISSION, new WorkflowSubmissionServlet(workflowRuntime), null, null);
		}catch(Exception ex){
			throw new Exception("Unable to register WorkflowServlet " + ex.getMessage(), ex);
		}
		
		try{
			this.httpService.registerServlet(WorkflowService.EVENT_NOTIFICATION_SERVLET, new EventNotificationServlet(this), null, null);
		}catch(Exception ex){
			throw new Exception("Unable to register WorkflowServlet " + ex.getMessage(), ex);
		}
		
		getLogger().info("servlet registered");
	}
	private void unregisterServlets(){
		this.httpService.unregister(WorkflowService.SERVLET_RUNTIME);
		this.httpService.unregister(WorkflowService.SERVLET_TASK_SUBMISSION);
		this.httpService.unregister(WorkflowService.EVENT_NOTIFICATION_SERVLET);
		getLogger().info("servlet unregistered");
	}
	
	public void activate(ComponentContext ctx) throws Exception{
		
		status = Status.STARTING;
		context = ctx;
		
		try {
			ServiceReference ref = ctx.getBundleContext().getServiceReference(HttpService.class.getName());
			String port = (String)ref.getProperty("http.port");
			
			registerInVanilla(
					VanillaComponentType.COMPONENT_WORKFLOW,
					"WorkflowRuntime",
					port
					);
			status = Status.STARTED;
		}catch(Throwable ex){
			ex.printStackTrace();
			status = Status.ERROR;
			Logger.getLogger(getClass()).error("Unable to activate component " + ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		}
		
		try{
			workflowRuntime = new WorkflowRuntimeImpl(this);
			
			IVanillaAPI api = new RemoteVanillaPlatform(
					ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), 
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			
			VanillaHttpContext httpContext = new VanillaHttpContext(api.getVanillaSecurityManager(), api.getVanillaSystemManager());
			
			registerServlets(httpContext);
		}catch(Throwable ex){
			ex.printStackTrace();
			status = Status.ERROR;
			Logger.getLogger(getClass()).error("Unable to activate component " + ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	public void desactivate(ComponentContext ctx) throws Exception{
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
	public Status getStatus() {
		return status;
	}
	
	@Override
	protected void doStop() throws Exception {
		context.disableComponent(PLUGIN_ID);
	}

	@Override
	protected void doStart() throws Exception {
		context.enableComponent(PLUGIN_ID);
		status = Status.STARTED;
	}
	@Override
	public void notify(IVanillaEvent event) {
		if (event instanceof ReportExecutedEvent){
			Logger.getLogger(getClass()).info("event notification");
			reportListener.handle((ReportExecutedEvent)event);
		}
	}
	@Override
	public ReportListener getReportListener() {
		return reportListener;
	}
}
