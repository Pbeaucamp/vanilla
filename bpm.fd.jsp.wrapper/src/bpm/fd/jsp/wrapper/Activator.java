package bpm.fd.jsp.wrapper;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;



public class Activator implements BundleActivator {

	public static final String ID = "bpm.fd.jsp.wrapper";
	
	private static BundleContext context;
	
	
	/**
	 * Url of the vanilla server to allow to use use adminbirep-api 
	 */
	private static final String VANILLA_SERVER_URL  = "bpm.vanilla.server.url"; 
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
       
	}
	
	public static BundleContext getContext(){
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
