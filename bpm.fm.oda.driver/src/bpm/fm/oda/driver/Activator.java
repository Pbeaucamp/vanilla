package bpm.fm.oda.driver;

import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.osgi.framework.BundleContext;

import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.utils.Tools;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.fm.oda.driver";

	// The shared instance
	private static Activator plugin;
	private static IManager fmMgr ;
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
		
		/*
		 * init FM
		 */
//		FactoryManager.init("", Tools.OS_TYPE_WINDOWS);
//		
//		
//		
//		try{
//			fmMgr = FactoryManager.getInstance(/*new Properties(), Platform.getInstallLocation().getURL().getPath() + "/resources/freeMetricsContext.xml"*/).getManager();
//		}catch(Exception ex){
//			ex.printStackTrace();
//			throw new OdaException(new Exception("Error when initializing Freemetrics context : " + ex.getMessage(), ex));
//		}
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

	public IManager getFmManager() {
		
		return fmMgr;
	}

}
