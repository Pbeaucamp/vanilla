package bpm.vanilla.platform.core.components.impl;

import java.io.IOException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.impl.jmx.VanillaComponentMBean;
import bpm.vanilla.platform.core.components.impl.jmx.VanillaMBeanServer;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.VanillaComponentIdentifier;

/**
 * abstract class to share the code for Registration within Vanilla ere, added
 * jmx:dynamicMbean creations, need to be called by the implementors, update:
 * actually called in registerInVanilla()
 * 
 * implementors of this class should request a dependancy on IVanillaListener
 * 
 * 
 * @author ludo
 * 
 */
public abstract class AbstractVanillaComponent implements IVanillaComponent {
	private IVanillaComponentIdentifier identifier;
	private boolean registered = false;

	private IVanillaComponentListenerService listenerServiceComponent;

	public void bind(IVanillaComponentListenerService service) {
		this.listenerServiceComponent = service;
		Logger.getLogger(getClass()).info("IVanillaComponentListenerService binded");
	}

	public void unbind(IVanillaComponentListenerService service) {
		this.listenerServiceComponent = null;
		Logger.getLogger(getClass()).info("IVanillaComponentListenerService unbinded");
	}

	protected IVanillaComponentListenerService getVanillaListenerComponent() {
		return listenerServiceComponent;
	}

	/**
	 * Creates a dynamic JMX MBean associated with this object. Will ignore
	 * errors (might not be fatal)
	 * 
	 * The name is unique :
	 * bpm.vanilla.platform.core.components.impl.jmx:type=VanillaComponentMBean,name={COMPONENT_NATURE}
	 * 
	 * If components of the same nature are to be registered in the same jmx
	 * server, an extra attribute will be needed
	 * 
	 * note : automatically replaces existing bean with same name ere
	 */
	protected void createDynamicMBean() {
		String objectName = "";
		ObjectName name = null;
		VanillaComponentMBean bean = null;
		VanillaMBeanServer mbs = null;
		try {
			Logger.getLogger(getClass()).info("Registering VanillaMBean...");

			// get platform's one
			mbs = VanillaMBeanServer.getInstance();

			if (!mbs.useJmx()) {
				Logger.getLogger(getClass()).debug("Ignoring jmx registration attenpt, VanillaJMX is disabled");
				return;
			}

			Logger.getLogger(getClass()).info("Creating object name.");
			// Construct the ObjectName for the MBean we will register

			objectName = "bpm.vanilla.platform.core.components.impl.jmx:" + "type=VanillaComponentMBean" + ",name=" + this.getIdentifier().getComponentNature();

			name = new ObjectName(objectName);

			bean = new VanillaComponentMBean(this);

			if (mbs.isRegistered(name)) {

				mbs.unregisterMBean(name);
			}

			Logger.getLogger(getClass()).info("Registering...");
			ObjectInstance instance = mbs.registerMBean(bean, name);

			Logger.getLogger(getClass()).info("Registered MBean with name " + instance.getObjectName().getCanonicalName());

		} catch (NullPointerException e) {
			Logger.getLogger(getClass()).error("Failed to register JMX Bean : " + e.getMessage(), e);
		} catch (MalformedObjectNameException e) {
			Logger.getLogger(getClass()).error("Failed to register JMX Bean : " + e.getMessage(), e);
		} catch (InstanceAlreadyExistsException e) {
			Logger.getLogger(getClass()).error("Instance of JMX Bean already exist for name (name='" + objectName + "'): " + e.getMessage(), e);
		} catch (MBeanRegistrationException e) {
			Logger.getLogger(getClass()).error("Failed to register JMX Bean : " + e.getMessage(), e);
		} catch (NotCompliantMBeanException e) {
			Logger.getLogger(getClass()).error("Failed to register JMX Bean : " + e.getMessage(), e);
		} catch (InstanceNotFoundException e) {
			Logger.getLogger(getClass()).error("Failed to un-register JMX Bean : " + e.getMessage(), e);
		} catch (IOException e) {
			Logger.getLogger(getClass()).error("Failed to register JMX Bean : " + e.getMessage(), e);
		}
	}

	/**
	 * public to give acces to Mbeans
	 * 
	 * @throws Exception
	 */
	@Override
	public void stop() throws Exception {
		if (this.getStatus() != Status.STARTED) {
			String err = "Trying to stop a component that is not started.";
			Logger.getLogger(getClass()).error(err);
			throw new Exception(err);
		}

		Logger.getLogger(getClass()).info("Trying to stop component : " + this.getIdentifier().getComponentNature());

		doStop();

	}

	@Override
	public void start() throws Exception {
		if (this.getStatus() != Status.STOPPED) {
			String err = "Trying to start a component that is not stopped. (status='" + this.getStatus() + "').";
			Logger.getLogger(getClass()).error(err);
			throw new Exception(err);
		}

		Logger.getLogger(getClass()).info("Trying to start component : " + this.getIdentifier().getComponentNature());

		doStart();
	}

	/**
	 * Abstract method to be implemented by implementors to actually stop their
	 * operations
	 * 
	 * @throws Exception
	 * 
	 */
	protected abstract void doStop() throws Exception;

	/**
	 * Abstract method to be implemented by implementors to actually start their
	 * operations
	 * 
	 * @throws Exception
	 * 
	 */
	protected abstract void doStart() throws Exception;

	/**
	 * create a VanillaIdentifier an register within the vanilla Runtime
	 * 
	 * 
	 * 
	 * @param componentNature
	 * @param componentId
	 * @param port
	 *            : should be read from the HttpService properties http.port, if
	 *            it does not exists, it means we are within a regular Java
	 *            AppServer and we find the port from the vanillaConfiguration
	 * 
	 * @throws Exception
	 */
	protected void registerInVanilla(String vanillaComponentType, String componentId, String port) throws Exception {
		String local_protocole = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PROTOCOLE);
		if (local_protocole == null) {
			throw new Exception("Missing property " + VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PROTOCOLE + " in vanillaConfiguration file");
		}

		String local_ip = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_IP);
		if (local_ip == null) {
			throw new Exception("Missing property " + VanillaConfiguration.P_VANILLA_LOCAL_SERVER_IP + " in vanillaConfiguration file");
		}

		if (port == null || port.isEmpty()) {
			port = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PORT);
			if (port == null) {
				throw new Exception("Missing property " + VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PORT + " in vanillaConfiguration file");
			}
		}

		String local_context = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_CONTEXT);
		if (local_context == null) {
			throw new Exception("Missing property " + VanillaConfiguration.P_VANILLA_LOCAL_SERVER_CONTEXT + " in vanillaConfiguration file");
		}

		VanillaComponentIdentifier identifier = new VanillaComponentIdentifier(vanillaComponentType, local_protocole, local_ip, port, local_context, componentId, Status.STARTED.getStatus());
		this.identifier = identifier;

		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();

		try {
			Logger.getLogger(getClass()).info("Trying to register in the Vanilla Platform at : " + vanillaUrl);

			getVanillaListenerComponent().registerVanillaComponent(identifier);
			registered = true;
			Logger.getLogger(getClass()).info("Registered in the Vanilla Platform at : " + vanillaUrl);

			// when we re registered we go to mbean registration
			createDynamicMBean();
		} catch (Exception ex) {
			registered = false;
			Logger.getLogger(getClass()).warn("Cannot Remotely Register on VanillaPlatform " + ex.getMessage());
			throw new Exception("Could not register component within the VanillaPlatform - " + ex.getMessage() + ",Target registration = " + vanillaUrl + "\ncomponentiD=" + identifier.getComponentId(), ex);
		}
	}

	/**
	 * goes and updates the component status in vnailla
	 * 
	 * @param identifier
	 * @throws Exception
	 */
	protected void updateInVanilla(IVanillaComponentIdentifier identifier) throws Exception {
		Logger.getLogger(getClass().getName()).info("Trying to unregister component with id : " + identifier.getComponentId());
		if (!isRegistered()) {
			Logger.getLogger(getClass().getName()).info("Component is not registered, will ignore update.");
			return;
		}
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = conf.getVanillaServerUrl();

		try {
			getVanillaListenerComponent().updateVanillaComponent(identifier);
			registered = false;
			Logger.getLogger(getClass().getName()).info("Updated in the Vanilla Platform at : " + vanillaUrl);
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).error("Update in the Vanilla Platform at " + vanillaUrl + " failed - " + ex.getMessage(), ex);
		}
	}

	protected void unregisterFromVanilla(IVanillaComponentIdentifier identifier) throws Exception {
		Logger.getLogger(getClass().getName()).info("Trying to unregister component with id : " + identifier.getComponentId());

		if (!isRegistered()) {
			Logger.getLogger(getClass().getName()).info("Component is not registered, will ignore unregistraton.");
			return;
		}
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = conf.getVanillaServerUrl();

		try {
			getVanillaListenerComponent().unregisterVanillaComponent(identifier);
			registered = false;
			Logger.getLogger(getClass().getName()).info("Unregistered from the Vanilla Platform at : " + vanillaUrl);
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).error("Unregistration from the Vanilla Platform at " + vanillaUrl + " failed - " + ex.getMessage(), ex);
		}
	}

	protected boolean isRegistered() {
		return registered;
	}

	public IVanillaComponentIdentifier getIdentifier() {
		return identifier;
	}
}
