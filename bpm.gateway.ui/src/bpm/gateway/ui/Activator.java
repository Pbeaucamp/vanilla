package bpm.gateway.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.framework.BundleContext;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.runtime2.RuntimeEngine;
import bpm.gateway.ui.actions.ActionSave;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.ext.TransformationUIExtension;
import bpm.gateway.ui.gef.commands.LinkCommand;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.views.ResourceViewPart;
import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.designer.ui.common.IRepositoryContextChangedListener;
import bpm.vanilla.designer.ui.common.IRepositoryContextMonitorable;
import bpm.vanilla.map.core.design.IFactoryModelObject;
import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.map.model.impl.FactoryMapModel;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.Customer;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.versionning.VersionningManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IDesignerActivator<GatewayEditorInput>, IRepositoryContextMonitorable {
	private static List<TransformationUIExtension> additionalTransformations = new ArrayList<TransformationUIExtension>();

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.gateway.ui"; //$NON-NLS-1$
	public static final boolean HAS_LICENCE = false;
	public static final String LICENCE_PASSWORD = "u56fsfDfsd9fds$3fd"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private PropertyChangeSupport modelLoadListener = new PropertyChangeSupport(this);
	private List<IRepositoryContextChangedListener> repositoryConnectionListeners = new ArrayList<IRepositoryContextChangedListener>();

	// public static RuntimeEngine localRuntimeEngine = new RuntimeEngine();
	public static RuntimeEngine localRuntimeEngine = new RuntimeEngine();

	private IRepositoryApi repositoryConnection = null;

	private IMapDefinitionService mapDefinitionService;

	private Customer customer;
	
	//This boolean is use when the user select link from the context menu and we need to revert to select after the link
	private boolean onlyOneLink;
	
	//This command is use when the user select link from the context menu
	private LinkCommand activeLinkCommand;

	public boolean isConnectedOnRepository() {
		return repositoryConnection != null;
	}

	/**
	 * The constructor
	 */
	public Activator() {

		// inti the GATEWAY_HOME
		ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_BIGATEWAY_HOME).setValue(Platform.getInstallLocation().getURL().getPath() + "/"); //$NON-NLS-1$

		// add the variable TEMP folder

		ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP);
		Variable GATEWAY_TEMP = ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP);
		GATEWAY_TEMP.setValue(Platform.getInstallLocation().getURL().getPath() + "/temp/"); //$NON-NLS-1$

		File f = new File(GATEWAY_TEMP.getValueAsString());
		if (!f.exists()) {

			f.mkdirs();

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		plugin = this;

		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

		for (IExtensionPoint e : extensionRegistry.getExtensionPoints("bpm.gateway.ui")) { //$NON-NLS-1$
			for (IExtension x : e.getExtensions()) {
				/*
				 * create all Aditional TransformationExtensions
				 */
				for (IConfigurationElement c : x.getConfigurationElements()) {
					final TransformationUIExtension ext = new TransformationUIExtension();
					final IConfigurationElement _c = c;

					ISafeRunnable runnable = new ISafeRunnable() {
						public void handleException(Throwable exception) {
							exception.printStackTrace();
						}

						public void run() throws Exception {

							ext.setPaletteDrawerId(_c.getAttribute("paletteDrawerId")); //$NON-NLS-1$
							ext.setIcon16(_c.getAttribute("icon16")); //$NON-NLS-1$
							ext.setIcon32(_c.getAttribute("icon32")); //$NON-NLS-1$

							/*
							 * set the Transfo class
							 */
							Class newClass = _c.createExecutableExtension("transformationClass").getClass(); //$NON-NLS-1$
							ext.setTransformationClass(newClass);

							addTransformationExtension(ext);
						}
					};
					SafeRunner.run(runnable);

				}
			}
		}

		/*
		 * register Norparena
		 */
		context.registerService(IMapDefinitionService.class.getName(), new RemoteMapDefinitionService(), null);
		context.registerService(IFactoryModelObject.class.getName(), new FactoryMapModel(), null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public SessionSourceProvider getSessionSourceProvider() {
		ISourceProviderService service = (ISourceProviderService) getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
		return (SessionSourceProvider) service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);

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
	 * Returns an image descriptor for the image file at the given plug-in relative path
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

		reg.put(IconsNames.mdm_entity, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.mdm_entity)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.mdm_attribute, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.mdm_attribute)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.mdm_16_ext, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.mdm_16_ext)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.mdm_32_ext, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.mdm_32_ext)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.mdm_16_ins, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.mdm_16_ins)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.mdm_32_ins, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.mdm_32_ins)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("folder_16", ImageDescriptor.createFromFile(Activator.class, "icons/folder.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.design_16, ImageDescriptor.createFromFile(Activator.class, "icons/design.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.database_16, ImageDescriptor.createFromFile(Activator.class, "icons/database.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.file_go_16, ImageDescriptor.createFromFile(Activator.class, "icons/file_go.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.link_16, ImageDescriptor.createFromFile(Activator.class, "icons/link.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.newDocument_16, ImageDescriptor.createFromFile(Activator.class, "icons/new.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.package_go_16, ImageDescriptor.createFromFile(Activator.class, "icons/package_go.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.page_go_16, ImageDescriptor.createFromFile(Activator.class, "icons/page_go.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.save_as_16, ImageDescriptor.createFromFile(Activator.class, "icons/save_as.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.xls_16, ImageDescriptor.createFromFile(Activator.class, "icons/xls-16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.dedoublon_16, ImageDescriptor.createFromFile(Activator.class, "icons/dedoublon_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.dedoublon_32, ImageDescriptor.createFromFile(Activator.class, "icons/dedoublon_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.variable_16, ImageDescriptor.createFromFile(Activator.class, "icons/variable.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.ranging_16, ImageDescriptor.createFromFile(Activator.class, "icons/ranging_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.arrow_left_16, ImageDescriptor.createFromFile(Activator.class, "icons/arrow_left.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.forms_16, ImageDescriptor.createFromFile(Activator.class, "icons/orbeon_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.fmdt_16, ImageDescriptor.createFromFile(Activator.class, "icons/fmdt.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.gateway_16, ImageDescriptor.createFromFile(Activator.class, "icons/gateway_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.sequence_16, ImageDescriptor.createFromFile(Activator.class, "icons/sequence_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.comment_16, ImageDescriptor.createFromFile(Activator.class, "icons/note_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.XMLFile_16_input, ImageDescriptor.createFromFile(Activator.class, "icons/XMLFile_16_from.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.align_botttom_16, ImageDescriptor.createFromFile(Activator.class, "icons/text_padding_bottom.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.align_top_16, ImageDescriptor.createFromFile(Activator.class, "icons/text_padding_top.png")); //$NON-NLS-1$ //$NON-NLS-2$		
		reg.put(IconsNames.align_left_16, ImageDescriptor.createFromFile(Activator.class, "icons/text_align_left.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.align_rigth_16, ImageDescriptor.createFromFile(Activator.class, "icons/text_align_right.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.ldap_server_16, ImageDescriptor.createFromFile(Activator.class, "icons/ldap_server.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.connected_16, ImageDescriptor.createFromFile(Activator.class, "icons/connect.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.disconnected_16, ImageDescriptor.createFromFile(Activator.class, "icons/disconnect.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.ldap_input_16, ImageDescriptor.createFromFile(Activator.class, "icons/ldap_input_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.cartesian_16, ImageDescriptor.createFromFile(Activator.class, "icons/catesian_16.png")); //$NON-NLS-1$ //$NON-NLS-2$		
		reg.put(IconsNames.split_row_16, ImageDescriptor.createFromFile(Activator.class, "icons/split_row_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.small_splash, ImageDescriptor.createFromFile(Activator.class, "icons/small_splash2.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.transtyp_16, ImageDescriptor.createFromFile(Activator.class, "icons/transtyp_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.transtyp_32, ImageDescriptor.createFromFile(Activator.class, "icons/transtyp_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.gid_16, ImageDescriptor.createFromFile(Activator.class, "icons/gid_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.gid_32, ImageDescriptor.createFromFile(Activator.class, "icons/gid_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.scriptSql_16, ImageDescriptor.createFromFile(Activator.class, "icons/script_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.pivot_16, ImageDescriptor.createFromFile(Activator.class, "icons/pivot-16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.pivot_32, ImageDescriptor.createFromFile(Activator.class, "icons/pivot-32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.group_16, ImageDescriptor.createFromFile(Activator.class, "icons/group_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.group_32, ImageDescriptor.createFromFile(Activator.class, "icons/group_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.ldapSynchro_16, ImageDescriptor.createFromFile(Activator.class, "icons/ldapSynchro_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.ldapSynchro_32, ImageDescriptor.createFromFile(Activator.class, "icons/ldapSynchro_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.infobright_32, ImageDescriptor.createFromFile(Activator.class, "icons/infobright_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.infobright_16, ImageDescriptor.createFromFile(Activator.class, "icons/infobright_16.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.scriptSql_32, ImageDescriptor.createFromFile(Activator.class, "icons/script_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.split_row_32, ImageDescriptor.createFromFile(Activator.class, "icons/split_row_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.cartesian_32, ImageDescriptor.createFromFile(Activator.class, "icons/cartesian_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.ldap_input_32, ImageDescriptor.createFromFile(Activator.class, "icons/ldap_input_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.disconnected_16, ImageDescriptor.createFromFile(Activator.class, "icons/disconnect.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.XMLFile_32_input, ImageDescriptor.createFromFile(Activator.class, "icons/XMLFile_32_from.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.sequence_32, ImageDescriptor.createFromFile(Activator.class, "icons/sequence_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.gateway_32, ImageDescriptor.createFromFile(Activator.class, "icons/gateway_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.ranging_32, ImageDescriptor.createFromFile(Activator.class, "icons/ranging_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.geoFilter, ImageDescriptor.createFromFile(Activator.class, "icons/iconfinder_geo_filter.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.geoFilter_32, ImageDescriptor.createFromFile(Activator.class, "icons/geoFilter_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.save_16, ImageDescriptor.createFromFile(Activator.class, "icons/save.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.server_add_16, ImageDescriptor.createFromFile(Activator.class, "icons/server_add.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.repository_server_16, ImageDescriptor.createFromFile(Activator.class, "icons/repository.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.server_database_16, ImageDescriptor.createFromFile(Activator.class, "icons/server_database.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.table_go_16, ImageDescriptor.createFromFile(Activator.class, "icons/table_go.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.table_relationship_16, ImageDescriptor.createFromFile(Activator.class, "icons/table_relationship.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.table_row_insert_16, ImageDescriptor.createFromFile(Activator.class, "icons/table_row_insert.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.table_16, ImageDescriptor.createFromFile(Activator.class, "icons/table.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.file_in_16, ImageDescriptor.createFromFile(Activator.class, "icons/file_in.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.select_all_16, ImageDescriptor.createFromFile(Activator.class, "icons/select_all_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.prevval_16, ImageDescriptor.createFromFile(Activator.class, "icons/previous_val_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.arrow_right_16, ImageDescriptor.createFromFile(Activator.class, "icons/arrow_right.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.fmdt_input_16, ImageDescriptor.createFromFile(Activator.class, "icons/fmdt_input-16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.XMLFile_16, ImageDescriptor.createFromFile(Activator.class, "icons/XMLFile_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.metric_16, ImageDescriptor.createFromFile(Activator.class, "icons/metric_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.lookup_16, ImageDescriptor.createFromFile(Activator.class, "icons/lookup_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.filter_16, ImageDescriptor.createFromFile(Activator.class, "icons/filter_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.add_16, ImageDescriptor.createFromFile(Activator.class, "icons/add_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.del_16, ImageDescriptor.createFromFile(Activator.class, "icons/del_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.sum_16, ImageDescriptor.createFromFile(Activator.class, "icons/sum_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.split_16, ImageDescriptor.createFromFile(Activator.class, "icons/split_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.xls_out_16, ImageDescriptor.createFromFile(Activator.class, "icons/xls_out-16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.calc_16, ImageDescriptor.createFromFile(Activator.class, "icons/calc-16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.merge_16, ImageDescriptor.createFromFile(Activator.class, "icons/merge_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.sort_16, ImageDescriptor.createFromFile(Activator.class, "icons/sort_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.relation_16, ImageDescriptor.createFromFile(Activator.class, "icons/link_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.distinct_16, ImageDescriptor.createFromFile(Activator.class, "icons/distinct_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.start_16, ImageDescriptor.createFromFile(Activator.class, "icons/start.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.stop_16, ImageDescriptor.createFromFile(Activator.class, "icons/stop.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.prepare_16, ImageDescriptor.createFromFile(Activator.class, "icons/prepare.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.parameter_16, ImageDescriptor.createFromFile(Activator.class, "icons/parameter_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.add_parameter_16, ImageDescriptor.createFromFile(Activator.class, "icons/add_parameter_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.update_16, ImageDescriptor.createFromFile(Activator.class, "icons/table_update_16.png")); //$NON-NLS-1$ //$NON-NLS-2$		
		reg.put(IconsNames.scd_16, ImageDescriptor.createFromFile(Activator.class, "icons/scd_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.scd2_16, ImageDescriptor.createFromFile(Activator.class, "icons/scd2_16.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.cube_input_16, ImageDescriptor.createFromFile(Activator.class, "icons/cube_input_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.cube_input_32, ImageDescriptor.createFromFile(Activator.class, "icons/cube_input_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.cube_output_16, ImageDescriptor.createFromFile(Activator.class, "icons/cube_output_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.cube_output_32, ImageDescriptor.createFromFile(Activator.class, "icons/cube_output_32.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.scd2_32, ImageDescriptor.createFromFile(Activator.class, "icons/scd2_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.scd_32, ImageDescriptor.createFromFile(Activator.class, "icons/scd_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.update_32, ImageDescriptor.createFromFile(Activator.class, "icons/table_update_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.sort_32, ImageDescriptor.createFromFile(Activator.class, "icons/sort_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.distinct_32, ImageDescriptor.createFromFile(Activator.class, "icons/distinct_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.merge_32, ImageDescriptor.createFromFile(Activator.class, "icons/merge_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.calc_32, ImageDescriptor.createFromFile(Activator.class, "icons/calc-32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.xls_out_32, ImageDescriptor.createFromFile(Activator.class, "icons/xls_out-32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.split_16, ImageDescriptor.createFromFile(Activator.class, "icons/split_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.split_32, ImageDescriptor.createFromFile(Activator.class, "icons/split_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.sum_32, ImageDescriptor.createFromFile(Activator.class, "icons/sum_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.del_32, ImageDescriptor.createFromFile(Activator.class, "icons/delete_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.xls_32, ImageDescriptor.createFromFile(Activator.class, "icons/xls-32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.filter_32, ImageDescriptor.createFromFile(Activator.class, "icons/filter_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.lookup_32, ImageDescriptor.createFromFile(Activator.class, "icons/lookup_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.metric_32, ImageDescriptor.createFromFile(Activator.class, "icons/metric_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.XMLFile_32, ImageDescriptor.createFromFile(Activator.class, "icons/XMLFile_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.table_go_32, ImageDescriptor.createFromFile(Activator.class, "icons/table_go_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.table_row_insert_32, ImageDescriptor.createFromFile(Activator.class, "icons/table_row_insert_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.file_go_32, ImageDescriptor.createFromFile(Activator.class, "icons/file_go_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.link_32, ImageDescriptor.createFromFile(Activator.class, "icons/link_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.file_in_32, ImageDescriptor.createFromFile(Activator.class, "icons/file_in_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.select_all_32, ImageDescriptor.createFromFile(Activator.class, "icons/select_all_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.prevval_32, ImageDescriptor.createFromFile(Activator.class, "icons/previous_val_32.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.fmdt_input_32, ImageDescriptor.createFromFile(Activator.class, "icons/fmdt_input-32.png")); //$NON-NLS-1$

		reg.put(IconsNames.anonyme_16, ImageDescriptor.createFromFile(Activator.class, "icons/anonyme_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.anonyme_32, ImageDescriptor.createFromFile(Activator.class, "icons/anonyme_32.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.table_go_48, ImageDescriptor.createFromFile(Activator.class, "icons/table_go_48.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.table_row_insert_48, ImageDescriptor.createFromFile(Activator.class, "icons/table_row_insert_48.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.file_go_48, ImageDescriptor.createFromFile(Activator.class, "icons/file_go_48.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.browse_datas_16, ImageDescriptor.createFromFile(Activator.class, "icons/browse_datas.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.structure_16, ImageDescriptor.createFromFile(Activator.class, "icons/structure.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.open_16, ImageDescriptor.createFromFile(Activator.class, "icons/open.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.close_16, ImageDescriptor.createFromFile(Activator.class, "icons/close_16.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.topX_16, ImageDescriptor.createFromFile(Activator.class, "icons/top_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.topX_32, ImageDescriptor.createFromFile(Activator.class, "icons/top_32.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.regex_16, ImageDescriptor.createFromFile(Activator.class, "icons/regex_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.regex_32, ImageDescriptor.createFromFile(Activator.class, "icons/regex_32.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.access_16, ImageDescriptor.createFromFile(Activator.class, "icons/ms_access_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.access_32, ImageDescriptor.createFromFile(Activator.class, "icons/ms_access_32.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.file_folder_16, ImageDescriptor.createFromFile(Activator.class, "icons/file_folder_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.file_folder_32, ImageDescriptor.createFromFile(Activator.class, "icons/file_folder_32.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.surrogate_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.surrogate_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.surrogate_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.surrogate_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.geojson_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.geojson_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.geojson_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.geojson_32)); //$NON-NLS-1$ //$NON-NLS-2$
		
		reg.put(IconsNames.consistency_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.consistency_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.consistency_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.consistency_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.oda_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.oda_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.oda_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.oda_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.cube_dim_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.cube_dim_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.cube_dim_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.cube_dim_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.database_lookup_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.database_lookup_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.database_lookup_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.database_lookup_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.vanillaUser_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.vanillaUser_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.vanillaUser_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.vanillaUser_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.ldapMember_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.ldapMember_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.ldapMember_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.ldapMember_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.simpleoutput_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.simpleoutput_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.simpleoutput_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.simpleoutput_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.vanilla_group_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.vanilla_group_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.vanilla_group_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.vanilla_group_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.vanilla_role_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.vanilla_role_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.vanilla_role_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.vanilla_role_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.file_vcl, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.file_vcl)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.file_vcl_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.file_vcl_32)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.file_vcl, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.file_vcl)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.file_vcl_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.file_vcl_32)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.file_vcl_out, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.file_vcl_out)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.file_vcl_out_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.file_vcl_out_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.dimension, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.dimension)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.dimension_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.dimension_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.filer_dimension, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.filer_dimension)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.filer_dimension_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.filer_dimension_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.dimension, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.dimension)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.dimension_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.dimension_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.kml_input, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.kml_input)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.kml_input_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.kml_input_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.kml_output, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.kml_output)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.kml_output_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.kml_output_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.fd_input, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.fd_input)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.fd_input_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.fd_input_32)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.default_palette, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.default_palette)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.custom_palette, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.custom_palette)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.norparena_adress_input_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.norparena_adress_input_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.norparena_adress_input_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.norparena_adress_input_32)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.norparena_adress_output_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.norparena_adress_output_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.norparena_adress_output_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.norparena_adress_output_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.multi_xls_out_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.multi_xls_out_16)); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put(IconsNames.multi_xls_out_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.multi_xls_out_32)); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put(IconsNames.googleAnalytics_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.googleAnalytics_16)); //$NON-NLS-1$
		reg.put(IconsNames.googleAnalytics_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.googleAnalytics_32)); //$NON-NLS-1$

		reg.put(IconsNames.cassandra_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.cassandra_16)); //$NON-NLS-1$
		reg.put(IconsNames.cassandra_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.cassandra_32)); //$NON-NLS-1$

		reg.put(IconsNames.HBASE_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.HBASE_16)); //$NON-NLS-1$
		reg.put(IconsNames.HBASE_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.HBASE_32)); //$NON-NLS-1$

		reg.put(IconsNames.MONGODB_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.MONGODB_16));//$NON-NLS-1$
		reg.put(IconsNames.MONGODB_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.MONGODB_32));//$NON-NLS-1$

		reg.put(IconsNames.nagios_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.nagios_16)); //$NON-NLS-1$
		reg.put(IconsNames.nagios_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.nagios_32)); //$NON-NLS-1$

		reg.put(IconsNames.CHECK, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.CHECK)); //$NON-NLS-1$
		reg.put(IconsNames.NO_CHECK, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.NO_CHECK)); //$NON-NLS-1$

		reg.put(IconsNames.COMMENT_EXTRACTION_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.COMMENT_EXTRACTION_16)); //$NON-NLS-1$
		reg.put(IconsNames.COMMENT_EXTRACTION_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.COMMENT_EXTRACTION_32)); //$NON-NLS-1$

		reg.put(IconsNames.TRANSFO_NULL_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.TRANSFO_NULL_16)); //$NON-NLS-1$
		reg.put(IconsNames.TRANSFO_NULL_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.TRANSFO_NULL_32)); //$NON-NLS-1$
		reg.put(IconsNames.LOGS_ERROR_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.LOGS_ERROR_16)); //$NON-NLS-1$
		reg.put(IconsNames.SHAPE_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.SHAPE_16)); //$NON-NLS-1$
		reg.put(IconsNames.SHAPE_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.SHAPE_32)); //$NON-NLS-1$

		reg.put(IconsNames.WEB_SERVICE_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.WEB_SERVICE_16)); //$NON-NLS-1$
		reg.put(IconsNames.WEB_SERVICE_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.WEB_SERVICE_32)); //$NON-NLS-1$

		reg.put(IconsNames.WEB_SERVICE_VANILLA_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.WEB_SERVICE_VANILLA_16)); //$NON-NLS-1$
		reg.put(IconsNames.WEB_SERVICE_VANILLA_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.WEB_SERVICE_VANILLA_32)); //$NON-NLS-1$

		reg.put(IconsNames.WEKA_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.WEKA_16)); //$NON-NLS-1$
		reg.put(IconsNames.WEKA_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.WEKA_32)); //$NON-NLS-1$

		reg.put(IconsNames.SQOOP_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.SQOOP_16)); //$NON-NLS-1$
		reg.put(IconsNames.SQOOP_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.SQOOP_32)); //$NON-NLS-1$

		reg.put(IconsNames.VEOLIA_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.VEOLIA_16)); //$NON-NLS-1$
		reg.put(IconsNames.VEOLIA_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.VEOLIA_32)); //$NON-NLS-1$

		reg.put(IconsNames.XML_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.XML_16)); //$NON-NLS-1$
		reg.put(IconsNames.XML_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.XML_32)); //$NON-NLS-1$

		reg.put(IconsNames.TSBN_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.TSBN_16)); //$NON-NLS-1$
		reg.put(IconsNames.TSBN_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.TSBN_32)); //$NON-NLS-1$
	
		reg.put(IconsNames.DataPrep_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.DataPrep_16)); //$NON-NLS-1$
		reg.put(IconsNames.DataPrep_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.DataPrep_32)); //$NON-NLS-1$
	
		reg.put(IconsNames.d4c_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.d4c_16)); //$NON-NLS-1$
		reg.put(IconsNames.d4c_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.d4c_32)); //$NON-NLS-1$
	
		reg.put(IconsNames.ic_pdf_form_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.ic_pdf_form_16)); //$NON-NLS-1$
		reg.put(IconsNames.ic_pdf_form_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.ic_pdf_form_32)); //$NON-NLS-1$
	
		reg.put(IconsNames.geodispatch_16, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.geodispatch_16)); //$NON-NLS-1$
		reg.put(IconsNames.geodispatch_32, ImageDescriptor.createFromFile(Activator.class, "icons/" + IconsNames.geodispatch_32)); //$NON-NLS-1$
	}

	/**
	 * return the EditorInput from the active EditorPart
	 * 
	 * @return
	 */
	public GatewayEditorInput getCurrentInput() {
		return (GatewayEditorInput) getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();

	}

	public void addLoadModelListener(PropertyChangeListener listener) {
		modelLoadListener.addPropertyChangeListener(listener);

	}

	public void fireEventModelOpened(String fileName) {
		modelLoadListener.firePropertyChange(fileName, "", "dd"); //$NON-NLS-1$ //$NON-NLS-2$

	}

	public IRepositoryApi getRepositoryConnection() {
		IRepositoryContext repContext = getSessionSourceProvider().getContext();

		if (repositoryConnection == null && repContext != null) {
			repositoryConnection = new RemoteRepositoryApi(repContext);
		}
		return repositoryConnection;
	}

	public IRepositoryContext getRepositoryContext() {
		return getSessionSourceProvider().getContext();
	}

	public void setRepositoryContext(IRepositoryContext ctx) {
		IRepositoryContext old = getRepositoryContext();
		getSessionSourceProvider().setContext(ctx);
		repositoryConnection = null;

		// notify listeners
		for (IRepositoryContextChangedListener l : repositoryConnectionListeners) {
			l.repositoryContextChanged(old, ctx);
		}
		for (IEditorPart p : getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditors()) {
			if (p.getEditorInput() instanceof GatewayEditorInput) {
				((GatewayEditorInput) p.getEditorInput()).getDocumentGateway().setRepositoryContext(ctx);
			}
		}
	}

	public IMapDefinitionService getMapDefinitionService() throws Exception {
		if (mapDefinitionService == null) {
			try {
				mapDefinitionService = new RemoteMapDefinitionService();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new Exception(Messages.Activator_4 + ex.getMessage(), ex);
			}
		}
		mapDefinitionService.configure(getVanillaContext().getVanillaUrl());
		return mapDefinitionService;
	}

	/**
	 * 
	 * @return All additionalDigesters
	 */
	public static List<TransformationUIExtension> getTransformationExtension() {
		return new ArrayList<TransformationUIExtension>(additionalTransformations);
	}

	/**
	 * add a transformation extension from plugins
	 * 
	 * @param ext
	 */
	private static void addTransformationExtension(TransformationUIExtension ext) {
		boolean exist = false;
		for (TransformationUIExtension x : getTransformationExtension()) {
			if (x.equals(ext)) {
				exist = true;
				break;
			}
		}
		if (!exist) {
			additionalTransformations.add(ext);
		}

	}

	public GatewayEditorInput getCurrentModel() {
		IEditorPart part = getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null) {
			return null;
		}

		if (!(part instanceof GatewayEditorPart)) {
			return null;
		}

		return ((GatewayEditorInput) part.getEditorInput());
	}

	public GatewayEditorPart getCurrentEditor() {
		IEditorPart part = getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null) {
			return null;
		}

		if (!(part instanceof GatewayEditorPart)) {
			return null;
		}

		return (GatewayEditorPart) part;
	}

	public Integer getCurrentModelDirectoryItemId() {
		IEditorPart part = getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null) {
			return null;
		}

		if (!(part instanceof GatewayEditorPart)) {
			return null;
		}
		if (((GatewayEditorInput) part.getEditorInput()).getDirectoryItem() == null) {
			return null;
		}
		return ((GatewayEditorInput) part.getEditorInput()).getDirectoryItem().getId();
	}

	public String getCurrentModelFileName() {
		IEditorPart part = getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null) {
			return null;
		}

		if (!(part instanceof GatewayEditorPart)) {
			return null;
		}
		return ((GatewayEditorInput) part.getEditorInput()).getName();
	}

	public String getCurrentModelXml() {
		IEditorPart part = getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null) {
			return null;
		}

		if (!(part instanceof GatewayEditorPart)) {
			return null;
		}
		try {
			return ((GatewayEditorInput) part.getEditorInput()).getDocumentGateway().getAsFormatedString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public int getRepositoryItemType() {
		return IRepositoryApi.GTW_TYPE;
	}

	public boolean isRepositoryConnectionDefined() {
		return getRepositoryConnection() != null;
	}

	public Object saveCurrentModel() throws Exception {
		ActionSave ac = new ActionSave();
		ac.run();
		return new File(getCurrentInput().getName());
	}

	public void setCurrentModel(String modelObjectXmlDefinition, Integer directoryItemId) throws Exception {
		RepositoryItem item = null;
		if (directoryItemId != null && isConnectedOnRepository()) {
			try {

				item = getRepositoryConnection().getRepositoryService().getDirectoryItem(directoryItemId);
			} catch (Exception ex) {
				throw new Exception(Messages.Activator_0 + ex.getMessage());
			}

		}

		getSessionSourceProvider().setDirectoryItemId(directoryItemId);
		GatewayEditorInput input = new GatewayEditorInput(modelObjectXmlDefinition, item);
		if (input.getName() == null) {
			FileDialog fd = new FileDialog(getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
			fd.setFilterExtensions(new String[] { "*.gateway" }); //$NON-NLS-1$

			String fname = fd.open();
			if (fname == null) {
				MessageDialog.openInformation(getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.Activator_2, Messages.Activator_3);
				return;
			}
			else {
				input.setFile(new File(fname));
				getSessionSourceProvider().setCheckedIn(VersionningManager.getInstance().getCheckoutInfos(fname) != null);
			}
		}

		IWorkbenchPage page = getWorkbench().getActiveWorkbenchWindow().getActivePage();

		ResourceManager mgr = input.getDocumentGateway().getResourceManager();

		// inti the GATEWAY_HOME
		mgr.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_BIGATEWAY_HOME).setValue(Platform.getInstallLocation().getURL().getPath());

		// add the variable TEMP folder

		mgr.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP);
		Variable GATEWAY_TEMP = mgr.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP);
		GATEWAY_TEMP.setValue(Platform.getInstallLocation().getURL().getPath() + "/temp/"); //$NON-NLS-1$

		File f = new File(GATEWAY_TEMP.getValueAsString());
		if (!f.exists()) {

			f.mkdirs();

		}

		ResourceViewPart v = (ResourceViewPart) page.findView(ResourceViewPart.ID);
		if (v != null) {
			v.refresh();
		}

		page.openEditor(input, GatewayEditorPart.ID, false);
		getSessionSourceProvider().setModelOpened(true);
	}

	public void setCurrentModel(InputStream modelObjectXmlDefinition, Integer directoryItemId) throws Exception {
		RepositoryItem item = null;
		if (directoryItemId != null && isConnectedOnRepository()) {
			try {
				item = getRepositoryConnection().getRepositoryService().getDirectoryItem(directoryItemId);
			} catch (Exception ex) {
				throw new Exception(Messages.Activator_4 + ex.getMessage());
			}

		}
		GatewayEditorInput input = new GatewayEditorInput(modelObjectXmlDefinition, item);

		if (input.getName() == null) {
			FileDialog fd = new FileDialog(getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
			fd.setFilterExtensions(new String[] { "*.gateway" }); //$NON-NLS-1$

			String fname = fd.open();
			if (fname == null) {
				MessageDialog.openInformation(getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.Activator_6, Messages.Activator_7);
				return;
			}
			else {
				input.setFile(new File(fname));
				getSessionSourceProvider().setCheckedIn(VersionningManager.getInstance().getCheckoutInfos(fname) != null);
			}
		}

		IWorkbenchPage page = getWorkbench().getActiveWorkbenchWindow().getActivePage();

		ResourceManager mgr = input.getDocumentGateway().getResourceManager();

		// inti the GATEWAY_HOME
		mgr.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_BIGATEWAY_HOME).setValue(Platform.getInstallLocation().getURL().getPath());

		// add the variable TEMP folder

		mgr.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP);
		Variable GATEWAY_TEMP = mgr.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP);
		GATEWAY_TEMP.setValue(Platform.getInstallLocation().getURL().getPath() + "/temp/"); //$NON-NLS-1$

		File f = new File(GATEWAY_TEMP.getValueAsString());
		if (!f.exists()) {

			f.mkdirs();

		}
		getSessionSourceProvider().setDirectoryItemId(directoryItemId);
		getSessionSourceProvider().setModelOpened(true);

		ResourceViewPart v = (ResourceViewPart) page.findView(ResourceViewPart.ID);
		if (v != null) {
			v.refresh();
		}

		page.openEditor(input, GatewayEditorPart.ID, false);

	}

	public void setCurrentModel(Object oldModel) {
		IWorkbenchPage page = getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart p = page.findEditor((IEditorInput) oldModel);
		if (p == null) {
			try {

				page.openEditor((IEditorInput) oldModel, GatewayEditorPart.ID);
				getSessionSourceProvider().setModelOpened(true);
			} catch (PartInitException e) {
				e.printStackTrace();
				MessageDialog.openError(page.getWorkbenchWindow().getShell(), Messages.Activator_8, Messages.Activator_9 + e.getMessage());
			}
		}

	}

	public IVanillaContext getVanillaContext() throws Exception {
		if (getRepositoryContext() == null) {
			throw new Exception(Messages.Activator_10);
		}

		return getRepositoryContext().getVanillaContext();
	}

	@Override
	public void addRepositoryContextChangedListener(IRepositoryContextChangedListener listener) {
		repositoryConnectionListeners.add(listener);

	}

	@Override
	public void removeRepositoryContextChangedListener(IRepositoryContextChangedListener listener) {
		repositoryConnectionListeners.remove(listener);

	}

	public IVanillaAPI getVanillaApi() throws Exception {
		if (getRepositoryContext() == null) {
			throw new Exception(Messages.Activator_10);
		}

		return new RemoteVanillaPlatform(getRepositoryContext().getVanillaContext());
	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.BIG;
	}

	@Override
	public void setCurrentDirectoryItemId(Integer directoryItemId) throws Exception {
		if (getSessionSourceProvider() != null) {
			getSessionSourceProvider().setDirectoryItemId(directoryItemId);
		}
	}

	public Customer getCurrentCustomer() {
		if (customer == null) {
			this.customer = ConfigurationManager.getInstance().getVanillaConfiguration().getCustomer();
		}
		return customer;
	}
	
	public void setOnlyOneLink(boolean onlyOneLink) {
		this.onlyOneLink = onlyOneLink;
	}
	
	public boolean isOnlyOneLink() {
		return onlyOneLink;
	}

	public void setActiveLinkCommand(LinkCommand activeLinkCommand) {
		this.activeLinkCommand = activeLinkCommand;
	}
	
	public LinkCommand getActiveLinkCommand() {
		return activeLinkCommand;
	}
}
