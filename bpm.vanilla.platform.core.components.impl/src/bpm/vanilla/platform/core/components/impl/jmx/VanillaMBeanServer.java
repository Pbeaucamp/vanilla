package bpm.vanilla.platform.core.components.impl.jmx;

import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.config.ConfigurationException;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

/**
 * This is the class providing a secured mbean server
 * for vanilla specific mbeans.
 * 
 * We need to provide 3 methods to vanilla :
 * - register
 * - isRegisterd
 * - unregister
 * 
 * @author manu
 *
 */
public class VanillaMBeanServer {
	
	//start exposed constants
	public static String VANILLA_JMX_USE 		= "bpm.vanilla.jmx.use";
	public static String VANILLA_JMX_HOST 		= "bpm.vanilla.jmx.host";
	public static String VANILLA_JMX_PORT 		= "bpm.vanilla.jmx.port";
	public static String VANILLA_JMX_RMI_USE 	= "bpm.vanilla.jmx.rmi.security.use";
	public static String VANILLA_JMX_RMI_CONFIG = "bpm.vanilla.jmx.rmi.security.config";

	//end exposed constants

	//start static part
	private static VanillaMBeanServer instance = null;
	
	/**
	 * sync'd to protect
	 * @return
	 * @throws IOException if there is failure to start the server 
	 */
	public static synchronized VanillaMBeanServer getInstance() throws IOException {
		if (instance == null) {
			instance = new VanillaMBeanServer();
		}
		
		return instance;
	}
	//end static part
	
	private MBeanServer mbs = null;
	
	private IVanillaCoreMBean coreMBean;
	
	private boolean useJmx = false;
	
	private String jmxHost;
	private String jmxPort;
	
	private boolean jmxRMIUse = false;
	private String jmxRMIConfigFolder;
	
	/**
	 * Creates and starts the secured MBeanServer
	 * @throws IOException 
	 */
	private VanillaMBeanServer() throws IOException {

		loadProperties();
		
		if (!useJmx) {
			//do no use JMX
			return;
		}
		
		mbs = MBeanServerFactory.createMBeanServer(); 
	    
		//create connector according to security method
		if (jmxRMIUse) {
			createRmiSecuredConnector(mbs);
		}
		else {
			createUnsecuredConnector(mbs);
		}
	}
	
	/**
	 * Load the following props 
	 * 
	 * bpm.vanilla.jmx.use
	 * bpm.vanilla.jmx.host=localhost
	 * bpm.vanilla.jmx.port=9999
	 * bpm.vanilla.jmx.rmi.security.use=true
	 * bpm.vanilla.jmx.rmi.security.configFolder=vanilla-conf/jmx/
	 * 
	 * @return
	 */
	private void loadProperties() throws ConfigurationException {
		
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		try {
			String useJmxString = config.getProperty(VANILLA_JMX_USE);
			useJmx = Boolean.parseBoolean(useJmxString);
		} catch (Exception e) {
			Logger.getLogger(VanillaMBeanServer.class)
				.error("Could not read property '" + VANILLA_JMX_USE + "', vanilla JMX will be disabled");
			useJmx = false;
		}
		if (!useJmx) {
			Logger.getLogger(VanillaMBeanServer.class)
				.info("Property '" + VANILLA_JMX_USE + "' was set to false, vanilla JMX will be disabled");
			return;
		}
		
		jmxHost = config.getProperty(VANILLA_JMX_HOST);
		if (jmxHost == null || jmxHost.isEmpty()) {
			Logger.getLogger(VanillaMBeanServer.class)
				.error("Could not read property '" + VANILLA_JMX_HOST + "', vanilla JMX will be disabled");
			useJmx = false;
			return;
		}
		Logger.getLogger(VanillaMBeanServer.class)
			.info("Found property '" + VANILLA_JMX_HOST + "', value=" + jmxHost);
		
		
		jmxPort = config.getProperty(VANILLA_JMX_PORT);
		if (jmxPort == null || jmxPort.isEmpty()) {
			Logger.getLogger(VanillaMBeanServer.class)
				.error("Could not read property '" + VANILLA_JMX_PORT + "', vanilla JMX will be disabled");
			useJmx = false;
			return;
		}
		Logger.getLogger(VanillaMBeanServer.class)
			.info("Found property '" + VANILLA_JMX_PORT + "', value=" + jmxPort);
		
		
		try {
			String useRMIJmxString = config.getProperty(VANILLA_JMX_RMI_USE);
			jmxRMIUse = Boolean.parseBoolean(useRMIJmxString);
		} catch (Exception e) {
			Logger.getLogger(VanillaMBeanServer.class)
				.error("Could not read property '" + VANILLA_JMX_RMI_USE + "', using default of false");
			jmxRMIUse = false;
		}
		Logger.getLogger(VanillaMBeanServer.class)
			.info("Found property '" + VANILLA_JMX_RMI_USE + "', value=" + jmxRMIUse);
		
		
		jmxRMIConfigFolder = config.getProperty(VANILLA_JMX_RMI_CONFIG);
		if (jmxRMIConfigFolder == null || jmxRMIConfigFolder.isEmpty()) {
			Logger.getLogger(VanillaMBeanServer.class)
				.error("Could not read property '" + VANILLA_JMX_RMI_CONFIG + "', vanilla RMI JMX will be disabled");
			jmxRMIUse = false;
			return;
		}
		else {
			File f = new File(jmxRMIConfigFolder);
			if (!f.exists()) {
				Logger.getLogger(VanillaMBeanServer.class).error("Invalid property '" + VANILLA_JMX_RMI_CONFIG + "', folder doesnt exist., vanilla RMI JMX will be disabled");
				jmxRMIUse = false;				
			}
			//replace with absolute path
			jmxRMIConfigFolder = f.getAbsolutePath();
			Logger.getLogger(VanillaMBeanServer.class)
				.info("Found property '" + VANILLA_JMX_RMI_CONFIG + "', value=" + jmxRMIConfigFolder);
		}
	}
	
	/**
	 * Dont access class members
	 * 
	 * Creates a unsecured connector for the bean server using the following props :
	 * bpm.vanilla.jmx.host=localhost
	 * bpm.vanilla.jmx.port=9999
	 * 
	 * @param beanServer
	 * @throws IOException 
	 */
	private void createUnsecuredConnector(MBeanServer beanServer) throws IOException {
		

		String service = "service:jmx:rmi:///jndi/rmi://" + jmxHost + ":" + jmxPort + "/vanilla";
		
		Logger.getLogger(VanillaMBeanServer.class).info("Creating rmi registry on port : " + jmxPort);
		
		LocateRegistry.createRegistry(Integer.parseInt(jmxPort));
		
		Logger.getLogger(VanillaMBeanServer.class).info("Creating jmx connector with url : " + service + "");
		
        JMXServiceURL url = new JMXServiceURL(service);

        JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);

        cs.start();
	}
	
	public boolean useJmx() {
		return useJmx;
	}
	
	/**
	 * Dont access class members
	 * 
	 * Creates a secured RMI connector for the beanServer using the following props :
	 * 
	 * bpm.vanilla.jmx.host=localhost
	 * bpm.vanilla.jmx.port=9999
	 * bpm.vanilla.jmx.rmi.security.use=true
	 * bpm.vanilla.jmx.rmi.security.configFolder=vanilla-conf/jmx/
	 * 
	 * @param beanServer
	 * @throws IOException
	 */
	private void createRmiSecuredConnector(MBeanServer beanServer) throws IOException {
		Logger.getLogger(VanillaMBeanServer.class).debug("Creating secured RMI Connector using provided SSL info :");
		String service = "service:jmx:rmi:///jndi/rmi://" + jmxHost + ":" + jmxPort + "/vanilla";
		
		//doc, this is for the all mbean server :
		//-Djavax.net.ssl.keyStore=${PWD}/vanilla-conf/jmx/keystore
		//-Djavax.net.ssl.keyStorePassword=password
		
		String keyStore = System.getProperty("javax.net.ssl.keyStore");
		String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
		
		Logger.getLogger(VanillaMBeanServer.class).debug("Using keyStore='" + keyStore + 
				"' and keyStorePassword='" + keyStorePassword);
		
		//documentation, to run a secured rmi registry properly, you'll need to have set : 
		//-Djavax.net.ssl.trustStore=config/truststore
		//-Djavax.net.ssl.trustStorePassword=trustword
		//otherwise, the RMI reg will not accept the certs
		
		String trustStore = System.getProperty("javax.net.ssl.trustStore");
		String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
		
		Logger.getLogger(VanillaMBeanServer.class).debug("Using trustStore='" + trustStore + 
				"' and trustStorePassword='" + trustStorePassword);
	    

//		if (keyStore == null || 
//				keyStorePassword == null || 
//				trustStore == null ||
//				trustStorePassword == null) {
//			throw new RuntimeException("Invalid SSL configuration for secured RMI/JMX Connector.");
//		}
		
		Logger.getLogger(VanillaMBeanServer.class).info("Creating rmi registry on port : " + jmxPort);
	    LocateRegistry.createRegistry(Integer.parseInt(jmxPort));
	    //LocateRegistry.createRegistry(Integer.parseInt(jmxPort), csf, ssf);
	    
		
		HashMap<String, Object> env = new HashMap<String, Object>(); 
		
	    SslRMIClientSocketFactory csf = new SslRMIClientSocketFactory(); 
	    SslRMIServerSocketFactory ssf = new SslRMIServerSocketFactory(); 
	    env.put(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE, csf); 
	    env.put(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE, ssf); 
	    
	    Logger.getLogger(VanillaMBeanServer.class).info("Creating jmx connector with url : " + service + "");
	    env.put("jmx.remote.x.password.file", jmxRMIConfigFolder + File.separator + "password.properties"); 
	    env.put("jmx.remote.x.access.file", jmxRMIConfigFolder + File.separator + "access.properties"); 
	
	    JMXServiceURL url = new JMXServiceURL(service);
	    
	    JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, env, beanServer);
	    
	    cs.start(); 
	}
	
	public boolean isRegistered(ObjectName name) {
		if (!useJmx) {
			Logger.getLogger(VanillaMBeanServer.class).info("Ignoring JMX query, VanillaJMX is disabled");
			return false;
		}
		return mbs.isRegistered(name);
	}
	
	/**
	 * for regular vanilla components
	 * 
	 * @param object
	 * @param name
	 * @return
	 * @throws InstanceAlreadyExistsException
	 * @throws MBeanRegistrationException
	 * @throws NotCompliantMBeanException
	 */
	public ObjectInstance registerMBean(IVanillaComponentMBean object, ObjectName name) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		if (!useJmx) {
			Logger.getLogger(VanillaMBeanServer.class).info("Ignoring JMX query, VanillaJMX is disabled");
			return null;
		}
		
		coreMBean.registerBean(object);
		
		return mbs.registerMBean(object, name);
	}
	
	/**
	 * Register the core bean
	 * 
	 * @param object
	 * @param name
	 * @return
	 * @throws InstanceAlreadyExistsException
	 * @throws MBeanRegistrationException
	 * @throws NotCompliantMBeanException
	 */
	public ObjectInstance registerMBean(IVanillaCoreMBean object, ObjectName name) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		if (!useJmx) {
			Logger.getLogger(VanillaMBeanServer.class).info("Ignoring JMX query, VanillaJMX is disabled");
			return null;
		}
		
		coreMBean = object;
		
		return mbs.registerMBean(object, name);
	}
	
	public void unregisterMBean(ObjectName name) throws MBeanRegistrationException, InstanceNotFoundException {
		if (!useJmx) {
			Logger.getLogger(VanillaMBeanServer.class).info("Ignoring JMX query, VanillaJMX is disabled");
			return;
		}
		mbs.unregisterMBean(name);
	}
}
