package bpm.fd.repository.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.framework.BundleContext;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectRepositoryDescriptor;
import bpm.fd.api.core.model.tools.ModelLoader;
import bpm.fd.design.ui.editors.FdEditor;
import bpm.fd.design.ui.editors.FdProjectEditorInput;
import bpm.fd.repository.ui.icons.Icons;
import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.designer.ui.common.ICheckoutReleaser;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IDesignerActivator<FdProject>, ICheckoutReleaser{

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.fd.repository.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	

	private IRepositoryApi repositoryConnection = null;
	private PropertyChangeSupport modelLoadListener = new PropertyChangeSupport(this);	
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

	private SessionSourceProvider getSessionSourceProvider() {
		ISourceProviderService service = (ISourceProviderService) getWorkbench()
				.getActiveWorkbenchWindow().getService(
						ISourceProviderService.class);
		return (SessionSourceProvider) service
				.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);

	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		for(Field f : Icons.class.getFields()){
			
			try {
				reg.put((String)f.get(null), ImageDescriptor.createFromFile(Activator.class, "icons/" + f.get(null))); //$NON-NLS-1$
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	
	public void fireModelClosed(String fileName, Object fdProject){
		modelLoadListener.firePropertyChange(fileName, null, fdProject);
	}

	public void addLoadModelListener(PropertyChangeListener listener) {
		modelLoadListener.addPropertyChangeListener(listener);
		
		
	}

	public FdProject getCurrentModel() {
		IEditorPart editorPart = getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (!(editorPart instanceof FdEditor)){
			return null;
		}
		
		
		FdProjectEditorInput input = (FdProjectEditorInput)editorPart.getEditorInput();
		FdProject fdProject = input.getModel().getProject();
		
	
		
		return fdProject;
	}

	public Integer getCurrentModelDirectoryItemId() {
		if (getCurrentModel() != null){
			if (getCurrentModel().getProjectDescriptor() instanceof FdProjectRepositoryDescriptor){
				return ((FdProjectRepositoryDescriptor)getCurrentModel().getProjectDescriptor()).getModelDirectoryItemId();
			}
		}
		return null;
	}

	public String getCurrentModelFileName() {
		
		return null;
	}

	public String getCurrentModelXml() {
		
		return null;
	}

	public IRepositoryContext getRepositoryContext() {
		return getSessionSourceProvider().getContext();
	}

	public int getRepositoryItemType() {
		return IRepositoryApi.FD_TYPE;
	}

	public boolean isRepositoryConnectionDefined() {
		return repositoryConnection != null;
	}

	public Object saveCurrentModel() throws Exception {
		
		return null;
	}

	public void setCurrentModel(String modelObjectXmlDefinition, Integer directoryItemId) throws Exception {
		
		
	}

	public void setCurrentModel(InputStream modelObjectXmlDefinition,
			Integer directoryItemId) throws Exception {
		
		
	}

	public void setCurrentModel(Object oldModel) {
		
		
	}


	public  IRepositoryApi getRepositoryConnection(){
		if (repositoryConnection == null && getRepositoryContext() != null){
			IRepositoryContext repContext = getRepositoryContext();
			repositoryConnection = new RemoteRepositoryApi(repContext);
		}
		return repositoryConnection;
	}
	
	
	public  void setRepositoryContext(IRepositoryContext ctx){
		getSessionSourceProvider().setContext(ctx);
		repositoryConnection = null;
		if (ctx != null){
			ConfigurationManager.getInstance().getVanillaConfiguration().setProperty(VanillaConfiguration.P_VANILLA_URL, 
					ctx.getVanillaContext().getVanillaUrl());
		}
	}

	public void checkin(Object model) throws Exception {
		if (! (model instanceof FdProject)){
			throw new Exception(Messages.Activator_2);
		}
		
		
		ModelLoader.checkinProject(getRepositoryConnection(), ((FdProjectRepositoryDescriptor)((FdProject)model).getProjectDescriptor()).getModelDirectoryItemId(), (FdProject)model);
		
		
	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.FD;
	}

	@Override
	public void setCurrentDirectoryItemId(Integer directoryItemId) throws Exception { }
}
