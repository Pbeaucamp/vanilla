package bpm.vanilla.server.gateway.wrapper;

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
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
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
import bpm.vanilla.server.gateway.server.GatewayManagerImpl;
import bpm.vanilla.server.gateway.server.GatewayServer;
import bpm.vanilla.server.gateway.server.GatewayServerConfig;
import bpm.vanilla.server.gateway.wrapper.servlets.XStreamGatewayServlet;

public class GatewayRuntimeWrapper extends AbstractVanillaComponent implements Distributable {
	public static final String ID = "bpm.vanilla.server.gateway.wrapper";
	private GatewayServer gatewayServer;

	private HttpService httpService;
	private IVanillaLoggerService loggerService;

	private Status status = Status.UNDEFINED;
	private ComponentContext context = null;

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
		unregisterServlets();
		this.httpService = null;

		getLogger().info("Unbinded HttpService");

	}

	private void unregisterServlets() {
		httpService.unregister(GatewayComponent.GATEWAY_XTSREAM_SERVLET);
		httpService.unregister(GatewayComponent.GATEWAY_LOAD_EVALUATOR_SERVLET);
		getLogger().info("Servlet unregistered");
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
			getLogger().warn("No configuration file found - creating a default one");
			p = new Properties();
			// XXX for debug purpose
			p.setProperty(GatewayServerConfig.VANILLA_SERVER_URL, "http://localhost:8080/vanilla");
			p.setProperty(GatewayServerConfig.RUNTIME_MAX_ROWS, "50000");
			getLogger().info("Configuration created");
		}

		getLogger().info("JDBC JAR location : " + IConstants.getJdbcJarFolder());
		getLogger().info("JDBC JAR descriptor : " + IConstants.getJdbcDriverXmlFile());

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();

		IVanillaAPI api = new RemoteVanillaPlatform(config.getVanillaServerUrl(), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));

		VanillaHttpContext httpContext = new VanillaHttpContext(api.getVanillaSecurityManager(), api.getVanillaSystemManager());

		String host = null;

		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception ex) {
			Logger.getLogger(getClass()).warn("Could not get HostAdress, we will use localhost - " + ex.getMessage());
			host = "localhost";
		}
		try {
			gatewayServer = new GatewayServer(this, Logger.getLogger("bpm.vanilla.server.gateway"), host);
			gatewayServer.init(new GatewayServerConfig(p));
		} catch (Exception e) {
			throw new Exception("Unable to init GatewayRuntimeServer " + e.getMessage(), e);
		}

		try {
			gatewayServer.start();
		} catch (Exception ex) {
			throw new Exception("Unable to start GatewayRuntimeServer", ex);
		}
		
		GatewayManagerImpl gatewayManager = new GatewayManagerImpl(this, gatewayServer);

		// register the LoadEvaluator Servlet
		try {
			httpService.registerServlet(GatewayComponent.GATEWAY_LOAD_EVALUATOR_SERVLET, new LoadEvaluationServlet(this), null, httpContext);
		} catch (Exception e) {
			getLogger().error("Unable to register " + GatewayComponent.GATEWAY_LOAD_EVALUATOR_SERVLET + " alias - " + e.getMessage());
			throw e;
		}

		// register xstream servlet
		try {
			httpService.registerServlet(GatewayComponent.GATEWAY_XTSREAM_SERVLET, new XStreamGatewayServlet(gatewayManager), null, httpContext);
		} catch (Exception e) {
			getLogger().error("Unable to register " + GatewayComponent.GATEWAY_XTSREAM_SERVLET + " alias - " + e.getMessage());
			throw e;
		}

		getLogger().info("Servlets registered");
	}

	public IVanillaLogger getLogger() {
		return loggerService.getLogger(ID);
	}

	public void activate(ComponentContext ctx) throws Exception {
		try {
			status = Status.STARTING;
			context = ctx;
	
			try {
				registerServlets();
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
				registerInVanilla(VanillaComponentType.COMPONENT_GATEWAY, "GatewayRuntime", port);
				status = Status.STARTED;
			} catch (Exception ex) {
				getLogger().error("Error when registring ComponentGateway within VanillaPlatform - " + ex.getMessage(), ex);
				status = Status.ERROR;
				this.httpService = null;
				throw new RuntimeException(ex);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void desactivate(ComponentContext ctx) throws Exception {
		if (ctx.getBundleContext().getBundle().getState() == Bundle.STOPPING) {
			// the entire bundle is shutting.
			status = Status.STOPPING;
			unregisterServlets();
			unregisterFromVanilla(getIdentifier());
			status = Status.STOPPED;
		}
		else if (ctx.getBundleContext().getBundle().getState() == Bundle.ACTIVE) {
			status = Status.STOPPING;
			unregisterServlets();
			// update vanilla status
			// unregisterFromVanilla(getIdentifier());
			IVanillaComponentIdentifier ident = getIdentifier();
			ident.setComponentStatus(Status.STOPPED.getStatus());
			updateInVanilla(ident);
			status = Status.STOPPED;
		}
	}

	@Override
	public int computeLoadEvaluation() {
		/*
		 * heuristic function =
		 * 
		 * F= 1/queueMaxSize * ((2*runninngTasks + waitingTasks) - 1)
		 */

		int maxSz = gatewayServer.getConfig().getMaxTasks();
		int runningN = gatewayServer.getTaskManager().getRunningTasks().size();
		int waitingN = gatewayServer.getTaskManager().getWaitingTasks().size();

		float eval = (1.0f / maxSz) * ((2 * runningN) + waitingN - 1.0f) * 100;
		return Float.valueOf(eval).intValue();
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
