package bpm.csv.oda.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {
	public static final String VANILLA_SERVER_URL = "bpm.vanilla.server.url";
	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.csv.oda.runtime";

	// The shared instance
	private static Activator plugin;
	private String vanillaUrl = "";
	
	private Logger logger;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		logger = Logger.getLogger(context.getBundle().getSymbolicName());
		super.start(context);
		plugin = this;
		
    	    
    	if (System.getProperty("bpm.vanilla.configurationFile") != null){
    		logger.info("Loading configuration File = " + System.getProperty("bpm.vanilla.configurationFile"));
    		File f = new File(System.getProperty("bpm.vanilla.configurationFile"));
    		
    		if (!f.exists() ){
    			logger.warn("The file " + System.getProperty("bpm.vanilla.configurationFile") + " does not exist.");
    		}
    		else{
    			findVanillaUrl(f);
    		}
    	}
    	
    	if (vanillaUrl == null){
    		logger.warn("The property " + VANILLA_SERVER_URL + " has not be found");
    		File f = new File(Platform.getInstallLocation().getURL().getPath() + File.separator + "resources"+ File.separator);
    		logger.info("Looking the property in .properties file located at : "+ f.getAbsolutePath());
    		findVanillaUrl(f);    	
    		
    		
    	}
    	
    	if (vanillaUrl == null){
    		throw new Exception("Cannot be started without " + VANILLA_SERVER_URL + " property set within specified bpm.vanilla.configurationFile or [Palteform]/resources/*.properties files");
    	}
    	
    	
    	
	}

	private void findVanillaUrl(File f) {
		if (f.isDirectory()) {
			for (File _f : f.listFiles()) {
				findVanillaUrl(_f);
			}
		}
		else {
			if (f.exists() && f.getAbsolutePath().endsWith("properties")){		
				Properties p = new Properties();
	    		try { 
	    			p.load(new FileInputStream(f)); 
	    			if (p.getProperty(VANILLA_SERVER_URL) != null) {
	    				vanillaUrl = p.getProperty(VANILLA_SERVER_URL);
	    				return;
	    			}
	    		} 
	    		catch (IOException e) { 
	    			e.printStackTrace();
	    		} 
	    	}
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public String getVanillaUrl() {
		return vanillaUrl;
	}


	
}
