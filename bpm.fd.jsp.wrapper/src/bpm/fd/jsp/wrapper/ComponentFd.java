package bpm.fd.jsp.wrapper;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.jsp.jasper.JspServlet;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import bpm.fd.jsp.wrapper.deployer.BirtPregenerationServlet;
import bpm.fd.jsp.wrapper.deployer.DeployFromFdWebServlet;
import bpm.fd.jsp.wrapper.deployer.DeploymentServlet;
import bpm.fd.jsp.wrapper.deployer.GenerateFromFaweb;
import bpm.fd.jsp.wrapper.deployer.VanillaFormDeploymentServlet;
import bpm.fd.jsp.wrapper.deployer.VanillaValidationFormDeploymentServlet;
import bpm.fd.jsp.wrapper.runtime.servlets.ComponentServlet;
import bpm.fd.jsp.wrapper.runtime.servlets.DrillServlet;
import bpm.fd.jsp.wrapper.runtime.servlets.FolderPageServlet;
import bpm.fd.jsp.wrapper.runtime.servlets.ListDirtyComponentServlet;
import bpm.fd.jsp.wrapper.runtime.servlets.ParameterServlet;
import bpm.fd.jsp.wrapper.runtime.servlets.PopupModelServlet;
import bpm.fd.jsp.wrapper.runtime.servlets.SlicerServlet;
import bpm.fd.jsp.wrapper.runtime.servlets.WebDashboardServlet;
import bpm.fd.jsp.wrapper.runtime.servlets.ZoomChartServlet;
import bpm.fd.runtime.model.controler.Controler;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.components.FreeDashboardComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.impl.AbstractVanillaComponent;
import bpm.vanilla.platform.core.components.impl.VanillaHttpContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

public class ComponentFd extends AbstractVanillaComponent {

	private IVanillaLoggerService loggerService;
	private HttpService httpService;

	private Status status = Status.UNDEFINED;
	private ComponentContext context = null;

	public IVanillaLogger getLogger() {
		return loggerService.getLogger(Activator.ID);
	}

	public void bind(IVanillaLoggerService loggerService) {
		this.loggerService = loggerService;
		getLogger().info("IVanillaLoggerService binded");
	}

	public void unbind(IVanillaLoggerService loggerService) {
		getLogger().info("IVanillaLoggerService unbinded");
		this.loggerService = null;

	}

	public void bind(HttpService service) {
		this.httpService = service;
		getLogger().info("Binded HttpService");
	}

	public void unbind(HttpService service) {
		this.httpService = null;
		getLogger().info("Unbinded HttpService");
	}

	private void unregisterServlets() {

		httpService.unregister(FreeDashboardComponent.SERVLET_DEPLOY);
		getLogger().info("unregistered servlet /freedashDeployer");
		
		httpService.unregister("/birtPregeneration");
		getLogger().info("unregistered servlet /birtPregeneration");
		
		httpService.unregister(FreeDashboardComponent.SERVLET_VALIDATE_FORM);
		getLogger().info("unregistered servlet " + FreeDashboardComponent.SERVLET_VALIDATE_FORM);
		
		httpService.unregister(FreeDashboardComponent.SERVLET_DEPLOY_FORM);
		getLogger().info("unregistered servlet /vanillaFormDeployer");
		
		httpService.unregister(FreeDashboardComponent.SERVLET_DEPLOY_ZIP);
		getLogger().info("unregistered servlet " + FreeDashboardComponent.SERVLET_DEPLOY_ZIP);
		
		httpService.unregister(FreeDashboardComponent.SERVLET_SLICER);
		getLogger().info("unregistered servlet " + FreeDashboardComponent.SERVLET_SLICER);
		
		httpService.unregister(FreeDashboardComponent.SERVLET_COMPONENT);
		getLogger().info("unregistered servlet " + FreeDashboardComponent.SERVLET_COMPONENT);
		
		httpService.unregister(FreeDashboardComponent.SERVLET_DRILL);
		getLogger().info("unregistered servlet " + FreeDashboardComponent.SERVLET_DRILL);
		
		httpService.unregister(FreeDashboardComponent.SERVLET_PARAMETER);
		getLogger().info("unregistered servlet " + FreeDashboardComponent.SERVLET_PARAMETER);
		
		httpService.unregister(FreeDashboardComponent.SERVLET_LIST_DIRTY_COMPONENTS);
		getLogger().info("unregistered servlet " + FreeDashboardComponent.SERVLET_LIST_DIRTY_COMPONENTS);
		
		httpService.unregister(FreeDashboardComponent.SERVLET_POPUP_MODEL);
		getLogger().info("unregistered servlet " + FreeDashboardComponent.SERVLET_POPUP_MODEL);
		
		httpService.unregister("/freedashboardRuntime/zoomComponent");
		getLogger().info("unregistered servlet /freedashboardRuntime/zoomComponent");
		
		httpService.unregister("/generateFromFaWeb");
		getLogger().info("unregistered servlet /generateFromFaWeb");

		httpService.unregister("/*.jsp");
		getLogger().info("unregistered servlet /*.jsp");

		httpService.unregister("/freedashboardRuntime/ckeditor");
		getLogger().info("unregistered servlet /ckeditor");
		
		httpService.unregister("/freedashboardRuntime/datagrid");
		getLogger().info("unregistered servlet /datagrid");

		httpService.unregister("/freedashboardRuntime/FusionCharts");
		getLogger().info("unregistered servlet /FusionCharts");

		httpService.unregister("/freedashboardRuntime/FusionMaps");
		getLogger().info("unregistered servlet /FusionMaps");

		httpService.unregister("/freedashboardRuntime/menu");
		getLogger().info("unregistered servlet /menu");
		
		httpService.unregister("/freedashboardRuntime/stackableCell");
		getLogger().info("unregistered servlet /stackableCell");
		
		httpService.unregister("/freedashboardRuntime/timer");
		getLogger().info("unregistered servlet /timer");
		
		httpService.unregister("/freedashboardRuntime/comment");
		getLogger().info("unregistered servlet /freedashboardRuntime/comment");
		
		httpService.unregister("/freedashboardRuntime/OpenLayers");
		getLogger().info("unregistered servlet /freedashboardRuntime/OpenLayers");
		
		httpService.unregister("/freedashboardRuntime/jquery");
		getLogger().info("unregistered servlet /freedashboardRuntime/jquery");
		
		httpService.unregister("/freedashboardRuntime/js");
		getLogger().info("unregistered servlet /freedashboardRuntime/js");
		
		httpService.unregister("/freedashboardRuntime/fmmap");
		getLogger().info("unregistered servlet /freedashboardRuntime/fmmap");
		
		httpService.unregister("/freedashboardRuntime/VanillaMap");
		getLogger().info("unregistered servlet /freedashboardRuntime/VanillaMap");

		httpService.unregister("/freedashboardRuntime/d3");
		getLogger().info("unregistered servlet /freedashboardRuntime/d3");
		
		httpService.unregister("/freedashboardRuntime/bootstrap");
	}

	private void registerServlets(BundleContext ctx) throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = config.getVanillaServerUrl();

		IVanillaAPI api = new RemoteVanillaPlatform(config.getVanillaServerUrl(), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));

		VanillaHttpContext httpCtx = new VanillaHttpContext(api.getVanillaSecurityManager(), api.getVanillaSystemManager());

		try {
			httpService.registerServlet(FreeDashboardComponent.SERVLET_DEPLOY, new DeploymentServlet(this, vanillaUrl), null, httpCtx);
			getLogger().info("registered servlet /freedashDeployer");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /freedashDeployer" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerServlet("/birtPregeneration", new BirtPregenerationServlet(vanillaUrl), null, httpCtx);
			getLogger().info("registered servlet /birtPregeneration");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /birtPregeneration" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerServlet(FreeDashboardComponent.SERVLET_VALIDATE_FORM, new VanillaValidationFormDeploymentServlet(this, vanillaUrl), null, httpCtx);
			getLogger().info("registered servlet " + FreeDashboardComponent.SERVLET_VALIDATE_FORM);
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /vanillaValidationFormDeployer" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerServlet(FreeDashboardComponent.SERVLET_DEPLOY_FORM, new VanillaFormDeploymentServlet(this, vanillaUrl), null, httpCtx);
			getLogger().info("registered servlet /vanillaFormDeployer");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /vanillaFormDeployer" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerServlet(FreeDashboardComponent.SERVLET_DEPLOY_ZIP, new DeployFromFdWebServlet(vanillaUrl), null, httpCtx);
			getLogger().info("registered servlet /fdWebServlet");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /fdWebServlet" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerServlet(FreeDashboardComponent.SERVLET_SLICER, new SlicerServlet(), null, null);
			getLogger().info("registered servlet /fdWebServlet");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /fdWebServlet" + e.getMessage());
			throw e;
		}
		try {
			httpService.registerServlet(FreeDashboardComponent.SERVLET_COMPONENT, new ComponentServlet(), null, null);
			getLogger().info("registered servlet /fdWebServlet");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /fdWebServlet" + e.getMessage());
			throw e;
		}
		
		try {
			httpService.registerServlet(FreeDashboardComponent.SERVLET_WEB_DASHBOARD, new WebDashboardServlet(), null, null);
			getLogger().info("registered servlet /WebDashboardServlet");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /WebDashboardServlet" + e.getMessage());
			throw e;
		}
		
		try {
			httpService.registerServlet(FreeDashboardComponent.SERVLET_FOLDER, new FolderPageServlet(), null, null);
			getLogger().info("registered servlet /FolderPageServlet");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /FolderPageServlet" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerServlet(FreeDashboardComponent.SERVLET_DRILL, new DrillServlet(), null, null);
			getLogger().info("registered servlet /fdWebServlet");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /fdWebServlet" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerServlet(FreeDashboardComponent.SERVLET_PARAMETER, new ParameterServlet(), null, null);
			getLogger().info("registered servlet /fdWebServlet");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /fdWebServlet" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerServlet(FreeDashboardComponent.SERVLET_LIST_DIRTY_COMPONENTS, new ListDirtyComponentServlet(), null, null);
			getLogger().info("registered servlet /fdWebServlet");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /fdWebServlet" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerServlet(FreeDashboardComponent.SERVLET_POPUP_MODEL, new PopupModelServlet(), null, null);
			getLogger().info("registered servlet /fdWebServlet");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /fdWebServlet" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerServlet("/freedashboardRuntime/zoomComponent", new ZoomChartServlet(), null, null);
			getLogger().info("registered servlet /fdWebServlet");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /fdWebServlet" + e.getMessage());
			throw e;
		}

		try {
			// before merge, ere :
			// httpService.registerServlet("/generateFromFaWeb", new
			// GenerateFromFaweb(this, p.getProperty(VANILLA_SERVER_URL)), null,
			// null);
			httpService.registerServlet("/generateFromFaWeb", new GenerateFromFaweb(this, vanillaUrl), null, httpCtx);
			getLogger().info("registered servlet /generateFromFaWeb");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /generateFromFaWeb" + e.getMessage());
			throw e;
		}

		// try{
		// httpService.registerResources("/pre-generation","/pre-generation",null);
		// }catch(Exception e){
		// getLogger().error("Unable to register alias /timer" +
		// e.getMessage());
		// throw e;
		// }
		//		
		// try{
		// httpService.registerResources("/generation","/generation",null);
		// }catch (NamespaceException e) {
		//
		// }catch(Exception e){
		// getLogger().error("Unable to register alias /timer" +
		// e.getMessage());
		// throw e;
		// }

		try {
			Category c = null;
			httpService.registerServlet("/*.jsp", new JspServlet(Activator.getContext().getBundle(), "/generation", "/freedashboardRuntime"), null, null);
			getLogger().info("registered servlet /vanillaValidationFormDeployer");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /vanillaValidationFormDeployer" + e.getMessage());
			throw e;
		}

		// try{
		// httpService.registerResources("/pre-generation","/pre-generation",null);
		// }catch(Exception e){
		// getLogger().error("Unable to register alias /timer" +
		// e.getMessage());
		// throw e;
		// }
		//		
		try {
			httpService.registerResources("/freedashboardRuntime/ckeditor", "/ckeditor", null);
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /timer" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerResources("/freedashboardRuntime/datagrid", "/datagrid", null);
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /datagrid" + e.getMessage());
			throw e;
		}
		
		try {
			httpService.registerResources("/freedashboardRuntime/datepicker", "/datepicker", null);
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /datepicker" + e.getMessage());
			throw e;
		}


		// try{
		// httpService.registerResources("/freedashboardRuntime/datepicker","/datepicker",null);
		// }catch (NamespaceException e) {
		//
		// }catch(Exception e){
		// getLogger().error("Unable to register alias /datepicker" +
		// e.getMessage());
		// throw e;
		// }

		try {
			httpService.registerResources("/freedashboardRuntime/FusionCharts", "/FusionCharts", null);
			getLogger().info("registered servlet /FusionCharts");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /FusionCharts" + e.getMessage());
			throw e;
		}
		
		try {
			httpService.registerResources("/freedashboardRuntime/chartjs", "/chartjs", null);
			getLogger().info("registered servlet /chartjs");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /chartjs" + e.getMessage());
			throw e;
		}

//		try {
//			httpService.registerResources("/freedashboardRuntime/Nimes", "/Nimes", null);
//		} catch (NamespaceException e) {
//
//		} catch (Exception e) {
//			getLogger().error("Unable to register alias /Nimes" + e.getMessage());
//			throw e;
//		}

		try {
			httpService.registerResources("/freedashboardRuntime/FusionMaps", "/FusionMaps", null);
			getLogger().info("registered servlet /FusionMaps");
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /FusionMaps" + e.getMessage());
			throw e;
		}
		try {
			httpService.registerResources("/freedashboardRuntime/menu", "/menu", null);
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /menu" + e.getMessage());
			throw e;
		}

		// try{
		// httpService.registerResources("/freedashboardRuntime/ofc","/ofc",null);
		// }catch (NamespaceException e) {
		//
		// }catch(Exception e){
		// getLogger().error("Unable to register alias /ofc" + e.getMessage());
		// throw e;
		// }
		//		

		// try{
		// httpService.registerResources("/freedashboardRuntime/slider","/slider",null);
		// }catch (NamespaceException e) {
		//
		// }catch(Exception e){
		// getLogger().error("Unable to register alias /slider" +
		// e.getMessage());
		// throw e;
		// }

		try {
			httpService.registerResources("/freedashboardRuntime/stackableCell", "/stackableCell", null);
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /slider" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerResources("/freedashboardRuntime/timer", "/timer", null);
		} catch (NamespaceException e) {

		} catch (Exception e) {
			getLogger().error("Unable to register alias /timer" + e.getMessage());
			throw e;
		}

		// try{
		// httpService.registerResources("/freedashboardRuntime/tld", "/tld",
		// null);
		// getLogger().info("registered servlet /vanillaValidationFormDeployer");
		// }catch (NamespaceException e) {
		//			
		// }catch(Exception e){
		// getLogger().error("Unable to register alias /vanillaValidationFormDeployer"
		// + e.getMessage());
		// throw e;
		// }
		try {
			httpService.registerResources("/freedashboardRuntime/comment", "/comment", null);
			getLogger().info("registered resource /comment");
		} catch (Exception e) {
			getLogger().error("Unable to resource alias /comment" + e.getMessage());
			throw e;
		}
		try {
			httpService.registerResources("/freedashboardRuntime/OpenLayers", "/OpenLayers", null);
			getLogger().info("registered resource /OpenLayers");
		} catch (Exception e) {
			getLogger().error("Unable to resource alias /OpenLayers" + e.getMessage());
			throw e;
		}
		try {
			httpService.registerResources("/freedashboardRuntime/Ol465", "/Ol465", null);
			getLogger().info("registered resource /Ol465");
		} catch (Exception e) {
			getLogger().error("Unable to resource alias /Ol465" + e.getMessage());
			throw e;
		}
		try {
			httpService.registerResources("/freedashboardRuntime/OpenLayers_3_5", "/OpenLayers_3_5", null);
			getLogger().info("registered resource /OpenLayers_3_5");
		} catch (Exception e) {
			getLogger().error("Unable to resource alias /OpenLayers_3_5" + e.getMessage());
			throw e;
		}
		try {
			httpService.registerResources("/freedashboardRuntime/jquery", "/jquery", null);
			getLogger().info("registered resource /jquery");
		} catch (Exception e) {
			getLogger().error("Unable to resource alias /jquery" + e.getMessage());
			throw e;
		}
		try {
			httpService.registerResources("/freedashboardRuntime/js", "/js", null);
			getLogger().info("registered resource /js");
		} catch (Exception e) {
			getLogger().error("Unable to resource alias /js" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerResources("/freedashboardRuntime/fmmap", "/fmmap", null);
			getLogger().info("registered resource /fmmap");
		} catch (Exception e) {
			getLogger().error("Unable to resource alias /js" + e.getMessage());
			throw e;
		}
		try {
			httpService.registerResources("/freedashboardRuntime/VanillaMap", "/VanillaMap", null);
			getLogger().info("registered resource /VanillaMap");
		} catch (Exception e) {
			getLogger().error("Unable to resource alias /VanillaMap" + e.getMessage());
			throw e;
		}
		try {
			httpService.registerResources("/freedashboardRuntime/d3", "/d3", null);
			getLogger().info("registered resource /d3");
		} catch (Exception e) {
			getLogger().error("Unable to resource alias /d3" + e.getMessage());
			throw e;
		}
		try {
			httpService.registerResources("/freedashboardRuntime/bootstrap", "/bootstrap", null);
			getLogger().info("registered resource /bootstrap");
		} catch (Exception e) {
			getLogger().error("Unable to resource alias /bootstrap" + e.getMessage());
			throw e;
		}
		try {
			// XXX : requested otherwise the CSS from dashboards cannot be
			// accessed
			httpService.registerResources("/", "/", null);
		} catch (NamespaceException e) {
		} catch (Exception e) {
			throw e;
		}
	}

	public void activate(ComponentContext ctx) throws Exception {
		status = Status.STARTING;
		this.context = ctx;

		try {
			registerServlets(ctx.getBundleContext());
		} catch (Exception ex) {
			getLogger().error("Error when regsitering servlets - " + ex.getMessage(), ex);
			status = Status.ERROR;
			this.httpService = null;
			throw new RuntimeException(ex);
		}

		// get the port from the HttpService
		ServiceReference ref = ctx.getBundleContext().getServiceReference(HttpService.class.getName());
		String port = (String) ref.getProperty("http.port");

		try {
			registerInVanilla(VanillaComponentType.COMPONENT_FREEDASHBOARD, "FreeDashBoardRuntime", port);
			status = Status.STARTED;
		} catch (Exception ex) {
			getLogger().error("Error when registring ComponentFd within VanillaPlatform - " + ex.getMessage(), ex);
			status = Status.ERROR;
			this.httpService = null;
			throw new RuntimeException(ex);
		}

		// init controler
		try {
			Bundle bpmFdJspWrapperBundle = Platform.getBundle(Activator.ID);
			String p = FileLocator.getBundleFile(bpmFdJspWrapperBundle).getAbsolutePath();// Platform.getInstallLocation().getURL().getPath()
																							// +
																							// "/plugins/"
																							// +
																							// Activator.ID
																							// +
																							// "_"
																							// +
																							// bpmFdJspWrapperBundle.getHeaders().get("Bundle-Version")
																							// +
																							// "/";
																							// ;
			Controler.initGenerationFolder(p + "/generation");
		} catch (Throwable t) {
			Logger.getLogger(getClass()).error(t.getMessage(), t);
			throw new RuntimeException(t);
		}

	}

	public void desactivate(ComponentContext ctx) throws Exception {
		if (ctx.getBundleContext().getBundle().getState() == Bundle.STOPPING) {
			status = Status.STOPPING;
			Controler.getInstance().destroyAll();
			unregisterServlets();
			unregisterFromVanilla(getIdentifier());
			status = Status.STOPPED;
		}
		else if (ctx.getBundleContext().getBundle().getState() == Bundle.ACTIVE) {
			status = Status.STOPPING;
			Controler.getInstance().destroyAll();
			try {
				unregisterServlets();
			} catch (Throwable e) {
				e.printStackTrace();
			}
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
		context.disableComponent(Activator.ID);
	}

	@Override
	protected void doStart() throws Exception {
		context.enableComponent(Activator.ID);
	}

	//
	// @Override
	// public int computeLoadEvaluation() {
	// 
	// return 0;
	// }

	@Override
	public void notify(IVanillaEvent event) {
		

	}
}
