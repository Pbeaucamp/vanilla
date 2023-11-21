package bpm.forms.runtime;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import bpm.forms.core.design.IServiceProvider;
import bpm.forms.core.runtime.IDataSourceProvider;
import bpm.forms.core.runtime.IInstanceLauncher;
import bpm.forms.core.tools.IFactoryModelElement;
import bpm.forms.dao.VanillaFormsDaoComponent;
import bpm.forms.instanciator.VanillaFormInstanciatorComponent;
import bpm.forms.runtime.dao.servlets.DefinitionServlet;
import bpm.forms.runtime.dao.servlets.InstanceServlet;
import bpm.forms.runtime.dao.servlets.LaunchServlet;
import bpm.forms.runtime.servlets.GetFormServlet;
import bpm.forms.runtime.servlets.GetValidationFormServlet;
import bpm.forms.runtime.servlets.InvalidationFormServlet;
import bpm.forms.runtime.servlets.SubmitFormServlet;
import bpm.forms.runtime.servlets.ValidationFormServlet;
import bpm.vanilla.platform.core.components.IFormComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.impl.AbstractVanillaComponent;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

public class VanillaFormsRuntimeComponent extends AbstractVanillaComponent{
	public static final String PLUGIN_ID = "bpm.forms.runtime.VanillaFormsRuntimeComponent";
	
	
	private HttpService httpService;
	private IVanillaLoggerService loggerService;

	
	private VanillaFormsDaoComponent daoComponent;
	private VanillaFormInstanciatorComponent instanciator;
	
	
	private Status status = Status.UNDEFINED;
	private ComponentContext context = null;
	
	public void bindLoggingService(IVanillaLoggerService service){
		this.loggerService = service;

		
		getLogger().info("Binded IVanillaLoggerService");
	}
	public void unbindLoggingService(IVanillaLoggerService service){
		getLogger().info("Unbinding IVanillaLoggerService");
		this.loggerService = null;
	}
	public IVanillaLogger getLogger(){
		return loggerService.getLogger(PLUGIN_ID);
	}
	
	

	public IServiceProvider getFormServiceProvider(){
		return this.daoComponent;
	}
	
	
	
	
	public IInstanceLauncher getFormInstanceLauncher(){
		return instanciator;
	}
	
	
	
	
	
	public IFactoryModelElement getFormFactory(){
		return this.daoComponent.getFactoryModelElement();
	}
	
	
	
	
	
	
	public IDataSourceProvider getFormDataSourceProvider(){
		return this.daoComponent;
	}
	
	
	
	
	
	public void bindHttpService(HttpService service){
		this.httpService = service;
		getLogger().info("Binded HttpService");
		
	}
	public void unbindHttpService(HttpService service){
		getLogger().info("Unbinding HttpService");
		unregisterServlets();
		
		this.httpService = null;
	}

	public VanillaContext getVanillaContext(String ctxLogin, String ctxPassword, String ctxGroupId) throws Exception{
		return new VanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), ctxLogin, ctxPassword, ctxGroupId);
	}
	
	public void activate(ComponentContext ctx) throws Exception{
		status = Status.STARTING;
		context = ctx;
		try{
			daoComponent = new VanillaFormsDaoComponent();
			Logger.getLogger(getClass()).info("FormsDao created");
		}catch(Exception ex){
			status = Status.ERROR;
			Logger.getLogger(getClass()).error("Unable to create VanillaFormsDaoComponent - " + ex.getMessage(), ex);
			throw new RuntimeException("Unable to create FormDao - " + ex.getMessage(), ex);
		}
		
		this.instanciator = new VanillaFormInstanciatorComponent(daoComponent.getFactoryModelElement(), daoComponent);
		try{
			registerServlets();
		}catch(Exception ex){
			status = Status.ERROR;
			Logger.getLogger(getClass()).error("Failed to register FormsServlet : " + ex.getMessage(), ex);
			throw new RuntimeException("Failed to register FormsServlet, the component is not available");
		}
		
		// get the port from the HttpService
		try{
			ServiceReference ref = ctx.getBundleContext().getServiceReference(HttpService.class.getName());
			String port = (String)ref.getProperty("http.port");
			registerInVanilla(VanillaComponentType.COMPONENT_VANILLA_FORMS, "VanillaForms", port);
			status = Status.STARTED;
		}catch(Exception ex){
			ex.printStackTrace();
			status = Status.ERROR;
			Logger.getLogger(getClass()).error("Unable to activate component " + ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		}
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
		
		daoComponent.desactivate(ctx);
		
		Logger.getLogger(getClass()).info("Forms Component disabled");
	}

	
	private void unregisterServlets(){
		httpService.unregister(IFormComponent.SERVLET_SUBMIT_FORM);
		httpService.unregister(IFormComponent.SERVLET_GET_FORM);
		httpService.unregister(IFormComponent.SERVLET_GET_VALIDATION_FORM);
		httpService.unregister(IFormComponent.SERVLET_VALIDATE_FORM);
		httpService.unregister(IFormComponent.SERVLET_INVALIDATE_FORM);
		httpService.unregister(IFormComponent.SERVLET_FORM_DEFINITION);
		httpService.unregister(IFormComponent.SERVLET_FORM_INSTANCE);
		httpService.unregister(IFormComponent.SERVLET_FORM_LAUNCHER);
		Logger.getLogger(getClass()).info("Unregistered Forms Servlets");
	}
	
	private void registerServlets() throws Exception{
		try {
			httpService.registerServlet(IFormComponent.SERVLET_SUBMIT_FORM, new SubmitFormServlet(this), null, null);
		}catch(Exception e){
			getLogger().error("Error when aliasing "+IFormComponent.SERVLET_SUBMIT_FORM + e.getMessage(), e);
			throw e;
		}
		
		try {
			httpService.registerServlet(IFormComponent.SERVLET_GET_FORM, new GetFormServlet(this), null, null);
		}catch(Exception e){
			getLogger().error("Error when aliasing /getForm" + e.getMessage(), e);
			throw e;
		}
		try {
			httpService.registerServlet(IFormComponent.SERVLET_GET_VALIDATION_FORM, new GetValidationFormServlet(this), null, null);
		}catch(Exception e){
			getLogger().error("Error when aliasing " + e.getMessage(), e);
			throw e;
		}
		try {
			httpService.registerServlet(IFormComponent.SERVLET_VALIDATE_FORM, new ValidationFormServlet(this), null, null);
		}catch(Exception e){
			getLogger().error("Error when aliasing " + e.getMessage(), e);
			throw e;
		}
		try {
			httpService.registerServlet(IFormComponent.SERVLET_INVALIDATE_FORM, new InvalidationFormServlet(this), null, null);
		}catch(Exception e){
			getLogger().error("Error when aliasing " + e.getMessage(), e);
			throw e;
		}
		try {
			httpService.registerServlet(IFormComponent.SERVLET_FORM_DEFINITION, new DefinitionServlet(this), null, null);
		}catch(Exception e){
			getLogger().error("Error when aliasing " + e.getMessage(), e);
			throw e;
		}
		try {
			httpService.registerServlet(IFormComponent.SERVLET_FORM_INSTANCE, new InstanceServlet(this), null, null);
		}catch(Exception e){
			getLogger().error("Error when aliasing " + e.getMessage(), e);
			throw e;
		}
		try {
			httpService.registerServlet(IFormComponent.SERVLET_FORM_LAUNCHER, new LaunchServlet(this), null, null);
		}catch(Exception e){
			getLogger().error("Error when aliasing " + e.getMessage(), e);
			throw e;
		}
		Logger.getLogger(getClass()).info("Registered Forms Servlets");
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
	}
	@Override
	public void notify(IVanillaEvent event) {
		
		
	}
}
