package bpm.es.dndserver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.es.dndserver.api.DNDOProject;
import bpm.es.dndserver.api.repository.AxisDirectoryItemWrapper;
import bpm.es.dndserver.api.repository.RepositoryWrapper;
import bpm.es.dndserver.icons.IconsName;
import bpm.es.dndserver.tools.OurLogger;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "bpm.es.dndserver"; //$NON-NLS-1$

	private static Activator plugin;
	
	private DNDOProject defaultProject;
	
	private List<AxisDirectoryItemWrapper> dndObjects = 
		new ArrayList<AxisDirectoryItemWrapper>();
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		OurLogger.init();
		OurLogger.info(Messages.Activator_1 + (new Date()).toGMTString());
		OurLogger.info(Messages.Activator_2);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		OurLogger.info(Messages.Activator_3);
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
		
		//our icons
		for(Field f : IconsName.class.getFields()){
			try {
				reg.put((String)f.get(null), ImageDescriptor.createFromFile(Activator.class, "icons/" + f.get(null))); //$NON-NLS-1$
			} catch (Exception e) {
				OurLogger.error(Messages.Activator_5, e);
			}
		}
		
		//es original icons
		//XXX need to be put under the new method
		reg.put("package", ImageDescriptor.createFromFile(Activator.class, "icons/disconnect.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("sched", ImageDescriptor.createFromFile(Activator.class, "icons/scheduled_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("secu", ImageDescriptor.createFromFile(Activator.class, "icons/lock.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("refresh", ImageDescriptor.createFromFile(Activator.class, "icons/refresh.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("unlock", ImageDescriptor.createFromFile(Activator.class, "icons/unlock.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("lock",  ImageDescriptor.createFromFile(Activator.class, "icons/lock.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("add", ImageDescriptor.createFromFile(Activator.class, "icons/add.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("del", ImageDescriptor.createFromFile(Activator.class, "icons/del.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("user", ImageDescriptor.createFromFile(Activator.class, "icons/user.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("group", ImageDescriptor.createFromFile(Activator.class, "icons/group.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("long_load", ImageDescriptor.createFromFile(Activator.class, "icons/long_load.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("stop_long_load", ImageDescriptor.createFromFile(Activator.class, "icons/stop_long_load.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("filter",  ImageDescriptor.createFromFile(Activator.class, "icons/filter.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("fasd", ImageDescriptor.createFromFile(Activator.class, "icons/cube.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fasd_run", ImageDescriptor.createFromFile(Activator.class, "icons/cube_run.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fasd_stop",ImageDescriptor.createFromFile(Activator.class, "icons/cube_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("fd", ImageDescriptor.createFromFile(Activator.class, "icons/fd.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fd_deployed", ImageDescriptor.createFromFile(Activator.class, "icons/fd_deployed.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fd_deployed_run", ImageDescriptor.createFromFile(Activator.class, "icons/fd_deployed_run.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fd_deployed_stop", ImageDescriptor.createFromFile(Activator.class, "icons/fd_deployed_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("folder", ImageDescriptor.createFromFile(Activator.class, "icons/folder.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		

		
		
		reg.put("default", ImageDescriptor.createFromFile(Activator.class, "icons/object.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("xaction", ImageDescriptor.createFromFile(Activator.class, "icons/xaction.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fmdt", ImageDescriptor.createFromFile(Activator.class, "icons/fmdt.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fmdt_run", ImageDescriptor.createFromFile(Activator.class, "icons/fmdt_run.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fmdt_stop", ImageDescriptor.createFromFile(Activator.class, "icons/fmdt_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("import", ImageDescriptor.createFromFile(Activator.class, "icons/import.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("role", ImageDescriptor.createFromFile(Activator.class, "icons/role.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("edit", ImageDescriptor.createFromFile(Activator.class, "icons/edit.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("jasper", ImageDescriptor.createFromFile(Activator.class, "icons/jasper.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("jasper_run", ImageDescriptor.createFromFile(Activator.class, "icons/jasper_run.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("jasper_stop", ImageDescriptor.createFromFile(Activator.class, "icons/jasper_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("birt", ImageDescriptor.createFromFile(Activator.class, "icons/birt.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("birt_run", ImageDescriptor.createFromFile(Activator.class, "icons/birt_run.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("birt_stop", ImageDescriptor.createFromFile(Activator.class, "icons/birt_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("documents", ImageDescriptor.createFromFile(Activator.class, "icons/adddoc_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("birt_deployed", ImageDescriptor.createFromFile(Activator.class, "icons/birt_deployed.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("dico", ImageDescriptor.createFromFile(Activator.class, "icons/dictionary.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("datasource", ImageDescriptor.createFromFile(Activator.class, "icons/database.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("start", ImageDescriptor.createFromFile(Activator.class, "icons/start.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("stop", ImageDescriptor.createFromFile(Activator.class, "icons/stop.png")); //$NON-NLS-1$ //$NON-NLS-2$
	
		reg.put("adressable", ImageDescriptor.createFromFile(Activator.class, "icons/AddAdressable.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("unadressable", ImageDescriptor.createFromFile(Activator.class, "icons/UnAdressable.gif")); //$NON-NLS-1$ //$NON-NLS-2$
	
		reg.put("addfiltre", ImageDescriptor.createFromFile(Activator.class, "icons/AddFiltre.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("delfiltre", ImageDescriptor.createFromFile(Activator.class, "icons/DelFiltre.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("variable", ImageDescriptor.createFromFile(Activator.class, "icons/variable.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("orbeon_16", ImageDescriptor.createFromFile(Activator.class, "icons/orbeon_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fwr", ImageDescriptor.createFromFile(Activator.class, "icons/fwr.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fwr_run", ImageDescriptor.createFromFile(Activator.class, "icons/fwr_run.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("fwr_stop", ImageDescriptor.createFromFile(Activator.class, "icons/fwr_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$
		

		
		reg.put("biw", ImageDescriptor.createFromFile(Activator.class, "icons/biw.gif")); //$NON-NLS-1$ //$NON-NLS-2$
//		reg.put("biw_run", ImageDescriptor.createFromFile(Activator.class, "icons/biw_run.png"));
//		reg.put("biw_stop", ImageDescriptor.createFromFile(Activator.class, "icons/biw_stop.png"));
		
		reg.put("gtw", ImageDescriptor.createFromFile(Activator.class, "icons/gtw_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("gtw_run", ImageDescriptor.createFromFile(Activator.class, "icons/gtw_16_start.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("gtw_stop", ImageDescriptor.createFromFile(Activator.class, "icons/gtw_16_stop.png")); //$NON-NLS-1$ //$NON-NLS-2$

		
		reg.put("fav", ImageDescriptor.createFromFile(Activator.class, "icons/view.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("md", ImageDescriptor.createFromFile(Activator.class, "icons/md.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("doc", ImageDescriptor.createFromFile(Activator.class, "icons/doc.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("jsp1", ImageDescriptor.createFromFile(Activator.class, "icons/jsp1.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("jsp2", ImageDescriptor.createFromFile(Activator.class, "icons/jsp2.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("jsp3", ImageDescriptor.createFromFile(Activator.class, "icons/jsp3.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("jsp4", ImageDescriptor.createFromFile(Activator.class, "icons/jsp4.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("save", ImageDescriptor.createFromFile(Activator.class, "icons/save.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("custom", ImageDescriptor.createFromFile(Activator.class, "icons/custom.png")); //$NON-NLS-1$ //$NON-NLS-2$
	
		reg.put("searchreplace", ImageDescriptor.createFromFile(Activator.class, "icons/text_replace.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		
		reg.put("addlink", ImageDescriptor.createFromFile(Activator.class, "icons/link_add.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("link", ImageDescriptor.createFromFile(Activator.class, "icons/link.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put("update", ImageDescriptor.createFromFile(Activator.class, "icons/update.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("run_action", ImageDescriptor.createFromFile(Activator.class, "icons/application_xp_terminal.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("ged_entry", ImageDescriptor.createFromFile(Activator.class, "icons/ged16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("temp_ged", ImageDescriptor.createFromFile(Activator.class, "icons/ged_temporary.png")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Will create an dndo project using the default connection (the repository's)
	 * as source. target will NOT be set
	 * 
	 * @throws Exception 
	 */
	public void createDefaultProject() throws Exception {
		//Activator act = bpm.vanilla.server.client.ui.clustering.menu.Activator;
//		bpm.vanilla.server.client.ui.clustering.menu.Activator activator 
//			= bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault();
//		activator.getManager()
		//AxisRepositoryConnection sock = (AxisRepositoryConnection) adminbirep.Activator.getDefault().getSocket();
		//sock.
		//IRepository rep = sock.getRepository();
		
//		String repositoryName = adminbirep.Activator.getDefault().getActiveRepository().getName();
//		RepositoryWrapper inputRepositoryWrapper = new RepositoryWrapper(sock, repositoryName);
		
		//bpm.vanilla.server.client.ui.clustering.menu.Activator 
	
		//RepositoryWrapper inputRepositoryWrapper = new RepositoryWrapper(sock, repositoryName);
		
		RepositoryWrapper inputRepositoryWrapper = null;
		RepositoryWrapper outputRepositoryWrapper = null;
		
		defaultProject = new DNDOProject();
		defaultProject.setInputRepository(inputRepositoryWrapper);
		defaultProject.setOutputRepository(outputRepositoryWrapper);
	}
	
	public DNDOProject getDefaultProject() {
		return defaultProject;
	}
	
	public void setDndObjects(List<AxisDirectoryItemWrapper> dndObjects) {
		this.dndObjects = dndObjects;
	}
	
	public List<AxisDirectoryItemWrapper> getDndObjects() {
		return dndObjects;
	}
}
