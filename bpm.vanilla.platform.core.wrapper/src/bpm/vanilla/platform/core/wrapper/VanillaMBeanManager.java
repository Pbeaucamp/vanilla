package bpm.vanilla.platform.core.wrapper;

import java.io.IOException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.components.impl.AbstractVanillaComponent;
import bpm.vanilla.platform.core.components.impl.jmx.VanillaCoreMBean;
import bpm.vanilla.platform.core.components.impl.jmx.VanillaMBeanServer;

/**
 * just a helper class to register the main vanilla bean
 * 
 * @author manu
 *
 */
public class VanillaMBeanManager {

	private static Logger logger = Logger.getLogger(VanillaMBeanManager.class);
	
	/**
	 * this one comes straight from {@link AbstractVanillaComponent}
	 * but the core cannot implement it, so copying it now
	 * should we make an abstractjmx?
	 * 
	 * @param vanillaCore
	 */
	public static void createMBean(VanillaCoreWrapper vanillaCore) {
		String objectName = "";
		ObjectName name = null;
		VanillaCoreMBean bean;
		VanillaMBeanServer mbs = null;
		try {
			logger.info("Registering VanillaCoreMBean...");
			
			//get platform's one
			mbs = VanillaMBeanServer.getInstance();
			
			if (!mbs.useJmx()) {
				logger.warn("Ignoring jmx registration attenpt, VanillaJMX is disabled");
				return;
			}
			
			logger.info("Creating object name.");
			// Construct the ObjectName for the MBean we will register
			
			objectName = "bpm.vanilla.platform.core.wrapper:" +
					"type=VanillaCoreMBean" +
					",name=" + "VanillaCore";
			
			name = new ObjectName(objectName);
			
			bean = new VanillaCoreMBean(vanillaCore);
			
			if (mbs.isRegistered(name)) {
				mbs.unregisterMBean(name);
			}
			
			logger.info("Registering...");
			ObjectInstance instance = mbs.registerMBean(bean, name);
			
			logger.info("Registered MBean with name " + instance.getObjectName().getCanonicalName());
		} catch (NullPointerException e) {
			logger.error("Failed to register JMX Bean : " + e.getMessage(), e);
		} catch (MalformedObjectNameException e) {
			logger.error("Failed to register JMX Bean : " + e.getMessage(), e);
		} catch (InstanceAlreadyExistsException e) {
			logger.error("Instance of JMX Bean already exist for name (name='" + objectName + "'): " + e.getMessage(), e);
		} catch (MBeanRegistrationException e) {
			logger.error("Failed to register JMX Bean : " + e.getMessage(), e);
		} catch (NotCompliantMBeanException e) {
			logger.error("Failed to register JMX Bean : " + e.getMessage(), e);
		} catch (InstanceNotFoundException e) {
			logger.error("Failed to un-register JMX Bean : " + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("Failed to register JMX Bean : " + e.getMessage(), e);
		}
	}
	
}
