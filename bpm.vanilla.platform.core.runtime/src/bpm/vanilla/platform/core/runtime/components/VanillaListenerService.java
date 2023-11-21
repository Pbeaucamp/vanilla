package bpm.vanilla.platform.core.runtime.components;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;
import bpm.vanilla.platform.core.components.impl.StartStopServlet;
import bpm.vanilla.platform.core.components.impl.VanillaHttpContext;
import bpm.vanilla.platform.core.components.impl.VanillaServiceTracker;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.listeners.IVanillaListener;
import bpm.vanilla.platform.core.listeners.event.impl.ReportExecutedEvent;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.runtime.components.listener.AlertListener;
import bpm.vanilla.platform.core.runtime.components.listener.VanillaComponentListener;
import bpm.vanilla.platform.core.runtime.components.listener.internal.EventManager;

public class VanillaListenerService implements IVanillaComponentListenerService{
	private ComponentContext componentCtx;
	
	private List<ServiceRegistration> registrations = new ArrayList<ServiceRegistration>();
	
	private EventManager eventManager;
	
	private HashMap<IVanillaComponentIdentifier, IVanillaComponent> components = new HashMap<IVanillaComponentIdentifier, IVanillaComponent>();
	
	private VanillaServiceTracker tracker;
	
	private HttpService httpService;
	
	public void registerListener(IVanillaListener listener){
		try{
			StringBuilder b = new StringBuilder();
			
			for(String s: listener.getListenedEventTypes()){
				b.append(s + ";");
			}
			String eventName = b.toString();
			if (!eventName.isEmpty()){
				eventName =eventName.substring(0,eventName.length() - 1);
			}
			
			Dictionary<String, String> dico = new Hashtable<String, String>();
			dico.put(IVanillaListener.FILTER_EVENT_NAME,eventName);
			
			ServiceRegistration reg = componentCtx.getBundleContext().registerService(IVanillaListener.class.getName(), listener, dico);
			registrations.add(reg);
			Logger.getLogger(getClass()).info("Listener " + listener.getClass() + " registered for " + dico.toString() );

		}catch(Throwable ex){
			Logger.getLogger(getClass()).error("Listener registration failed - " + ex.getMessage(), ex);
		}
	}
		
	/**
	 * ere, modified : 
	 * Check if the service identified by the identifier is already present and downed.
	 * if yes
	 * 	- delete old service
	 * 
	 * after : register service
	 * 
	 */
	@Override
	public void registerVanillaComponent(IVanillaComponentIdentifier componentIdentifier) {
		
		try {
			//as description above, try to find an existing service with same id, if there, delete
			synchronized (registrations) {
				for(ServiceRegistration sr : registrations) {
					
					if (sr.getReference() != null &&
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE) != null &&
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE).equals(componentIdentifier.getComponentNature()) &&
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_ID).equals(componentIdentifier.getComponentId()) &&
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_PORT).equals(componentIdentifier.getComponentPort())&& 
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_IP).equals(componentIdentifier.getComponentIp())){
						
						//componentIdentifier.setComponentStatus(status)
						sr.setProperties(componentIdentifier.getProperties());
						
						IVanillaComponentIdentifier ident = (IVanillaComponentIdentifier) componentCtx.getBundleContext().getService(sr.getReference());
						ident.setComponentStatus(componentIdentifier.getComponentStatus());
						//sr.
						Logger.getLogger(getClass()).info("VanillaComponent updated with " + componentIdentifier.getProperties().toString() + " instead of being registered");
						componentCtx.getBundleContext().ungetService(sr.getReference());
						
						return;
					}
					
					componentCtx.getBundleContext().ungetService(sr.getReference());
				}
			}
			//end ere add
			
			ServiceRegistration reg = null;
			
			Dictionary<String, String> dico = new Hashtable<String, String>();
			
			for(Object p : componentIdentifier.getProperties().keySet()) {
				dico.put((String)p, (String)componentIdentifier.getProperties().get(p));
			}
			
			
			reg = componentCtx.getBundleContext().registerService(IVanillaComponentIdentifier.class.getName(), componentIdentifier, dico);
			synchronized (registrations) {
				registrations.add(reg);
			
			}
			
			
			if (componentIdentifier.getComponentNature().equals(VanillaComponentType.COMPONENT_WORKFLOW)){
				VanillaComponentListener listener = new VanillaComponentListener(new String[]{
						ReportExecutedEvent.EVENT_REPORT_GENERATED
				}, componentIdentifier);
				registerListener(listener);
			}
			
			Logger.getLogger(getClass()).info("VanillaComponent registered with " + componentIdentifier.getProperties().toString());

		}catch(Throwable t){
			Logger.getLogger(getClass()).warn("Could not register component - " + t.getMessage(), t);
		}
	}
	
	@Override
	public void updateVanillaComponent(IVanillaComponentIdentifier componentIdentifier) throws Exception {
		int updateCount = 0;
		try{
			synchronized (registrations) {
				for(ServiceRegistration sr : registrations){
					if (sr.getReference() != null && 
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE) != null &&
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE).equals(componentIdentifier.getComponentNature()) &&
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_ID).equals(componentIdentifier.getComponentId()) &&
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_PORT).equals(componentIdentifier.getComponentPort())&& 
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_IP).equals(componentIdentifier.getComponentIp())){
						//have to update this one.
						
						sr.setProperties(componentIdentifier.getProperties());
						
						IVanillaComponentIdentifier ident = (IVanillaComponentIdentifier) componentCtx.getBundleContext().getService(sr.getReference());
						ident.setComponentStatus(componentIdentifier.getComponentStatus());
						
						updateCount++;
					}
					componentCtx.getBundleContext().ungetService(sr.getReference());
				}	
			}
			Logger.getLogger(getClass()).info("VanillaComponent updated with " + componentIdentifier.getProperties().toString() +
					", updated " + updateCount + " Service(s) Registration");

		}catch(Throwable t){
			Logger.getLogger(getClass()).warn("Could not update component - " + t.getMessage(), t);
		}
	}

	
	/**
	 * 
	 * @param event
	 * @return the registered Listener that can handle to given Event
	 * (it uses IVanillaListener.FILTER_EVENT_NAME as Filter propertyName and the Event.getEventTypeName as value
	 * to find the listeners from the registry)
	 */
	private List<IVanillaListener> getListeners(IVanillaEvent event){
		StringBuilder filter = new StringBuilder();
		filter.append("("+ IVanillaListener.FILTER_EVENT_NAME + "=*" + event.getEventTypeName() + "*)");
		
		List<IVanillaListener> listeners = new ArrayList<IVanillaListener>();
		
		try {
			ServiceReference[] refs = componentCtx.getBundleContext().getServiceReferences(IVanillaListener.class.getName(), filter.toString());
			if (refs == null){
				return new ArrayList<IVanillaListener>();
			}
			for(ServiceReference r : refs){
				IVanillaListener l = ((IVanillaListener)componentCtx.getBundleContext().getService(r));
				if (l != null){
					listeners.add(l);
				}
				
			}
			
			
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		
		return listeners;
	}
	
	@Override
	public void fireEvent(IVanillaEvent event) {
		eventManager.eventFired(event, getListeners(event));
	}
	
 
	@Override
	public void unregisterVanillaComponent(IVanillaComponentIdentifier componentIdentifier) {
		
		try{
			List<ServiceRegistration> toRemove = new ArrayList<ServiceRegistration>();
			for(ServiceRegistration sr : registrations){
				try{
					if (sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE).equals(componentIdentifier.getComponentNature()) &&
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_ID).equals(componentIdentifier.getComponentId()) &&
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_PORT).equals(componentIdentifier.getComponentPort())&& 
							sr.getReference().getProperty(IVanillaComponentIdentifier.P_COMPONENT_IP).equals(componentIdentifier.getComponentIp())){
							toRemove.add(sr);
						}
				}catch(Exception ex){
					//no catch, usually because the ServiceRegistration does not come from a VanillaComponent
				}
			}

			synchronized (registrations) {
				for(ServiceRegistration reg : toRemove){
					try{componentCtx.getBundleContext().ungetService(reg.getReference());
						registrations.remove(reg);
						reg.unregister();
						Logger.getLogger(getClass()).info("Released VanillaListener " + componentIdentifier.getProperties().toString());

						
					}catch(Throwable t){
						Logger.getLogger(getClass()).warn("failed to release Component - " + t.getMessage(), t);
					}
				}
				
			}

		}catch(Throwable ex){
			Logger.getLogger(getClass()).error("Unable to release listeners - " + ex.getMessage(), ex);
		}
		
		
	}
	
	
	public void bind(HttpService service){
		this.httpService = service;
		
		Logger.getLogger(getClass()).info("Binded HttpService");
		
	}
	
	public void unbind(HttpService service){
		
		this.httpService = null;
		
		Logger.getLogger(getClass()).info("Unbinded HttpService");
		
	}
	
	public void activated(ComponentContext ctx) throws Exception{
		
		//tracker stuff
		tracker = new VanillaServiceTracker(ctx.getBundleContext(), this);
		tracker.open();

		this.componentCtx = ctx;
		
		if (ConfigurationManager.getInstance().getVanillaConfiguration().isAlertEnabled()){
			registerListener(new AlertListener());
		}
		
		eventManager = new EventManager(50);
		Logger.getLogger(getClass()).info("VanillaListenerService started");
		
		//register servlet
		registerServlet();
	}
	
	private void registerServlet() throws Exception {
		Properties p = null;
		if (System.getProperty("bpm.vanilla.configurationFile") != null){
			Logger.getLogger(getClass()).info("Loading configuration with bpm.vanilla.configurationFile=" + System.getProperty("bpm.vanilla.configurationFile"));
    		try{
    			FileInputStream fis = new FileInputStream(System.getProperty("bpm.vanilla.configurationFile"));
    			p = new Properties();
    			p.load(fis);
    			Logger.getLogger(getClass()).info("Configuration loaded");
    		}catch(Exception ex){
    			Logger.getLogger(getClass()).warn("Loading configuration failed " + ex.getMessage(), ex);
    			p = null;
    		}
    	}
		
		if (p == null){
			Logger.getLogger(getClass()).error("No configuration file found");
			throw new Exception("bpm.vanilla.configurationFile not specified or the file does not exist");
		}
		
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		IVanillaAPI api = new RemoteVanillaPlatform(config.getVanillaServerUrl(),
				config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN),
				config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));		
		
		VanillaHttpContext httpContext = new VanillaHttpContext(api.getVanillaSecurityManager(), api.getVanillaSystemManager());
		
		try{
			Logger.getLogger(getClass()).info("Registering AdminServlet for component " + this.getClass().getName());
			httpService.registerServlet(IVanillaComponent.SERVICE_ADMIN_SERVLET, 
					new StartStopServlet(this), null, httpContext);
		}catch (Exception e) {
			Logger.getLogger(getClass()).error("Unable to register " + IVanillaComponent.SERVICE_ADMIN_SERVLET 
					+ " alias - " + e.getMessage());
			throw e;
		}
	}
	
	public void desactivated(ComponentContext ctx) throws Exception{
		tracker.close();

		this.componentCtx = null;
		Logger.getLogger(getClass()).info("Shutting down EventManager...");
		eventManager.shutdownAndAwaitTermination();
		Logger.getLogger(getClass()).info("EventManager stopped");
		eventManager = null;
		
		Logger.getLogger(getClass()).info("VanillaListenerService stopped");
	}


	@Override
	public List<IVanillaComponentIdentifier> getRegisteredComponents(String componentNature, boolean includeStoppedComponent) throws Exception {
		StringBuilder filter = new StringBuilder();
		if(includeStoppedComponent) {
			filter.append("(&("+ IVanillaComponentIdentifier.P_COMPONENT_NATURE + "=" +componentNature + "))");
		}
		else {
			filter.append("(&("+ IVanillaComponentIdentifier.P_COMPONENT_NATURE + "=" +componentNature + ")(" + IVanillaComponentIdentifier.P_COMPONENT_STATUS+"=" +Status.STARTED.getStatus() +"))");
		}
		
		List<IVanillaComponentIdentifier> l = new ArrayList<IVanillaComponentIdentifier>();
		ServiceReference[] refs = componentCtx.getBundleContext().getAllServiceReferences(IVanillaComponentIdentifier.class.getName(), filter.toString());
		if (refs != null){
			for(ServiceReference ref : refs){
				try{
					l.add((IVanillaComponentIdentifier)componentCtx.getBundleContext().getService(ref));
					componentCtx.getBundleContext().ungetService(ref);
				}catch(Throwable t){
					Logger.getLogger(getClass()).warn("Could not get IVanillaComponentIdentifier from ServiceReference - " + t.getMessage(), t);
				}
			}
		}
		return l;
	}
	
	public List<IVanillaComponentIdentifier> getComponents() throws Exception {
		List<IVanillaComponentIdentifier> cs;
		
		synchronized (components) {
			cs = new ArrayList<IVanillaComponentIdentifier>(components.keySet());
		}
		return cs;
	}
	
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
	
	public void addTrackedComponent(IVanillaComponent c) {
		synchronized (components) {
			if (c == null){
				Logger.getLogger(getClass()).error("TRACKIN NULL COMPONENT????");
				return;
			}

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
