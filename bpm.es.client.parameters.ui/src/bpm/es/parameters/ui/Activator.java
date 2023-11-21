package bpm.es.parameters.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.vanilla.platform.core.IRepositoryApi;



/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "bpm.es.parameters.ui"; //$NON-NLS-1$

	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

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

	
	public IRepositoryApi getRepositorySocket(){
		return adminbirep.Activator.getDefault().getRepositoryApi();
	}
	
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put("checked", ImageDescriptor.createFromFile(Activator.class, "icons/checked.png" )); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("unchecked", ImageDescriptor.createFromFile(Activator.class, "icons/unchecked.png" )); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
