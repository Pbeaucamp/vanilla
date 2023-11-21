package bpm.forms.design.ui;

import java.lang.reflect.Field;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.forms.core.design.IServiceProvider;
import bpm.forms.core.runtime.IInstanceLauncher;
import bpm.forms.core.tools.IFactoryModelElement;
import bpm.forms.design.ui.icons.IconsNames;
import bpm.forms.design.ui.preferences.VanillaContextImpl;
import bpm.forms.model.services.FactoryModelElement;
import bpm.forms.remote.services.RemoteInstanceLauncherService;
import bpm.forms.remote.services.RemoteServiceProvider;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.forms.design.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	
	private IServiceProvider serviceProvider;
	
	private IFactoryModelElement factoryModel;
	private IInstanceLauncher instanceLauncher;
	
	private VanillaContextImpl vanillaContext;
	
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
//		ServiceReference reference = context.getServiceReference(IServiceProvider.class.getName());
//		serviceProvider = (IServiceProvider) context.getService(reference);
		

		
			
		
		factoryModel = new FactoryModelElement();
		
//		reference = context.getServiceReference(IInstanceLauncher.class.getName());
//		instanceLauncher = (IInstanceLauncher)context.getService(reference);
		instanceLauncher = new RemoteInstanceLauncherService();
		
//		reference = context.getServiceReference(IServiceProvider.class.getName());
//		serviceProvider = (IServiceProvider)context.getService(reference);
		serviceProvider = new RemoteServiceProvider();
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
	
	
	public IServiceProvider getServiceProvider(){
		serviceProvider.configure(getVanillaRuntimeUrl());
		return serviceProvider;
	}
	
	
	public void resetVanillaContext(){
		vanillaContext = null;
		getVanillaContext();
	}
	
	public VanillaContextImpl getVanillaContext(){
		bpm.norparena.ui.menu.Activator hostPlugin = bpm.norparena.ui.menu.Activator.getDefault();
		
		if (vanillaContext == null){
			//XXX ere changed, go thru host plugin
			IVanillaContext vanCtx = hostPlugin.getRepositoryContext().getVanillaContext();
			IRepositoryContext repCtx = hostPlugin.getRepositoryContext();
			
			IVanillaContext vCtx = new BaseVanillaContext(
					vanCtx.getVanillaUrl(), 
					vanCtx.getLogin(), 
					vanCtx.getPassword());
			
			
			vanillaContext = new VanillaContextImpl(vCtx, repCtx.getGroup(), repCtx.getRepository());
			
//			IPreferenceStore store = getPreferenceStore();
//			
//			IVanillaContext vCtx = new BaseVanillaContext(
//					store.getString(VanillaContextPreferencesInitializer.VANILLA_URL), 
//					store.getString(VanillaContextPreferencesInitializer.VANILLA_LOGIN), 
//					store.getString(VanillaContextPreferencesInitializer.VANILLA_PASSWORD));
//			
//			Group group = new Group();
//			group.setId(store.getString(VanillaContextPreferencesInitializer.VANILLA_GROUP_ID));
//			group.setName("Dummy");
//			
//			Repository rep = new Repository();
//			rep.setId(0);
//			rep.setName("Dummy");
			
//			vanillaContext = new VanillaContextImpl(vCtx, group, rep);
//			((VanillaContextImpl)vanillaContext).setGroupId(store.getInt(VanillaContextPreferencesInitializer.VANILLA_GROUP_ID));
//			((VanillaContextImpl)vanillaContext).setVanillaUrl();
//			((VanillaContextImpl)vanillaContext).setLogin(store.getString(VanillaContextPreferencesInitializer.VANILLA_LOGIN));
//			((VanillaContextImpl)vanillaContext).setPassword(store.getString(VanillaContextPreferencesInitializer.VANILLA_PASSWORD));
			
			oldVanillaRuntimeUrl = vanillaContext.getVanillaContext().getVanillaUrl();
		}
		return vanillaContext;
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		for(Field f : IconsNames.class.getFields()){
			
			try {
				reg.put((String)f.get(null), ImageDescriptor.createFromFile(Activator.class, "icons/" + f.get(null))); //$NON-NLS-1$
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				
				e.printStackTrace();
			}
			
		}
		
		
	}

	
	public IFactoryModelElement getFactoryModel(){
		
		return factoryModel;
	}
	
	public IInstanceLauncher getInstanceLauncher(){
		instanceLauncher.configure(getVanillaRuntimeUrl());
		return instanceLauncher;
	}
	
	private String getVanillaRuntimeUrl(){
		
		getVanillaContext();
//		VanillaContextImpl ctx = (VanillaContextImpl)getVanillaContext();
//		
//		if (oldVanillaRuntimeUrl == null){
//			oldVanillaRuntimeUrl = ctx.getVanillaRuntimeUrl();
//		}
//		else if (!oldVanillaRuntimeUrl.equals(ctx.getVanillaRuntimeUrl())){
//			oldVanillaRuntimeUrl = ctx.getVanillaRuntimeUrl();
//		}
		return oldVanillaRuntimeUrl;
//		return "http://localhost:9191/bpm.fd.servletbridge/";
	}
	
	
	private String oldVanillaRuntimeUrl = null;
}
