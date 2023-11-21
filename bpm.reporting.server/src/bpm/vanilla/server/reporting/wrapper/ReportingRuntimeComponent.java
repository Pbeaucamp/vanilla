package bpm.vanilla.server.reporting.wrapper;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import bpm.studio.jdbc.management.config.IConstants;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.components.Distributable;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.ReportingComponent;
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
import bpm.vanilla.server.reporting.server.ReportingManagerImpl;
import bpm.vanilla.server.reporting.server.ReportingServer;
import bpm.vanilla.server.reporting.server.ReportingServerConfig;
import bpm.vanilla.server.reporting.wrapper.servlets.ReportingServlet;

public class ReportingRuntimeComponent extends AbstractVanillaComponent implements Distributable {
	public static final String ID = "bpm.vanilla.server.reporting.wrapper";

	private IVanillaLoggerService loggerService;
	private HttpService httpService;
	private ReportingServer reportingServer;

	private Status status = Status.UNDEFINED;
	private ComponentContext context = null;

	private ReportingManagerImpl reportingManager;

	public ReportingRuntimeComponent() {
	}

	public IVanillaLogger getLogger() {
		return loggerService.getLogger(ID);
	}

	public void bind(IVanillaLoggerService service) {
		this.loggerService = service;
		getLogger().info("Binded IVanillaLoggerService");
	}

	public void unbind(IVanillaLoggerService service) {
		this.loggerService = service;
		getLogger().info("Unbinded IVanillaLoggerService");
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
		httpService.unregister(ReportingComponent.REPORTING_SERVLET);
		httpService.unregister(ReportingComponent.REPORTING_LOAD_EVALUATOR_SERVLET);
	}

	private void registerServlets() throws Exception {

		Properties p = null;
		if (System.getProperty("bpm.vanilla.configurationFile") != null) {
			getLogger().info("Loading configuration with bpm.vanilla.configurationFile=" + System.getProperty("bpm.vanilla.configurationFile"));
			try {
				FileInputStream fis = new FileInputStream(System.getProperty("bpm.vanilla.configurationFile"));
				p = new Properties();
				p.load(fis);
				getLogger().info("Configuration loaded");
			} catch (Exception ex) {
				getLogger().warn("Loading configuration failed " + ex.getMessage(), ex);
				p = null;
			}
		}

		if (p == null) {
			getLogger().error("No configuration file found");
			throw new Exception("bpm.vanilla.configurationFile not specified or the file does not exist");
		}

		getLogger().info("JDBC JAR location : " + IConstants.getJdbcJarFolder());
		getLogger().info("JDBC JAR descriptor : " + IConstants.getJdbcDriverXmlFile());

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();

		IVanillaAPI api = new RemoteVanillaPlatform(config.getVanillaServerUrl(), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));

		VanillaHttpContext httpContext = new VanillaHttpContext(api.getVanillaSecurityManager(), api.getVanillaSystemManager());

		/*
		 * init ReportingServer
		 */
		getLogger().info("initing ReportingServer ....");
		ReportingServerConfig serverConfig = new ReportingServerConfig(p);
		reportingServer = null;
		try {
			reportingServer = new ReportingServer(this, Logger.getLogger(ReportingRuntimeComponent.ID), InetAddress.getLocalHost().getHostAddress());
			reportingServer.init(serverConfig);
			getLogger().info("ReportingServer inited");
		} catch (Exception e) {
			getLogger().warn("Unable to init server, maybe the server URL method to get it", e);
			getLogger().warn("The Server's Reporting URL internal URL is inited at blank");

			try {
				reportingServer = new ReportingServer(this, Logger.getLogger(ReportingRuntimeComponent.ID), "");
				reportingServer.init(serverConfig);
				getLogger().info("ReportingServer inited");
			} catch (Exception ex) {
				reportingServer = null;
				ex.printStackTrace();
				getLogger().error("Unable to init server - " + ex.getMessage(), ex);
				throw new Exception("Unable to init server - " + ex.getMessage(), ex);
			}
		}

		try {
			reportingServer.start();
			getLogger().info("ReportingServer started");
		} catch (Exception ex) {
			reportingServer = null;
			ex.printStackTrace();
			getLogger().error("Unable to start server : " + ex.getMessage(), ex);
			throw new Exception("Unable to start server : " + ex.getMessage(), ex);
		}
		
		reportingManager = new ReportingManagerImpl(this, reportingServer);

		try {
			httpService.registerServlet(ReportingComponent.REPORTING_SERVLET, new ReportingServlet(reportingManager), null, httpContext);
			getLogger().info("registered servlet /reportingServlet");
		} catch (Exception e) {
			getLogger().error("Unable to register alias /reportingServlet" + e.getMessage());
			throw e;
		}

		try {
			httpService.registerServlet(ReportingComponent.REPORTING_LOAD_EVALUATOR_SERVLET, new LoadEvaluationServlet(this), null, httpContext);
		} catch (Exception e) {
			getLogger().error("Unable to register " + ReportingComponent.REPORTING_LOAD_EVALUATOR_SERVLET + " alias - " + e.getMessage());
			throw e;
		}
	}

	public void activate(ComponentContext ctx) throws Exception {
		try {
			this.context = ctx;

			status = Status.STARTING;
			
			try {
				registerServlets();
			} catch (Exception ex) {
				getLogger().error("Error when regsitering servlets - " + ex.getMessage(), ex);
				this.httpService = null;
				status = Status.ERROR;
				throw new RuntimeException(ex);
			}

			// get the port from the HttpService
			ServiceReference ref = ctx.getBundleContext().getServiceReference(HttpService.class.getName());
			String port = (String) ref.getProperty("http.port");

			try {
				registerInVanilla(VanillaComponentType.COMPONENT_REPORTING, "ReportingRuntime", port);
				status = Status.STARTED;
			} catch (Exception ex) {
				getLogger().error("Error when registring ComponentReporting within VanillaPlatform - " + ex.getMessage(), ex);
				this.httpService = null;
				status = Status.ERROR;
				throw new RuntimeException(ex);
			}
		} catch (Throwable e) {
			getLogger().error("Error in reporting activate - " + e.getMessage(), e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void desactivate(ComponentContext ctx) throws Exception {
		// we make a difference, to know if the all bundle is stopping (not
		// vanilla action)
		// or if only the service is stopping (a vanilla action or cmd line
		// disabling the service)
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
	protected void doStop() throws Exception {
		context.disableComponent(ID);
	}

	@Override
	protected void doStart() throws Exception {
		context.enableComponent(ID);
	}

	@Override
	public int computeLoadEvaluation() throws Exception {
		/*
		 * heuristic function =
		 * 
		 * F= 1/queueMaxSize * ((2*runninngTasks + waitingTasks) - 1)
		 */

		int maxSz = reportingServer.getConfig().getMaxTasks();
		int runningN = reportingServer.getTaskManager().getRunningTasks().size();
		int waitingN = reportingServer.getTaskManager().getWaitingTasks().size();

		float eval = (1.0f / maxSz) * ((2 * runningN) + waitingN - 1.0f) * 100;
		return Float.valueOf(eval).intValue();
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void notify(IVanillaEvent event) {
	}

}
