package metadataclient;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import metadata.client.actions.ActionSave;
import metadata.client.i18n.Messages;
import metadata.client.views.ViewDimensionMeasure;
import metadata.client.views.ViewProperties;
import metadata.client.views.ViewTree;
import metadata.client.wizards.connection.WizardConnection;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.metadata.BuilderException;
import bpm.metadata.ConnectionOverridenMetaDataBuilder;
import bpm.metadata.MetaData;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.ConnectionOverridenMetaDataBuilder.DataSourceConnectionOverrider;
import bpm.metadata.digester.MetaDataDigester;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.physical.IConnection;
import bpm.repository.ui.SessionSourceProvider;
import bpm.united.olap.api.communication.IServiceProvider;
import bpm.united.olap.remote.services.RemoteServiceProvider;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IDesignerActivator<MetaData> {

	public static final String REPOSITORY_CONNECTION_EVENT_ID = "repositoryConnection"; //$NON-NLS-1$

	private MetaData model = new MetaData();
	private String fileName = ""; //$NON-NLS-1$
	private boolean changed = false;

	// The plug-in ID
	public static final String PLUGIN_ID = "metadataclient"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private PropertyChangeSupport modelLoadListener = new PropertyChangeSupport(this);

	public static final String FLEX_GENERATION_FILE_PATH = "/resources/flex/data"; //$NON-NLS-1$

	private static Logger logger;
	private IRepositoryApi repositorySock = null;

	public SessionSourceProvider getSessionSourceProvider() {
		ISourceProviderService service = (ISourceProviderService) getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
		return (SessionSourceProvider) service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);

	}

	public IVanillaContext getVanillaContext() throws Exception {
		IRepositoryContext repContext = getSessionSourceProvider().getContext();
		if (repContext == null) {
			throw new Exception(Messages.Activator_2);
		}

		return repContext.getVanillaContext();
	}

	public void setRepositoryContext(IRepositoryContext ctx) {
		getSessionSourceProvider().setContext(ctx);
		repositorySock = null;

		if (ctx != null) {
			ConfigurationManager.getInstance().getVanillaConfiguration().setProperty(VanillaConfiguration.P_VANILLA_URL, ctx.getVanillaContext().getVanillaUrl());
			IServiceProvider remoteServiceProvider = new RemoteServiceProvider();
			remoteServiceProvider.configure(ctx.getVanillaContext());

			UnitedOlapServiceProvider.getInstance().init(remoteServiceProvider.getRuntimeProvider(), remoteServiceProvider.getModelProvider());

		}
	}

	public IRepositoryApi getRepositoryConnection() {
		if (repositorySock == null && getRepositoryContext() != null) {
			IRepositoryContext repContext = getRepositoryContext();
			repositorySock = new RemoteRepositoryApi(repContext);
		}
		return repositorySock;
	}

	/**
	 * The constructor
	 */
	public Activator() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		logger = Logger.getLogger(context.getBundle().getSymbolicName());
		if (!logger.getAllAppenders().hasMoreElements()) {
			logger.addAppender(new ConsoleAppender(new SimpleLayout()));
			logger.addAppender(new FileAppender(new SimpleLayout(), "fmdt.log")); //$NON-NLS-1$
		}

		super.start(context);
		plugin = this;
		try {
			model.getProperties().setVersion(getFeatureVersion());
		} catch (Exception e) {

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
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

	public MetaData getModel() {
		return model;
	}

	private void setModel(MetaData model) {

		modelLoadListener.firePropertyChange(fileName, this.model, model);

		this.model = model;

		try {
			ViewTree v = (ViewTree) getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewTree.ID);
			if (v != null) {
				v.reInit();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		getSessionSourceProvider().setModelOpened(this.model != null);
	}

	/**
	 * return the current opened File
	 * 
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setText("Vanilla Metadata - " + fileName); //$NON-NLS-1$
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		String root = Platform.getInstallLocation().getURL().getPath();

		reg.put("small_splash", ImageDescriptor.createFromFile(Activator.class, "icons/small_splash2.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("bus_table", new Image(Display.getCurrent(), root + "resources/icons/business_table.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("log_table", new Image(Display.getCurrent(), root + "resources/icons/logical_table.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("log_table_filtered", ImageDescriptor.createFromFile(Activator.class, "icons/logical_table_filtered.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("phy_table", new Image(Display.getCurrent(), root + "resources/icons/physical_table.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("open", new Image(Display.getCurrent(), root + "resources/icons/open.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("save", new Image(Display.getCurrent(), root + "resources/icons/save.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("saveas", new Image(Display.getCurrent(), root + "resources/icons/save_as.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("log_column", new Image(Display.getCurrent(), root + "resources/icons/log_column.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("phy_column", new Image(Display.getCurrent(), root + "resources/icons/phy_column.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("relation", new Image(Display.getCurrent(), root + "resources/icons/relation.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("datasource", new Image(Display.getCurrent(), root + "resources/icons/datasource.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("object", new Image(Display.getCurrent(), root + "resources/icons/object.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("package", new Image(Display.getCurrent(), root + "resources/icons/package-16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("lov", new Image(Display.getCurrent(), root + "resources/icons/lov-16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("new", new Image(Display.getCurrent(), root + "resources/icons/new.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("model", new Image(Display.getCurrent(), root + "resources/icons/model.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("delete", new Image(Display.getCurrent(), root + "resources/icons/del.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("browse", new Image(Display.getCurrent(), root + "resources/icons/browse.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("filter", new Image(Display.getCurrent(), root + "resources/icons/filter.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("filter_complex", new Image(Display.getCurrent(), root + "resources/icons/filter_complex.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("filter_sql", new Image(Display.getCurrent(), root + "resources/icons/filter_sql.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("prompt", new Image(Display.getCurrent(), root + "resources/icons/prompt.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("edit", new Image(Display.getCurrent(), "resources/icons/edit_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("up", new Image(Display.getCurrent(), "resources/icons/arrow_up.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("down", new Image(Display.getCurrent(), "resources/icons/arrow_down.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("share", new Image(Display.getCurrent(), "resources/icons/share.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("checkin", new Image(Display.getCurrent(), "resources/icons/checkout.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("checkout", new Image(Display.getCurrent(), "resources/icons/chekin.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("dimension", ImageDescriptor.createFromFile(Activator.class, "icons/dimension.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("script", ImageDescriptor.createFromFile(Activator.class, "icons/script.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("variable", ImageDescriptor.createFromFile(Activator.class, "icons/variable.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("close", ImageDescriptor.createFromFile(Activator.class, "icons/close_16.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("check", ImageDescriptor.createFromFile(Activator.class, "icons/check.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("uncheck", ImageDescriptor.createFromFile(Activator.class, "icons/uncheck.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("browse_col", ImageDescriptor.createFromFile(Activator.class, "icons/browse_col.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("physical_to_business", ImageDescriptor.createFromFile(Activator.class, "icons/table_to_business.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("cube", ImageDescriptor.createFromFile(Activator.class, "icons/cube.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("folder", ImageDescriptor.createFromFile(Activator.class, "icons/folder.png")); //$NON-NLS-1$ //$NON-NLS-2$

		reg.put("dependencies", ImageDescriptor.createFromFile(Activator.class, "icons/gr_rel.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("savedquery", ImageDescriptor.createFromFile(Activator.class, "icons/fmdt_bq_16.png")); //$NON-NLS-1$ //$NON-NLS-2$

	}

	public void setChanged() {
		String s = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().getText();

		if (!s.endsWith("*")) { //$NON-NLS-1$
			s += "*"; //$NON-NLS-1$
			Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setText(s);
		}
		changed = true;

	}

	public boolean hasChanged() {
		return changed;
	}

	public void setSaved() {
		String s = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().getText();

		if (!s.endsWith("*")) { //$NON-NLS-1$
			Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setText(s.substring(0, s.length() - 2));
		}
		changed = false;
	}

	public void registerListeners() {
		ViewTree vTree = (ViewTree) getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewTree.ID);
		ViewProperties vP = (ViewProperties) getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewProperties.ID);

		ViewDimensionMeasure vdm = (ViewDimensionMeasure) getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewDimensionMeasure.ID);

		vP.registerAsListener(vTree.getViewer());
		vP.registerAsListener(vdm.getViewer());
	}

	public void addLoadModelListener(PropertyChangeListener listener) {
		modelLoadListener.addPropertyChangeListener(listener);
	}

	private static String FMDT_API_BUNDLE_NAME = "bpm.metadata.api"; //$NON-NLS-1$

	public static String getFeatureVersion() throws Exception {

		Bundle bundle = Platform.getBundle(FMDT_API_BUNDLE_NAME);

		if (bundle != null) {
			Object o = bundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$
			return (String) o;
		}
		throw new Exception(Messages.Activator_30);
	}

	public MetaData getCurrentModel() {
		return model;
	}

	public Integer getCurrentModelDirectoryItemId() {
		return getSessionSourceProvider().getDirectoryItemId();
	}

	public String getCurrentModelXml() {
		if (model != null) {
			try {
				return model.getXml(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean isRepositoryConnectionDefined() {

		return repositorySock != null;
	}

	public void setCurrentModel(String modelObjectXmlDefinition, Integer directoryItemId) throws Exception {

		MetaDataDigester dig = null;

		try {
			dig = new MetaDataDigester(IOUtils.toInputStream(modelObjectXmlDefinition, "UTF-8"), new MetaDataBuilder(getRepositoryConnection())); //$NON-NLS-1$
			String gName = null;
			try {
				gName = getRepositoryContext().getGroup().getName();
			} catch (Exception ex) {

			}

			setModel(dig.getModel(getRepositoryConnection(), gName));

			getSessionSourceProvider().setDirectoryItemId(directoryItemId);
			if (directoryItemId != null) {
				String itemName = getRepositoryConnection().getRepositoryService().getDirectoryItem(directoryItemId).getItemName();
				String repositoryUrl = getRepositoryConnection().getContext().getRepository().getUrl();

				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setText("Vanilla Metadata - " + itemName + " from " + repositoryUrl); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setText("Vanilla Metadata - " + getModel().getProperties().getName() + "*"); //$NON-NLS-1$ 
			}
			setFileName(""); //$NON-NLS-1$
		} catch (BuilderException ex) {

			if (MessageDialog.openQuestion(getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.Activator_34, Messages.Activator_35)) {

				// List<DataSourceConnectionOverrider> overrider = new
				// ArrayList<DataSourceConnectionOverrider>();
				for (IDataSource ds : ex.getNotRebuildableDataSources()) {
					WizardConnection wiz = new WizardConnection();

					WizardDialog d = new WizardDialog(getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
					if (d.open() == WizardDialog.OK) {
						((AbstractDataSource) ds).setConnection(0, wiz.getConnection());
						// ds.setDefaultConnection(wiz.getConnection());
						// overrider.add(new DataSourceConnectionOverrider(ds,
						// wiz.getConnection(), false));

					}
				}

				MetaDataBuilder builder = null;

				try {
					// builder = new
					// ConnectionOverridenMetaDataBuilder(overrider,
					// getRepositoryConnection());
					// dig.setBuilder(builder);
					MetaData model = dig.getModel(Activator.getDefault().getRepositoryConnection(), null);

					setModel(model);

					String itemName = getRepositoryConnection().getRepositoryService().getDirectoryItem(directoryItemId).getItemName();
					String repositoryUrl = getRepositoryConnection().getContext().getRepository().getUrl();

					getSessionSourceProvider().setDirectoryItemId(directoryItemId);
					if (directoryItemId != null) {
						Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setText("Vanilla Metadata - " + itemName + " from " + repositoryUrl); //$NON-NLS-1$ //$NON-NLS-2$
					}

				} catch (Exception ex2) {
					throw ex2;
				}
			}
			else {
				throw ex;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}

	public void setCurrentModel(InputStream modelObjectXmlDefinition, Integer directoryItemId) throws Exception {
		MetaDataDigester dig = null;
		try {
			dig = new MetaDataDigester(modelObjectXmlDefinition, new MetaDataBuilder(getRepositoryConnection()));
			setModel(dig.getModel(getRepositoryConnection()));
			getSessionSourceProvider().setDirectoryItemId(directoryItemId);
			setFileName(""); //$NON-NLS-1$
		} catch (BuilderException ex) {
			if (MessageDialog.openQuestion(getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.Activator_38, Messages.Activator_39)) {

				// List<DataSourceConnectionOverrider> overrider = new
				// ArrayList<DataSourceConnectionOverrider>();
				for (IDataSource ds : ex.getNotRebuildableDataSources()) {
					WizardConnection wiz = new WizardConnection();

					WizardDialog d = new WizardDialog(getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
					if (d.open() == WizardDialog.OK) {
						((AbstractDataSource) ds).setConnection(0, wiz.getConnection());
						// overrider.add(new DataSourceConnectionOverrider(ds,
						// wiz.getConnection(), false));

					}
				}

				MetaDataBuilder builder = null;
				dig.setBuilder(builder);
				try {
					// builder = new
					// ConnectionOverridenMetaDataBuilder(overrider,
					// getRepositoryConnection());
					// dig.setBuilder(builder);
					MetaData model = dig.getModel(Activator.getDefault().getRepositoryConnection(), null);

					Activator.getDefault().setModel(model);

					getSessionSourceProvider().setDirectoryItemId(directoryItemId);

				} catch (Exception ex2) {
					throw ex2;
				}

			}
			else {
				throw ex;
			}
		} catch (Exception ex) {
			throw ex;
		}

	}

	public Object saveCurrentModel() throws Exception {
		ActionSave s = new ActionSave("save", null); //$NON-NLS-1$
		s.run();
		setSaved();
		return s.getFile();
	}

	public void setCurrentModel(Object model) {
		if (model instanceof MetaData) {
			setModel((MetaData) model);
		}

	}

	public String getCurrentModelFileName() {
		return fileName;
	}

	public int getRepositoryItemType() {
		return IRepositoryApi.FMDT_TYPE;
	}

	public static Logger getLogger() {
		return logger;
	}

	public IRepositoryContext getRepositoryContext() {
		return getSessionSourceProvider().getContext();
	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.FMDT;
	}

	@Override
	public void setCurrentDirectoryItemId(Integer directoryItemId) throws Exception {
		if (getSessionSourceProvider() != null) {
			getSessionSourceProvider().setDirectoryItemId(directoryItemId);
		}
	}

}
