package adminbirep;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import adminbirep.icons.Icons;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.views.ViewGroup;
import bpm.birep.admin.client.views.ViewRole;
import bpm.birep.admin.client.views.ViewUser;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "adminBIRep"; //$NON-NLS-1$
	public static final String SOFT_ID = IRepositoryApi.ES;

	private static Activator plugin;

	public static String expirationDate = ""; //$NON-NLS-1$

	private IVanillaAPI vanillaApi;
	private IRepositoryApi repApi;

	private VanillaParameterComponent parameterComponent;

	private IRepositoryContext repCtx;

	public void initManagers(IRepositoryContext repCtx, IVanillaAPI vanillaApi) throws Exception {
		this.repCtx = repCtx;
		this.vanillaApi = vanillaApi;
		this.repApi = new RemoteRepositoryApi(repCtx);
		this.parameterComponent = new RemoteVanillaParameterComponent(repCtx.getVanillaContext());
	}

	public void resetRepository(Repository g) throws Exception {
		this.repCtx = new BaseRepositoryContext(this.repCtx.getVanillaContext(), this.repCtx.getGroup(), g);

		this.repApi = new RemoteRepositoryApi(repCtx);
	}

	public IRepositoryApi getRepositoryApi() {
		return repApi;
	}

	public IVanillaAPI getVanillaApi() {
		return vanillaApi;
	}

	public Repository getCurrentRepository() {
		return repCtx.getRepository();
	}

	public VanillaParameterComponent getRemoteParameter() {
		return parameterComponent;
	}

	public String getLogin() {
		return repCtx.getVanillaContext().getLogin();
	}

	public String getUserPassword() {
		return repCtx.getVanillaContext().getPassword();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
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
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(Icons.EXPORT, ImageDescriptor.createFromFile(Activator.class, "icons/export.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(Icons.IMPORT_VANILLA_PLACE, ImageDescriptor.createFromFile(Activator.class, "icons/import.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(Icons.CONNECT, ImageDescriptor.createFromFile(Activator.class, "icons/hierarchy.gif")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(Icons.TEMPLATE_INDEX1, ImageDescriptor.createFromFile(Activator.class, "icons/templateindex1.png")); //$NON-NLS-1$ 
		reg.put(Icons.TEMPLATE_INDEX2, ImageDescriptor.createFromFile(Activator.class, "icons/templateindex2.png")); //$NON-NLS-1$ 
		reg.put(Icons.TEMPLATE_PREVIEW1, ImageDescriptor.createFromFile(Activator.class, "icons/Home_preview_1.png")); //$NON-NLS-1$ 
		reg.put(Icons.TEMPLATE_PREVIEW2, ImageDescriptor.createFromFile(Activator.class, "icons/Home_preview_2.png")); //$NON-NLS-1$ 
		reg.put("package", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/disconnect.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("sched", ImageDescriptor.createFromFile(Activator.class, "icons/scheduled_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("secu", ImageDescriptor.createFromFile(Activator.class, "icons/lock.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("tasklist", ImageDescriptor.createFromFile(Activator.class, "icons/tasklist.png")); //$NON-NLS-1$ //$NON-NLS-2$//new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/tasklist.png"));
		reg.put("dwhview", ImageDescriptor.createFromFile(Activator.class, "icons/dwhview.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("refresh", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/refresh.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("unlock", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/unlock.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("lock", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/lock.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("add", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/add.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("del", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/del.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("user", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/user.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("group", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/group.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("long_load", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/long_load.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("stop_long_load", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/stop_long_load.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("filter", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/filter.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("fasd", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/cube.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fasd_run", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/cube_run.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fasd_stop", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/cube_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("fd", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/fd.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fd_deployed", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/fd_deployed.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fd_deployed_run", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/fd_deployed_run.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fd_deployed_stop", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/fd_deployed_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(Icons.FOLDER, new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/folder.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("default", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/object.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("xaction", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/xaction.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fmdt", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/fmdt.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fmdt_run", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/fmdt_run.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fmdt_stop", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/fmdt_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("import", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/import.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("role", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/role.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("edit", ImageDescriptor.createFromFile(Activator.class, "/adminbirep/icons/edit_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("browse", ImageDescriptor.createFromFile(Activator.class, "/adminbirep/icons/browse.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("jasper", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/jasper.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("jasper_run", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/jasper_run.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("jasper_stop", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/jasper_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(Icons.BIRT, new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/birt.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("birt_run", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/birt_run.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("birt_stop", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/birt_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("documents", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/adddoc_16.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("birt_deployed", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/birt_deployed.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("dico", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/dictionary.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("datasource", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/database.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("start", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/start.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("stop", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/stop.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("adressable", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/AddAdressable.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("unadressable", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/UnAdressable.gif")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("addfiltre", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/AddFiltre.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("delfiltre", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/DelFiltre.gif")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("variable", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/variable.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("orbeon_16", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/orbeon_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(Icons.FWR, new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/fwr.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fwr_run", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/fwr_run.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fwr_stop", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/fwr_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(Icons.BIW, new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/biw.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(Icons.BIW_STOP, ImageDescriptor.createFromFile(Activator.class, "icons/biw_stop.png")); //$NON-NLS-1$
		reg.put(Icons.BIW_RUN, ImageDescriptor.createFromFile(Activator.class, "icons/biw_run.png")); //$NON-NLS-1$

		reg.put("gtw", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/gtw_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("gtw_run", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/gtw_16_start.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("gtw_stop", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/gtw_16_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("fav", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/view.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("md", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/md.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(Icons.EXT_DOC, new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/doc.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("jsp1", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/jsp1.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("jsp2", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/jsp2.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("jsp3", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/jsp3.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("jsp4", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/jsp4.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("save", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/save.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("custom", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/custom.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("searchreplace", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/text_replace.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("vanilla_logo", ImageDescriptor.createFromFile(Activator.class, "icons/logo.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("addlink", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/link_add.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("link", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/link.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("update", ImageDescriptor.createFromFile(Activator.class, "icons/update.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("run_action", ImageDescriptor.createFromFile(Activator.class, "icons/application_xp_terminal.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("ged_entry", new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/ged16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(Icons.TEMP_GED, new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/ged_temporary.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(Icons.WAIT, ImageDescriptor.createFromFile(Activator.class, "icons/wait.gif")); //$NON-NLS-1$
		reg.put(Icons.CHECK, ImageDescriptor.createFromFile(Activator.class, "icons/check.png")); //$NON-NLS-1$
		reg.put(Icons.NO_CHECK, ImageDescriptor.createFromFile(Activator.class, "icons/no_check.png")); //$NON-NLS-1$

		reg.put(Icons.WIDGET, new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/widgets.png")); //$NON-NLS-1$
		reg.put(Icons.IGOOGLE, new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/igoogle.png")); //$NON-NLS-1$
		reg.put(Icons.NETVIBES, new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/netvibes.png")); //$NON-NLS-1$
		reg.put(Icons.FLUXRSS, new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "resources/icons/fluxRss.png")); //$NON-NLS-1$
		reg.put(Icons.PICTURE, ImageDescriptor.createFromFile(Activator.class, "icons/toolbar_pictures.png")); //$NON-NLS-1$
		reg.put(Icons.REPORTS_GROUP, ImageDescriptor.createFromFile(Activator.class, "icons/reports_group_16.png")); //$NON-NLS-1$
		reg.put(Icons.KPI_THEME, ImageDescriptor.createFromFile(Activator.class, "icons/kpi_theme_16.png")); //$NON-NLS-1$
		
		reg.put(Icons.ARROW_UP, ImageDescriptor.createFromFile(Activator.class, "icons/arrow_up.png")); //$NON-NLS-1$
		reg.put(Icons.ARROW_DOWN, ImageDescriptor.createFromFile(Activator.class, "icons/arrow_down.png")); //$NON-NLS-1$
		reg.put(Icons.WKF, ImageDescriptor.createFromFile(Activator.class, "icons/workflow_16.gif")); //$NON-NLS-1$
		reg.put(Icons.MAIL, ImageDescriptor.createFromFile(Activator.class, "icons/email.png")); //$NON-NLS-1$

	}

	/**
	 * recreate all trees to be called once the server parameters have been
	 * changed
	 */
	public void rebuildViewsContent() {
		ViewTree viewTree = (ViewTree) getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewTree.ID);
		if (viewTree != null) {
			try {
				viewTree.createInput();
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.Activator_148, e.getMessage());
			}
		}

		ViewGroup viewGroup = (ViewGroup) getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewGroup.ID);
		if (viewGroup != null) {
			try {
				viewGroup.initTree();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ViewUser viewUser = (ViewUser) getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewUser.ID);
		if (viewUser != null) {
			viewUser.initTree();
		}

		ViewRole viewRole = (ViewRole) getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewRole.ID);
		if (viewRole != null) {
			try {
				viewRole.createInput();
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.Activator_148, e.getMessage());
			}
		}
	}

}
