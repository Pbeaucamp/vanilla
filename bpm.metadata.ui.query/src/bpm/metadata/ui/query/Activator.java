package bpm.metadata.ui.query;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.metadata.ui.query"; //$NON-NLS-1$

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
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		
		reg.put("directory", ImageDescriptor.createFromFile(Activator.class, "icons/directory.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("directoryItem", ImageDescriptor.createFromFile(Activator.class, "icons/directory_item.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("directoryItemBirt", ImageDescriptor.createFromFile(Activator.class, "icons/birt.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("addCol", ImageDescriptor.createFromFile(Activator.class, "icons/add-16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("delCol", ImageDescriptor.createFromFile(Activator.class, "icons/del-16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("column", ImageDescriptor.createFromFile(Activator.class, "icons/column.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("table", ImageDescriptor.createFromFile(Activator.class, "icons/business_table.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("prompt", ImageDescriptor.createFromFile(Activator.class, "icons/prompt.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("filter", ImageDescriptor.createFromFile(Activator.class, "icons/filter.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("complexFilter", ImageDescriptor.createFromFile(Activator.class, "icons/filter_complex.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("sqlFilter", ImageDescriptor.createFromFile(Activator.class, "icons/filter_sql.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("edit", ImageDescriptor.createFromFile(Activator.class, "icons/edit.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("down", ImageDescriptor.createFromFile(Activator.class, "icons/arrow_down.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("up", ImageDescriptor.createFromFile(Activator.class, "icons/arrow_up.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("sql", ImageDescriptor.createFromFile(Activator.class, "icons/sql.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("calc", ImageDescriptor.createFromFile(Activator.class, "icons/calculator.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("lov-16", ImageDescriptor.createFromFile(Activator.class, "icons/lov-16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("addCol2", ImageDescriptor.createFromFile(Activator.class, "icons/add2_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("delCol2", ImageDescriptor.createFromFile(Activator.class, "icons/del2_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("relstrat", ImageDescriptor.createFromFile(Activator.class, "icons/tables_relation.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("count", ImageDescriptor.createFromFile(Activator.class, "icons/count.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
	}
}
