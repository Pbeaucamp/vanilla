package bpm.fa.ui.management;

import java.lang.reflect.Field;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import bpm.fa.ui.management.icons.Icons;
import bpm.united.olap.api.cache.IUnitedOlapCacheManager;
import bpm.united.olap.api.communication.IModelService;
import bpm.united.olap.api.communication.IServiceProvider;
import bpm.united.olap.remote.services.RemoteServiceProvider;
import bpm.united.olap.remote.services.RemoteUnitedOlapCacheManager;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.fa.ui.management";

	// The shared instance
	private static Activator plugin;
	
	
	
	private IVanillaAPI vanillaRemote;
	
	
	
	private ServiceTracker uOlapModelServiceTracker;
	private ServiceTracker uOlapCacheServiceTracker;
	
	private IModelService uOlapModelService;
	private RemoteUnitedOlapCacheManager uOlapCacheService;
	
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
				}
				
			}
			
			@Override
			public void modifiedService(ServiceReference reference, Object service) {
				
				
			}
			
			@Override
			public Object addingService(ServiceReference reference) {
				uOlapModelService = (IModelService)context.getService(reference);
				return uOlapModelService;
			}
		});
	
		
		uOlapCacheServiceTracker = new ServiceTracker(context, IUnitedOlapCacheManager.class.getName(), new ServiceTrackerCustomizer() {
			
			@Override
			public void removedService(ServiceReference reference, Object service) {
				if (service == uOlapCacheService){
					try {
						uOlapCacheService.persistCacheDisk();
					} catch (Exception e) {
						
						e.printStackTrace();
					}
					uOlapCacheService = null;
				}
				
			}
			
			@Override
			public void modifiedService(ServiceReference reference, Object service) {
				
				
			}
			
			@Override
			public Object addingService(ServiceReference reference) {
				uOlapCacheService = (RemoteUnitedOlapCacheManager)context.getService(reference);
				return uOlapCacheService;
			}
		});
		
	
	
		
	
//		uOlapModelServiceTracker.open();
//		uOlapCacheServiceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (uOlapCacheService != null){
			try{
				uOlapCacheService.persistCacheDisk();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		uOlapModelServiceTracker.close();
		uOlapCacheServiceTracker.close();
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
	
	public IVanillaAPI getVanillaApi(){
		if (vanillaRemote == null && getRepositoryContext() != null){
			vanillaRemote = new RemoteVanillaPlatform(getRepositoryContext().getVanillaContext());
		}
		
		return vanillaRemote;
	}
	
	public IRepositoryContext getRepositoryContext(){
		return bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryContext();
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

	public IModelService getModelService() {
		if (getUolapProvider() != null){
			return getUolapProvider().getModelProvider();
		}
		return uOlapModelService;
	}

	
	
	private IServiceProvider getUolapProvider(){
		if (getRepositoryContext() != null){
			if (provider != null){
				return provider;
			}
			else{
				provider = new RemoteServiceProvider();
				provider.configure(getRepositoryContext().getVanillaContext());
			}
		}
		
		
		
		return null;
	}
	
	private IServiceProvider provider;


	public IUnitedOlapCacheManager getCacheService() {
		if (getRepositoryContext() != null){
			if (uOlapCacheService != null){
				return uOlapCacheService;
			}
			else{
				uOlapCacheService = new RemoteUnitedOlapCacheManager();
				uOlapCacheService.init(getRepositoryContext().getVanillaContext().getVanillaUrl(),
						getRepositoryContext().getVanillaContext().getLogin(),
						getRepositoryContext().getVanillaContext().getPassword());
			}
		}
		return uOlapCacheService;
	}
}
