package bpm.vanilla.server.client.ui.clustering.menu;

import java.lang.reflect.Field;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.server.client.ui.clustering.menu.icons.Icons;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.vanilla.server.client.ui.clustering.menu"; //$NON-NLS-1$
	public static final String SOFT_ID = IRepositoryApi.HYPERVISION;

	// The shared instance
	private static Activator plugin;

	// private IManager manager;
	private IVanillaAPI vanillaApi;
	private IVanillaContext vanillaContext;
	private User user;

	private List<Button> items;

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

	public void setManager(String vanillaUrl, String login, String password) throws Exception {

		vanillaContext = new BaseVanillaContext(vanillaUrl, login, password);
		vanillaApi = new RemoteVanillaPlatform(vanillaContext);

		User user = vanillaApi.getVanillaSecurityManager().authentify("", vanillaContext.getLogin(), vanillaContext.getPassword(), false); //$NON-NLS-1$
		if (user == null) {
			this.vanillaApi = null;
			throw new Exception(Messages.Activator_1);
		}
		else if (!user.isSuperUser()) {
			throw new Exception(Messages.Activator_2);
		}
		
		this.user = user;
	}

	public IVanillaAPI getVanillaApi() {

		return vanillaApi;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put("login", ImageDescriptor.createFromFile(Activator.class, "icons/login.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("logout", ImageDescriptor.createFromFile(Activator.class, "icons/logout.png")); //$NON-NLS-1$ //$NON-NLS-2$

		for (Field f : Icons.class.getFields()) {

			try {
				reg.put((String) f.get(null), ImageDescriptor.createFromFile(Activator.class, "icons/" + f.get(null))); //$NON-NLS-1$
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
	}

	public IVanillaContext getVanillaContext() {
		return vanillaContext;
	}
	
	public User getUser() {
		return user;
	}

	public void setBarButtons(List<Button> items) {
		this.items = items;
	}
	
	public List<Button> getBarButtons() {
		return items;
	}

}
