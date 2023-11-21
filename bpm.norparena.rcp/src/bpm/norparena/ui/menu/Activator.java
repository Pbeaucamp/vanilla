package bpm.norparena.ui.menu;

import java.lang.reflect.Field;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.norparena.ui.menu.client.preferences.PreferenceConstants;
import bpm.norparena.ui.menu.icons.Icons;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.norparena.rcp"; //$NON-NLS-1$

	public static final String SOFT_ID = IRepositoryApi.NORPARENA;

	// The shared instance
	private static Activator plugin;

	private IRepositoryApi socket;
	private IRepository repository;
	private IVanillaAPI remote;
	
	private IRepositoryContext repositoryContext;
	
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

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/**
	 * @return the socket
	 */
	public IRepositoryApi getSocket() {
		if(socket == null) {
			
			socket = new RemoteRepositoryApi(repositoryContext);
		}
		return socket;
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		for(Field f : Icons.class.getFields()){
			
			try {
				reg.put((String)f.get(null), ImageDescriptor.createFromFile(Activator.class, "icons/" + f.get(null))); //$NON-NLS-1$
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public IVanillaAPI getRemote() {
		if(remote == null) {
			remote = new RemoteVanillaPlatform(repositoryContext.getVanillaContext());
		}
		return remote;
	}

	public IRepositoryContext getRepositoryContext() {
		return repositoryContext;
	}

	public void setRepositoryContext(IRepositoryContext repositoryContext) {
		if(repositoryContext != null) {
			if(repositoryContext.getGroup() == null && repositoryContext instanceof BaseRepositoryContext){
				Group dummyGroup = new Group();
				dummyGroup.setId(-1);
				this.repositoryContext = new BaseRepositoryContext(repositoryContext.getVanillaContext(), dummyGroup, repositoryContext.getRepository());
			}
			else {
				this.repositoryContext = repositoryContext;
			}
		
			getPreferenceStore().setValue(PreferenceConstants.P_BI_REPOSITIRY_USER, repositoryContext.getVanillaContext().getLogin());
			getPreferenceStore().setValue(PreferenceConstants.P_BI_REPOSITIRY_PASSWORD, repositoryContext.getVanillaContext().getPassword());
			getPreferenceStore().setValue(PreferenceConstants.p_BI_SECURITY_SERVER, repositoryContext.getVanillaContext().getVanillaUrl());
		}
		this.remote = null;
	}

	public IRepository getIRepository() throws Exception {
		if(repository == null) {
			repository = new Repository(getSocket());
		}
		return repository;
	}
	
	
}
