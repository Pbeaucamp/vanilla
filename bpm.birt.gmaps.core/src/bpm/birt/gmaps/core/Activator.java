package bpm.birt.gmaps.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.birt.gmaps.core";
	private ServiceReference httpServiceRef;
	
	/**
	 * Url of the vanilla server to allow to use use adminbirep-api 
	 */
	private static final String VANILLA_SERVER_URL  = "bpm.vanilla.server.url"; 
	
	
	private String vanillaServerUrl;

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		httpServiceRef = context.getServiceReference( HttpService.class.getName() );
        if( httpServiceRef != null ) {
        	vanillaServerUrl = ((String)httpServiceRef.getProperty(VANILLA_SERVER_URL));
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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

	public String getVanillaServerUrl() {
		return vanillaServerUrl;
	}

}
