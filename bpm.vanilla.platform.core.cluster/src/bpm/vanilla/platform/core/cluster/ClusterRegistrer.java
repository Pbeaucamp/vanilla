package bpm.vanilla.platform.core.cluster;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.impl.StartStopServlet;
import bpm.vanilla.platform.core.components.impl.VanillaHttpContext;
import bpm.vanilla.platform.core.components.impl.VanillaServiceTracker;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaSessionExpiredException;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

/**
 * this class is a simple Wrapper for a IVanillaComponentListenerService. It
 * supposed to be used only by VanillaClusters
 * 
 * ere, modified to add tracker support and start/Stop actions (including
 * creating a start/stop servlet)
 * 
 * The fact that it is a wrapper allow to re-init the password/user from the
 * real used remote API to avoid timeout errors
 * 
 * @author ludo
 * 
 */
public class ClusterRegistrer implements IVanillaComponentListenerService {

	private RemoteVanillaPlatform api;
	private VanillaConfiguration config;

	private HashMap<IVanillaComponentIdentifier, IVanillaComponent> components = new HashMap<IVanillaComponentIdentifier, IVanillaComponent>();

	private HttpService httpService;

	private VanillaServiceTracker tracker;

	public void bind(HttpService service) {
		this.httpService = service;
		Logger.getLogger(getClass()).info("Binded HttpService");
	}

	public void unbind(HttpService service) {
		this.httpService = null;
		Logger.getLogger(getClass()).info("Unbinded HttpService");
	}

	private IVanillaComponentListenerService getComponent() {
		if (config == null) {
			config = ConfigurationManager.getInstance().getVanillaConfiguration();
		}

		if (api == null) {
			api = new RemoteVanillaPlatform(config.getVanillaServerUrl(), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		}

		return api.getListenerService();
	}

	@Override
	public void fireEvent(IVanillaEvent event) throws Exception {
		try {
			getComponent().fireEvent(event);
		} catch (VanillaSessionExpiredException ex) {
			api = null;
			Logger.getLogger(getClass()).warn("Session expired, try to refresh it");
			getComponent().fireEvent(event);
		}
	}

	@Override
	public List<IVanillaComponentIdentifier> getComponents() throws Exception {

		return getComponent().getComponents();
	}

	@Override
	public List<IVanillaComponentIdentifier> getRegisteredComponents(String componentTypeName, boolean includeStoppedComponent) throws Exception {

		try {
			return getComponent().getRegisteredComponents(componentTypeName, includeStoppedComponent);
		} catch (VanillaSessionExpiredException ex) {
			api = null;
			Logger.getLogger(getClass()).warn("Session expired, try to refresh it");
			return getComponent().getRegisteredComponents(componentTypeName, includeStoppedComponent);
		}

	}

	@Override
	public void registerVanillaComponent(IVanillaComponentIdentifier componentIdentifier) throws Exception {

		try {
			getComponent().registerVanillaComponent(componentIdentifier);
		} catch (VanillaSessionExpiredException ex) {
			api = null;
			Logger.getLogger(getClass()).warn("Session expired, try to refresh it");
			getComponent().registerVanillaComponent(componentIdentifier);
		}

	}

	@Override
	public void updateVanillaComponent(IVanillaComponentIdentifier componentIdentifier) throws Exception {

		try {
			getComponent().updateVanillaComponent(componentIdentifier);
		} catch (VanillaSessionExpiredException ex) {
			api = null;
			Logger.getLogger(getClass()).warn("Session expired, try to refresh it");
			getComponent().registerVanillaComponent(componentIdentifier);
		}
	}

	@Override
	public void unregisterVanillaComponent(IVanillaComponentIdentifier componentIdentifier) throws Exception {

		try {
			getComponent().unregisterVanillaComponent(componentIdentifier);
		} catch (VanillaSessionExpiredException ex) {
			api = null;
			Logger.getLogger(getClass()).warn("Session expired, try to refresh it");
			getComponent().unregisterVanillaComponent(componentIdentifier);
		}

	}

	int regDelay;
	
	public void activate(ComponentContext ctx) throws Exception {

		// tracker stuff
		tracker = new VanillaServiceTracker(ctx.getBundleContext(), this);
		tracker.open();
		
		String delay = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanilla.cluster.registration.delay");
		regDelay = 1000000;
		try {
			regDelay = Integer.parseInt(delay);
		} catch(Exception e) {}
		

		
		//XXX A thread to register components again (if the master has been restarted.
		Thread registrationThread = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						sleep(regDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					System.out.println("Check registred components");
					
					try {
						
						for(IVanillaComponentIdentifier comp : components.keySet()) {
							System.out.println("register : " + comp.getComponentId());
							registerVanillaComponent(comp);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		registrationThread.start();
		

		Logger.getLogger(getClass()).info("VanillaClusterRegistrer started");

		// register servlet
		registerServlet();
	}

	private void registerServlet() throws Exception {
		Properties p = null;
		if (System.getProperty("bpm.vanilla.configurationFile") != null) {
			Logger.getLogger(getClass()).info("Loading configuration with bpm.vanilla.configurationFile=" + System.getProperty("bpm.vanilla.configurationFile"));
			try {
				FileInputStream fis = new FileInputStream(System.getProperty("bpm.vanilla.configurationFile"));
				p = new Properties();
				p.load(fis);
				Logger.getLogger(getClass()).info("Configuration loaded");
			} catch (Exception ex) {
				Logger.getLogger(getClass()).warn("Loading configuration failed " + ex.getMessage(), ex);
				p = null;
			}
		}

		if (p == null) {
			Logger.getLogger(getClass()).error("No configuration file found");
			throw new Exception("bpm.vanilla.configurationFile not specified or the file does not exist");
		}

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();

		IVanillaAPI api = new RemoteVanillaPlatform(config.getVanillaServerUrl(), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));

		VanillaHttpContext httpContext = new VanillaHttpContext(api.getVanillaSecurityManager(), api.getVanillaSystemManager());

		try {
			Logger.getLogger(getClass()).info("Registering AdminServlet for component " + this.getClass().getName());
			// httpService.
			httpService.registerServlet(IVanillaComponent.SERVICE_ADMIN_SERVLET, new StartStopServlet(this), null, httpContext);
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Unable to register " + IVanillaComponent.SERVICE_ADMIN_SERVLET + " alias - " + e.getMessage());
			throw e;
		}
	}

	public void desactivate(ComponentContext ctx) throws Exception {
		tracker.close();

		Logger.getLogger(getClass()).info("VanillaListenerService stopped");
	}

	/*
	 * We re doing those locally (not thru the remote)
	 */
	@Override
	public void startComponent(String componentNature) throws Exception {

		synchronized (components) {
			for (IVanillaComponentIdentifier ident : components.keySet()) {
				if (ident.getComponentNature().equals(componentNature)) {
					IVanillaComponent c = components.get(ident);
					c.start();
					return;
				}
			}
		}
		Logger.getLogger(getClass()).warn("Start: could not found component with nature " + componentNature);

	}

	/*
	 * We re doing those locally (not thru the remote)
	 */
	@Override
	public void stopComponent(String componentNature) throws Exception {

		synchronized (components) {
			for (IVanillaComponentIdentifier ident : components.keySet()) {
				if (ident.getComponentNature().equals(componentNature)) {
					IVanillaComponent c = components.get(ident);
					c.stop();
					return;
				}
			}
		}
		Logger.getLogger(getClass()).warn("Stop: could not found component with nature " + componentNature);
	}

	/*
	 * We re doing those locally (not thru the remote)
	 */
	@Override
	public void addTrackedComponent(IVanillaComponent c) {
		synchronized (components) {
			for (IVanillaComponentIdentifier ident : components.keySet()) {
				if (ident.getComponentNature().equals(c.getIdentifier().getComponentNature())) {
					components.put(ident, c);
					return;
				}
			}

			components.put(c.getIdentifier(), c);
		}
	}
}
