package bpm.vanilla.platform.core.components;

import java.util.Properties;

public interface IVanillaComponentIdentifier {
	public static final String P_COMPONENT_NATURE = "bpm.vanilla.platform.core.components.identifier.nature";
	public static final String P_COMPONENT_PROTOCOLE = "bpm.vanilla.platform.core.componentsidentifier.protocole";
	public static final String P_COMPONENT_IP = "bpm.vanilla.platform.core.componentsidentifier.ip";
	public static final String P_COMPONENT_ID = "bpm.vanilla.platform.core.components.identifier.id";
	
	public static final String P_COMPONENT_PORT = "bpm.vanilla.platform.core.components.identifier.port";
	public static final String P_COMPONENT_CONTEXT_NAME = "bpm.vanilla.platform.core.components.identifier.contextName"; 
	
	public static final String P_COMPONENT_STATUS = "bpm.vanilla.platform.core.components.status";
//	public static final String P_EVENT_SUPPORTED = "bpm.vanilla.platform.core.components.identifier.eventsPropertiesNames";
	
	/**
	 * used to register the IVanillaComponentIdentifier with the VanillaPlatform
	 * @return
	 */
	public Properties getProperties();
	
	/**
	 * 
	 * @return the component Nature, it must be a valid value from {@link VanillaComponentType}  
	 * 
	 */
	public String getComponentNature();
	
	/**
	 * 
	 * @return the protocole of the given component
	 */
	public String getComponentProtocole();
	
	/**
	 * 
	 * @return the Ip of the given component
	 */
	public String getComponentIp();
	
	/**
	 * 
	 * @return the component port Number
	 * If the RUntime environement is in a pure equinox, it should have been inited with the HttpService http.port value
	 * if the runtime is a WebApp server embeding an equinox, the port number come from the VanillaConfiguration file
	 * 
	 */
	public String getComponentPort();
	
	/**
	 * 
	 * @return the component context name, url:port/context_name
	 */
	public String getComponentContextName();
	
	/**
	 * 
	 * @return a symbolic name for the Component
	 */
	public String getComponentId();
	
	
	public String getComponentUrl();
	
	/**
	 * get the status of the component
	 * 
	 * @return
	 */
	public String getComponentStatus();
	
	/**
	 * to update the status
	 */
	public void setComponentStatus(String status);
	 
//	/**
//	 * 
//	 * @return the list of event properties that should notify this component when they occur
//	 * may be empty
//	 */
//	public String[] getComponentSupportedEvent();
	
	
}
