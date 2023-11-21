package bpm.sqldesigner.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.framework.BundleContext;

import bpm.repository.ui.SessionSourceProvider;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.xml.SaveData;
import bpm.sqldesigner.ui.internal.Workspace;
import bpm.sqldesigner.ui.repositoryimport.ImportHelper;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditorInput;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IDesignerActivator<DocumentSnapshot> {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.sqldesigner.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private IRepositoryApi repositoryConnection;

	private Workspace workspace;
	
	private PropertyChangeSupport workspaceListener = new PropertyChangeSupport(this);

	/**
	 * The constructor
	 */
	public Activator() {
		bpm.sqldesigner.api.database.JdbcConnectionProvider.init(Platform.getInstallLocation().getURL().getPath() 
				+ "/resources/jdbc/", "resources/driverjdbc.xml"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	
	public void addWorkspaceListener(PropertyChangeListener listener){
		workspaceListener.addPropertyChangeListener(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public Workspace getWorkspace(){
		return workspace;
	}
	
	public void setWorkspace(Workspace wks){
		Workspace old = workspace;
		if (old != null){
			old.close();
		}
		workspace = wks;
		workspaceListener.firePropertyChange("workspace", old, workspace); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		reg.put("catalog", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/catalog.png")); //$NON-NLS-1$
		reg.put("table", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/table.png")); //$NON-NLS-1$
		reg.put("databases", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/databases.png")); //$NON-NLS-1$
		reg.put("databasesNew", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/databasesNew.png")); //$NON-NLS-1$
		reg.put("view", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/view.png")); //$NON-NLS-1$
		reg.put("key", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/key.png")); //$NON-NLS-1$
		reg.put("open", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/open.png")); //$NON-NLS-1$
		reg.put("schema", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/schema.png")); //$NON-NLS-1$
		reg.put("photo", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/photo.png")); //$NON-NLS-1$
		reg.put("databasesMP", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/databasesMP.png")); //$NON-NLS-1$
		reg.put("databasesLoading", ImageDescriptor.createFromFile( //$NON-NLS-1$
				Activator.class, "icons/databasesLoading.png")); //$NON-NLS-1$
		reg.put("folder", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/folder.png")); //$NON-NLS-1$
		reg.put("compare", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/compare.png")); //$NON-NLS-1$
		reg.put("column", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/column.png")); //$NON-NLS-1$
		reg.put("execute_sql", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/execute_sql.png")); //$NON-NLS-1$
		reg.put("procedure", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/procedure.png")); //$NON-NLS-1$
		reg.put("procedure", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/procedure.png")); //$NON-NLS-1$
		reg.put("zoomIn", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/zoom_in.png")); //$NON-NLS-1$
		reg.put("zoomOut", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/zoom_out.png")); //$NON-NLS-1$
		reg.put("schemaSave", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/schema_save.png")); //$NON-NLS-1$
		reg.put("databaseSave", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/database_save.png")); //$NON-NLS-1$
		reg.put("schemaMP", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/schemaMP.png")); //$NON-NLS-1$
		reg.put("save", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/save.png")); //$NON-NLS-1$
		reg.put("close", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/close.png")); //$NON-NLS-1$
		reg.put("recent", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/hourglass.png")); //$NON-NLS-1$
		reg.put("png", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/png.png")); //$NON-NLS-1$
		reg.put("search", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/search.png")); //$NON-NLS-1$
		reg.put("list", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
				"icons/list.png")); //$NON-NLS-1$
		reg.put("small_splash", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
		"icons/small_splash2.png")); //$NON-NLS-1$
		reg.put("new", ImageDescriptor.createFromFile(Activator.class, //$NON-NLS-1$
		"icons/new.png")); //$NON-NLS-1$
		
		super.initializeImageRegistry(reg);
	}

	
	public List<IEditorReference> getEditorsFor(Class<? extends IEditorInput> c){
		List<IEditorReference> l = new ArrayList<IEditorReference>();
		
		for(IEditorReference ref : getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()){
			
			
			try{
				if (c.isAssignableFrom(ref.getEditorInput().getClass())){
					l.add(ref);
				}
			}catch(Exception ex){
				
			}
			
		}
		
		
		return l;
	}

	public void addLoadModelListener(PropertyChangeListener listener) {

	}
	
	public void refreshButton(){
		getCurrentModel();
	}

	public DocumentSnapshot getCurrentModel() {
		IEditorPart part = getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (part == null){
			getSessionSourceProvider().setModelOpened(false);
			return null;
		}
		
		if (!(part.getEditorInput() instanceof SnapshotEditorInput)){
			getSessionSourceProvider().setModelOpened(false);
			return null;
		}
		
		getSessionSourceProvider().setModelOpened(true);
		
		return ((SnapshotEditorInput)part.getEditorInput()).getSnapshot();
	}

	public Integer getCurrentModelDirectoryItemId() {
		return null;
	}

	public String getCurrentModelFileName() {
		return null;
	}

	public String getCurrentModelXml() {
		try {
			return SaveData.saveSnapshotAsXml("UTF-8", getCurrentModel(), SaveData.SAVE_LAYOUT); //$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public  IRepositoryApi getRepositoryConnection(){
		IRepositoryContext repContext = getSessionSourceProvider().getContext();
		
		if (repositoryConnection == null && repContext != null){
			repositoryConnection = new RemoteRepositoryApi(repContext);
		}
		return repositoryConnection;
	}

	public int getRepositoryItemType() {
		return IRepositoryApi.DWH_VIEW_TYPE;
	}

	public boolean isRepositoryConnectionDefined() {
		return getRepositoryConnection() != null;
	}

	public Object saveCurrentModel() throws Exception {
		return null;
	}

	public void setCurrentModel(String modelObjectXmlDefinition, Integer directoryItemId) throws Exception {
		ImportHelper.importDwhView(modelObjectXmlDefinition);
		
		getSessionSourceProvider().setModelOpened(true);
	}

	public void setCurrentModel(InputStream modelObjectXmlDefinition, Integer directoryItemId) throws Exception {

	}

	public void setCurrentModel(Object oldModel) {

	}

//	public void setRepositoryConnection(IRepositoryApi sock) {
//		this.repositoryConnection = sock;
//		
//	}


	@Override
	public IRepositoryContext getRepositoryContext() {
		return getSessionSourceProvider().getContext();
	}
	
	public SessionSourceProvider getSessionSourceProvider() {
		ISourceProviderService service = (ISourceProviderService) getWorkbench()
				.getActiveWorkbenchWindow().getService(
						ISourceProviderService.class);
		return (SessionSourceProvider) service
				.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);
	}


	@Override
	public void setRepositoryContext(IRepositoryContext ctx) {
		getSessionSourceProvider().setContext(ctx);
		repositoryConnection = null;
	}


	@Override
	public String getApplicationId() {
		return IRepositoryApi.DWH;
	}


	@Override
	public void setCurrentDirectoryItemId(Integer directoryItemId) throws Exception { }
	
}
