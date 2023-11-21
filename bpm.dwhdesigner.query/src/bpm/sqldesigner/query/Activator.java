package bpm.sqldesigner.query;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "SQLDesigner";

	private static Activator plugin;

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

	public static Activator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);

		reg.put("undo", ImageDescriptor.createFromFile(Activator.class,
				"icons/undo.png"));
		reg.put("redo", ImageDescriptor.createFromFile(Activator.class,
				"icons/redo.png"));
		reg.put("delete", ImageDescriptor.createFromFile(Activator.class,
				"icons/delete.png"));
		reg.put("table", ImageDescriptor.createFromFile(Activator.class,
				"icons/table.png"));
		reg.put("key", ImageDescriptor.createFromFile(Activator.class,
				"icons/key.png"));
		reg.put("zoomIn", ImageDescriptor.createFromFile(Activator.class,
				"icons/zoom_in.png"));
		reg.put("zoomOut", ImageDescriptor.createFromFile(Activator.class,
				"icons/zoom_out.png"));
		reg.put("filter", ImageDescriptor.createFromFile(Activator.class,
				"icons/filter.png"));
		reg.put("valid", ImageDescriptor.createFromFile(Activator.class,
				"icons/valid.png"));
		reg.put("cancel", ImageDescriptor.createFromFile(Activator.class,
				"icons/delete.png"));
		reg.put("invalid", ImageDescriptor.createFromFile(Activator.class,
				"icons/delete.png"));
		reg.put("trash", ImageDescriptor.createFromFile(Activator.class,
				"icons/trash.png"));
	}
}
