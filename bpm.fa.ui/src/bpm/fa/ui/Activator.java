package bpm.fa.ui;

import java.lang.reflect.Field;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.fa.ui.icons.Icons;
import bpm.united.olap.api.communication.IModelService;
import bpm.united.olap.api.communication.IRuntimeService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.fa.ui";

	// The shared instance
	private static Activator plugin;
	
	
	private ServiceTracker uOlapRuntimeServiceTracker;
	private ServiceTracker uOlapModelServiceTracker;

	
	private IModelService uOlapModelService;
	private IRuntimeService uOlapRuntimeService;
	
	
	
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		uOlapModelServiceTracker = new ServiceTracker(context, IModelService.class.getName(), new ServiceTrackerCustomizer() {
			
			@Override
			public void removedService(ServiceReference reference, Object service) {
				if (service == uOlapModelService){
					uOlapModelService = null;
					UnitedOlapServiceProvider.getInstance().init(uOlapRuntimeService, uOlapModelService);
				}
				
			}
			
			@Override
			public void modifiedService(ServiceReference reference, Object service) {
				
				
			}
			
			@Override
			public Object addingService(ServiceReference reference) {
				uOlapModelService = (IModelService)context.getService(reference);
				UnitedOlapServiceProvider.getInstance().init(uOlapRuntimeService, uOlapModelService);
				return uOlapModelService;
			}
		});
	
		
		uOlapRuntimeServiceTracker = new ServiceTracker(context, IRuntimeService.class.getName(), new ServiceTrackerCustomizer() {
			
			@Override
			public void removedService(ServiceReference reference, Object service) {
				if (service == uOlapRuntimeService){
					uOlapRuntimeService = null;
					UnitedOlapServiceProvider.getInstance().init(uOlapRuntimeService, uOlapModelService);
				}
				
			}
			
			@Override
			public void modifiedService(ServiceReference reference, Object service) {
				
				
			}
			
			@Override
			public Object addingService(ServiceReference reference) {
				uOlapRuntimeService = (IRuntimeService)context.getService(reference);
				
				UnitedOlapServiceProvider.getInstance().init(uOlapRuntimeService, uOlapModelService);
				return uOlapRuntimeService;
			}
		});
		
		
		
	
		
		uOlapRuntimeServiceTracker.open();
		uOlapModelServiceTracker.open();
	
		

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		uOlapRuntimeServiceTracker.close();
		uOlapModelServiceTracker.close();
		
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
	
	
	
	public IModelService getModelService(){
		return uOlapModelService;
	}

}
