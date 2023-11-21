package bpm.database.ui.viewer.relations;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.metadata.ui.viewer.relations";

	// The shared instance
	private static Activator plugin;
	
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
		super.start(context);
		plugin = this;
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

	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);

		
		reg.put("zoomIn", ImageDescriptor.createFromFile(Activator.class, "icons/zoom_in.png"));
		reg.put("zoomOut", ImageDescriptor.createFromFile(Activator.class, "icons/zoom_out.png"));
		reg.put("save_as_picture", ImageDescriptor.createFromFile(Activator.class, "icons/save_as_picture.png"));
		
	}

}
