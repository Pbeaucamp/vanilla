package bpm.metadata.birt.contribution;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.vanilla.platform.core.IRepositoryApi;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.metadata.birt.contribution";

	// The shared instance
	private static Activator plugin;
	
	private Map<String, Integer> fileItemMapping = new HashMap<String, Integer>();
	
	private IRepositoryApi sock;
	
//	public static Logger logger = Logger.getLogger("bpm.metadata.birt.contrib");
//	
//	static {
//		logger.addAppender(new ConsoleAppender());
//	}
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
		//logger.info("Starting bpm.metadata.birt.contrib.");
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		//logger.info("Stopping bpm.metadata.birt.contrib.");
		plugin = null;
		super.stop(context);
	}
	
	public Integer getItemIdByPath(String path) {
		
		path = path.replace("\\", "/");
		
		return fileItemMapping.get(path);
	}
	
	public void addItemPathMapping(String path, Integer itemId) {
		path = path.replace("\\", "/");
		fileItemMapping.put(path, itemId);
	}
	
	public IRepositoryApi getRepositoryApi() {
		return sock;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public void setRepositoryApi(IRepositoryApi sock) {
		this.sock = sock;
	}

}
