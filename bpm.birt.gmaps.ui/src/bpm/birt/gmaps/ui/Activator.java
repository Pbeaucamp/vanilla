package bpm.birt.gmaps.ui;

import java.lang.reflect.Field;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.birt.gmaps.ui.icons.Icons;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapRegistry;
import bpm.vanilla.map.core.design.kml.IKmlRegistry;
import bpm.vanilla.map.remote.core.design.impl.RemoteFusionMapRegistry;
import bpm.vanilla.map.remote.core.design.kml.impl.RemoteKmlRegistry;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.birt.gmaps.ui";

	// The shared instance
	private static Activator plugin;
	
	private IKmlRegistry kmlRegistry;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}
	
	public IKmlRegistry getKmlRegistry(){
		if (kmlRegistry == null){
			kmlRegistry = new RemoteKmlRegistry();
		}
		
		return kmlRegistry;
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
		for(Field f : Icons.class.getFields()){
			
			try {
				reg.put((String)f.get(null), ImageDescriptor.createFromFile(Activator.class, "icons/" + f.get(null)));
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				
				e.printStackTrace();
			}
			
		}
	}
}
