package org.freeolap;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.ISourceProviderService;
import org.fasd.actions.ActionSave;
import org.fasd.i18N.LanguageText;
import org.fasd.inport.DigesterFasd;
import org.fasd.inport.DigesterMondrian;
import org.fasd.inport.DigesterSecurity;
import org.fasd.inport.converter.MondrianToUOlapConverter;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPRelation;
import org.fasd.olap.OLAPSchema;
import org.fasd.preferences.PreferenceConstants;
import org.fasd.security.Security;
import org.fasd.views.CubeView;
import org.fasd.views.DimensionView;
import org.fasd.views.MeasureView;
import org.fasd.views.SQLView;
import org.fasd.views.SecurityView;
import org.fasd.views.XMLAView;
import org.fasd.views.actions.ActionNewSchema;
import org.fasd.views.actions.ActionOpen;
import org.fasd.views.composites.DialogNew;
import org.fasd.xmla.XMLASchema;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.repository.ui.SessionSourceProvider;
import bpm.united.olap.api.communication.IModelService;
import bpm.united.olap.api.communication.IRuntimeService;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;

//import bpm.repository.api.model.RepositoryConnection;

/**
 * The main plugin class to be used in the desktop.
 */
public class FreemetricsPlugin extends AbstractUIPlugin implements Observer, IDesignerActivator<FAModel> {

	private PropertyChangeSupport modelLoadListener = new PropertyChangeSupport(this);

	private IRepositoryApi repositorySock = null;
	private IVanillaAPI vanillaApi = null;

	private RemoteVanillaParameterComponent reportRuntime;

	public RemoteVanillaParameterComponent getReportRuntime() {
		if (reportRuntime == null) {
			reportRuntime = new RemoteVanillaParameterComponent(FreemetricsPlugin.getDefault().getRepositoryContext().getVanillaContext());
		}
		return reportRuntime;
	}

	public void setRepositoryConnection(IRepositoryApi sock) {
		repositorySock = sock;
	}

	// The shared instance.
	private static FreemetricsPlugin plugin;

	private static FAModel model;

	private SQLView sqlview;
	private DimensionView dimview;
	private CubeView cubeview;
	private MeasureView mesview;
	private SecurityView secuView;
	private XMLAView xmlaView;

	private Security security = new Security();

	private String path;
	private boolean mondrianFile = false;
	private boolean docOpened = false;

	private ServiceTracker uOlapRuntimeServiceTracker;
	private ServiceTracker uOlapModelServiceTracker;

	private IModelService uOlapModelService;
	private IRuntimeService uOlapRuntimeService;

	public boolean isDocOpened() {
		return docOpened;
	}

	public void setDocOpened(boolean docOpened) {
		this.docOpened = docOpened;
	}

	/**
	 * The constructor.
	 */
	public FreemetricsPlugin() {
		plugin = this;
		// System.out.println(System.getProperty("user.dir"));
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		uOlapModelServiceTracker = new ServiceTracker(context, IModelService.class.getName(), new ServiceTrackerCustomizer() {

			public void removedService(ServiceReference reference, Object service) {
				if (service == uOlapModelService) {
					uOlapModelService = null;
					UnitedOlapServiceProvider.getInstance().init(uOlapRuntimeService, uOlapModelService);
				}

			}

			public void modifiedService(ServiceReference reference, Object service) {

			}

			public Object addingService(ServiceReference reference) {
				uOlapModelService = (IModelService) context.getService(reference);
				UnitedOlapServiceProvider.getInstance().init(uOlapRuntimeService, uOlapModelService);
				return uOlapModelService;
			}
		});

		uOlapRuntimeServiceTracker = new ServiceTracker(context, IRuntimeService.class.getName(), new ServiceTrackerCustomizer() {

			public void removedService(ServiceReference reference, Object service) {
				if (service == uOlapRuntimeService) {
					uOlapRuntimeService = null;
					UnitedOlapServiceProvider.getInstance().init(uOlapRuntimeService, uOlapModelService);
				}

			}

			public void modifiedService(ServiceReference reference, Object service) {

			}

			public Object addingService(ServiceReference reference) {
				uOlapRuntimeService = (IRuntimeService) context.getService(reference);

				UnitedOlapServiceProvider.getInstance().init(uOlapRuntimeService, uOlapModelService);
				return uOlapRuntimeService;
			}
		});

		uOlapRuntimeServiceTracker.open();
		uOlapModelServiceTracker.open();

	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		uOlapRuntimeServiceTracker.close();
		uOlapModelServiceTracker.close();
		FileWriter fw;
		try {
			fw = new FileWriter("security.xml"); //$NON-NLS-1$
			fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); //$NON-NLS-1$
			String s = FreemetricsPlugin.getDefault().getSecurity().getFAXML();
			fw.write(s);
			fw.close();
		} catch (IOException e) {
			MessageDialog.openError(this.getWorkbench().getActiveWorkbenchWindow().getShell(), LanguageText.FreemetricsPlugin_Error, LanguageText.FreemetricsPlugin_Error_Saving_Security_xml);
			e.printStackTrace();
		}
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static FreemetricsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.freeolap", path); //$NON-NLS-1$
	}

	/**
	 * Launch login popup
	 * 
	 * @param sh
	 *            shell to launch on
	 */
	protected void login(Shell sh) {
		try {
			model = new FAModel(new OLAPSchema(), ""); //$NON-NLS-1$
			model.setSecurity(security);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(sh, LanguageText.FreemetricsPlugin_Error, LanguageText.FreemetricsPlugin_Unable_to_Load_Security);
		}

		IPreferenceStore store = this.getPreferenceStore();
		if (!store.getBoolean(PreferenceConstants.P_SHOWNEWATSTARTUP))
			return;

		DialogNew first = new DialogNew(sh);

		if (first.open() == Window.OK) {
			if (first.getAction() == 0) { // new project
				FreemetricsPlugin.getDefault().setMondrianImport(false);
				new ActionNewSchema(sh, true).run();
			}
			else if (first.getAction() == 1) { // existing project
				Shell s = new Shell(sh);
				FileDialog dd = new FileDialog(s);
				dd.setFilterExtensions(new String[] { "*.fasd" }); //$NON-NLS-1$
				dd.setText(LanguageText.FreemetricsPlugin_Opening_Exsisting_FA_Proj);

				path = dd.open();
				new ActionOpen(path).run();

			}
			else if (first.getAction() == 2) {// open recent file
				path = first.getPath();
				FreemetricsPlugin.getDefault().setMondrianImport(false);
				new ActionOpen(path).run();
			}
			else if (first.getAction() == 3) { // import mondrian
				Shell s = new Shell(sh);
				FileDialog dd = new FileDialog(s);
				dd.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$
				dd.setText(LanguageText.FreemetricsPlugin_Choose_Mondrian_Schema);

				this.setMondrianImport(true);

				path = dd.open();
				if (path != null) {
					try {
						importMondrian(path);

						Shell parent = this.getWorkbench().getActiveWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell();
						parent.setText("Free Analysis Schema Designer - " + path); //$NON-NLS-1$
						setDocOpened(true);

						// detect presence of calculater column inside
						// dimension's levels
						boolean calc = false;
						StringBuffer buf = new StringBuffer();
						for (OLAPDimension d : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getDimensions()) {
							for (OLAPHierarchy h : d.getHierarchies()) {
								for (OLAPLevel l : h.getLevels()) {
									if (l.getItem() == null && l.getKeyExpressions().size() > 0)
										calc = true;
								}
							}
						}

						if (calc) {
							buf.append("\n\n The Mondrian Schema contains some calculated Columns inside Dimension's Levels. Those columns are not created inside  the FASD DataSource, but you can create them choosing the add Column button when you select � Table in the Dummy's DataSource."); //$NON-NLS-1$
						}

						MessageDialog.openInformation(s, LanguageText.FreemetricsPlugin_Warning, LanguageText.FreemetricsPlugin_DummyDs + buf.toString());

					} catch (FileNotFoundException ex) {
						ex.printStackTrace();
						MessageDialog.openError(s, LanguageText.FreemetricsPlugin_Error, LanguageText.FreemetricsPlugin_Opening_Failed_ + ex.getMessage());
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageDialog.openError(s, LanguageText.FreemetricsPlugin_Error, LanguageText.FreemetricsPlugin_Parsing_Failed_ + ex.getMessage());
					}
				}
			}
			else if (first.getAction() == 4) {
				try {
					FAModel m = new FAModel();
					m.setXMLASchema(new XMLASchema());
					setFAModel(m);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			else {
				MessageDialog.openInformation(sh, LanguageText.FreemetricsPlugin_Warning, LanguageText.FreemetricsPlugin_Under_Implementation);
			}

		}
	}

	/**
	 * Main(& only) entry point to get internal representation
	 * 
	 * @return FAModel, summary of current model
	 */
	public FAModel getFAModel() {
		return model;
	}

	public void setFAModel(FAModel model) {

		FreemetricsPlugin.model.deleteObservers();

		modelLoadListener.firePropertyChange(path, this.model, model);
		setDocOpened(model != null);
		FreemetricsPlugin.model = model;
		model.setSecurity(security);
		model.addObserver(this);

		// if we load an xmla schema
		if (model.getOLAPSchema() == null && model.getXMLASchema() != null) {
			IPerspectiveDescriptor p = this.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.fasd.xmlaperspective"); //$NON-NLS-1$
			this.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(p);
			if (xmlaView != null)
				xmlaView.refresh();
			return;
		}
		// else open the standard perspective
		if (!this.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId().equals("org.fasd.xmlaperspective")) { //$NON-NLS-1$
			IPerspectiveDescriptor p = this.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.freeolap.perspective"); //$NON-NLS-1$
			this.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(p);
		}

		try {
			if (sqlview != null) {
				sqlview.registerObservable(model.getListDataSource());
				sqlview.registerObservable(model.getListRelations());
				sqlview.refresh(false);
			}
			if (dimview != null) {
				dimview.registerObservable(model.getOLAPSchema().getListDimensions());
				dimview.registerObservable(model.getOLAPSchema().getListDimGroup());
				dimview.refreshWithExpand();
			}
			if (cubeview != null) {
				cubeview.registerObservable(model.getOLAPSchema().getListCube());
				cubeview.registerObservable(model.getOLAPSchema().getListVirtualCube());
				cubeview.refresh();
			}
			if (mesview != null) {
				mesview.registerObservable(model.getOLAPSchema().getListMeasures());
				mesview.registerObservable(model.getOLAPSchema().getListMesGroup());
				mesview.refresh();
			}
			if (secuView != null) {
				secuView.registerObservable(model);
				secuView.refresh();
			}
		} catch (Exception e) {
			MessageDialog.openInformation(sqlview.getSite().getShell(), LanguageText.FreemetricsPlugin_Advertissement, LanguageText.FreemetricsPlugin_Schema_Contains_errors);
			e.printStackTrace();
		}

		getSessionSourceProvider().setModelOpened(getCurrentModel() != null);
	}

	public void loadSecurity() throws Exception {
		DigesterSecurity dig = new DigesterSecurity();
		security = dig.getSecurity();
	}

	public void registerSQLView(SQLView v) {
		sqlview = v;
	}

	public void registerDimensionView(DimensionView v) {
		dimview = v;
	}

	public void registerCubeView(CubeView v) {
		cubeview = v;
	}

	public void registerMeasureView(MeasureView v) {
		mesview = v;
	}

	protected void importMondrian(String path) throws FileNotFoundException, Exception {
		System.out.println("Importing mondrian schema : " + path); //$NON-NLS-1$
		model = new FAModel();
		DigesterMondrian dig = new DigesterMondrian(path);
		System.out.println("Found nb dims : " + dig.getModel().getOLAPSchema().getDimensions().size()); //$NON-NLS-1$
		model = dig.getModel();
		model.addDataSource(dig.getDataSource());
		for (OLAPRelation r : dig.getRelations()) {
			model.addRelation(r);
		}
		setFAModel(model);
		// clean();
		System.out.println("Done importing"); //$NON-NLS-1$

		dimview.refresh();
		mesview.refresh();
		cubeview.refresh();
	}

	public void update(Observable arg0, Object arg1) {
		updateTitle();
	}

	private void updateTitle() {
		Shell parent = this.getWorkbench().getActiveWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell();
		parent.setText("Free Analysis Schema Designer - *" + path); //$NON-NLS-1$
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isMondrianImport() {
		return mondrianFile;
	}

	public void setMondrianImport(boolean value) {
		mondrianFile = value;
	}

	public void registerSecurityView(SecurityView view) {
		secuView = view;
	}

	public void registerXMLAView(XMLAView view) {
		xmlaView = view;
	}

	public Security getSecurity() {
		return security;
	}

	public void refreshSQLView() {
		sqlview.refresh(true);
	}

	public IRepositoryApi getRepositoryConnection() {
		IRepositoryContext repContext = getRepositoryContext();
		if (repositorySock == null && repContext != null) {
			repositorySock = new RemoteRepositoryApi(repContext);
		}
		return repositorySock;
	}

	public IVanillaAPI getVanillaApi() {
		if (vanillaApi == null && getRepositoryContext() != null) {
			IRepositoryContext repContext = getRepositoryContext();
			vanillaApi = new RemoteVanillaPlatform(repContext.getVanillaContext().getVanillaUrl(), repContext.getVanillaContext().getLogin(), repContext.getVanillaContext().getPassword());
		}
		return vanillaApi;
	}

	public void addLoadModelListener(PropertyChangeListener listener) {
		modelLoadListener.addPropertyChangeListener(listener);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);

		reg.put("small_splash", ImageDescriptor.createFromFile(FreemetricsPlugin.class, "icons/small_splash2.png")); //$NON-NLS-1$ //$NON-NLS-2$
		reg.put("coloring", ImageDescriptor.createFromFile(FreemetricsPlugin.class, "icons/palette_16.png")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public FAModel getCurrentModel() {
		return model;
	}

	public Integer getCurrentModelDirectoryItemId() {
		return getSessionSourceProvider().getDirectoryItemId();
	}

	public String getCurrentModelFileName() {
		return path;
	}

	public String getCurrentModelXml() {
		return model.getFAXML();
	}

	public int getRepositoryItemType() {
		return IRepositoryApi.FASD_TYPE;
	}

	public boolean isRepositoryConnectionDefined() {
		return repositorySock != null;
	}

	public Object saveCurrentModel() throws Exception {
		ActionSave as = new ActionSave(getWorkbench().getActiveWorkbenchWindow().getShell());
		as.run();
		return as.getFile();
	}

	public void setCurrentModel(String modelObjectXmlDefinition, Integer directoryItemId) throws Exception {

		boolean isBefore40 = false;

		try {
			Document doc = DocumentHelper.parseText(modelObjectXmlDefinition);
			String version = doc.getRootElement().element("version").getStringValue(); //$NON-NLS-1$
			if (4.0 > Float.parseFloat(version)) {
				isBefore40 = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		getSessionSourceProvider().setDirectoryItemId(directoryItemId);

		DigesterFasd dig = new DigesterFasd(IOUtils.toInputStream(modelObjectXmlDefinition, "UTF-8")); //$NON-NLS-1$
		if (isBefore40) {
			setFAModel(MondrianToUOlapConverter.convertFromMondrianToUOlap(dig.getFAModel()));
			openRepositoryUpdateDialog();
		}
		else {
			setFAModel(dig.getFAModel());
		}

	}

	public void setCurrentModel(InputStream modelObjectXmlDefinition, Integer directoryItemId) throws Exception {
		String xml = IOUtils.toString(modelObjectXmlDefinition, "UTF-8"); //$NON-NLS-1$

		boolean isBefore40 = false;

		try {
			Document doc = DocumentHelper.parseText(xml);
			String version = doc.getRootElement().element("version").getStringValue(); //$NON-NLS-1$
			if (4.0 > Float.parseFloat(version)) {
				isBefore40 = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		getSessionSourceProvider().setDirectoryItemId(directoryItemId);

		DigesterFasd dig = new DigesterFasd(modelObjectXmlDefinition);
		if (isBefore40) {
			setFAModel(MondrianToUOlapConverter.convertFromMondrianToUOlap(dig.getFAModel()));
			openRepositoryUpdateDialog();
		}
		else {
			setFAModel(dig.getFAModel());
		}

	}

	public void openRepositoryUpdateDialog() {
		if (MessageDialog.openQuestion(getWorkbench().getActiveWorkbenchWindow().getShell(), LanguageText.FreemetricsPlugin_4, LanguageText.FreemetricsPlugin_5 + LanguageText.FreemetricsPlugin_6 + LanguageText.FreemetricsPlugin_7 + LanguageText.FreemetricsPlugin_8)) {
			try {
				repositorySock.getRepositoryService().updateModel(repositorySock.getRepositoryService().getDirectoryItem(getCurrentModelDirectoryItemId()), model.getFAXML());
			} catch (Exception e) {
				MessageDialog.openError(getWorkbench().getActiveWorkbenchWindow().getShell(), LanguageText.FreemetricsPlugin_9, LanguageText.FreemetricsPlugin_10 + e.getMessage());
				e.printStackTrace();
			}
		}

	}

	public void setCurrentModel(Object oldModel) {
		if (oldModel instanceof FAModel) {
			setFAModel((FAModel) oldModel);

		}

	}

	public void setRepositoryContext(IRepositoryContext ctx) {
		getSessionSourceProvider().setContext(ctx);
		repositorySock = null;
	}

	public SessionSourceProvider getSessionSourceProvider() {
		ISourceProviderService service = (ISourceProviderService) getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
		return (SessionSourceProvider) service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);

	}

	public IRepositoryContext getRepositoryContext() {
		return getSessionSourceProvider().getContext();
	}

	public IModelService getModelService() {
		return uOlapModelService;
	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.FASD;
	}

	@Override
	public void setCurrentDirectoryItemId(Integer directoryItemId) throws Exception {
		if (getSessionSourceProvider() != null) {
			getSessionSourceProvider().setDirectoryItemId(directoryItemId);
		}
	}

}
