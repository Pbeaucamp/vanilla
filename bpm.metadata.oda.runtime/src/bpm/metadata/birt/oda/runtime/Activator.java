package bpm.metadata.birt.oda.runtime;



import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import bpm.metadata.birt.oda.runtime.impl.ConnectionManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.metadata.birt.oda.runtime";

	// The shared instance
	private static Activator plugin;
	

	private static Logger logger;
	/**
	 * The constructor
	 */
	public Activator() {
	}

	public static Logger getLogger(){
		return logger;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.logger = Logger.getLogger(context.getBundle().getSymbolicName());
		super.start(context);
		plugin = this;
		
		
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		ConnectionManager.closeAll();
		ConnectionPool.clear();
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

}
