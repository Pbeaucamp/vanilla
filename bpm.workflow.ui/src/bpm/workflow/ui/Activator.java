package bpm.workflow.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Locale;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.framework.BundleContext;

import bpm.fa.api.olap.unitedolap.UnitedOlapLoader;
import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.fa.api.repository.FaApiHelper;
import bpm.repository.ui.SessionSourceProvider;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.united.olap.remote.services.RemoteServiceProvider;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.FMContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.ui.editors.WorkflowEditorInput;
import bpm.workflow.ui.editors.WorkflowMultiEditorPart;
import bpm.workflow.ui.gef.commands.LinkCommand;
import bpm.workflow.ui.icons.IconsNames;
import bpm.workflow.ui.views.ResourceViewPart;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IDesignerActivator {

	public static final String PLUGIN_ID = "bpm.workflow.ui"; //$NON-NLS-1$

	private static Activator plugin;
	private char[] autoActivationCharacters;
	private KeyStroke keyStroke = null;

	private IRepositoryApi sock;
	private IRepository repository;
	private RepositoryItem currentDirect;

	private PropertyChangeSupport modelLoadListener = new PropertyChangeSupport(this);

	private VanillaConfiguration config;
	private FMContext fmContext;

	private RemoteSmartManager remoteSmart;
	private FaApiHelper faHelper;

	private RemoteVanillaPlatform vanillaApi;
	
	//This boolean is use when the user select link from the context menu and we need to revert to select after the link
	private boolean onlyOneLink;
	
	//This command is use when the user select link from the context menu
	private LinkCommand activeLinkCommand;

	public char[] getAutoActivationCharacters() {
		return autoActivationCharacters;
	}

	public void setAutoActivationCharacters(char[] chars) {
		this.autoActivationCharacters = chars;
	}

	public KeyStroke getKeyStroke() {
		return keyStroke;
	}

	public void setKeyStroke(KeyStroke key) {
		this.keyStroke = key;
	}

	/**
	 * 
	 * @return the Idirectoryitem of last uploaded model
	 */
	public RepositoryItem getCurrentDirect() {
		return currentDirect;
	}

	/**
	 * Set the Idirectoryitem of the current model
	 * 
	 * @param currentDirect
	 */
	public void setCurrentDirect(RepositoryItem currentDirect) {
		this.currentDirect = currentDirect;
	}

	public IVanillaContext getVanillaContext() throws Exception {
		if(getRepositoryContext() == null) {
			throw new Exception(Messages.Activator_10);
		}

		return getRepositoryContext().getVanillaContext();
	}

	public IVanillaAPI getVanillaApi() throws Exception {
		if(vanillaApi == null ) {
			IVanillaContext ctx = getVanillaContext();

			vanillaApi = new RemoteVanillaPlatform(ctx);

			
		}
		return vanillaApi;
	}

	public String getSession() throws Exception {
		IVanillaContext ctx = getVanillaContext();
		RemoteAdminManager admin = new RemoteAdminManager(ctx.getVanillaUrl(), null, Locale.getDefault());
		User u = getVanillaApi().getVanillaSecurityManager().authentify("", ctx.getLogin(), ctx.getPassword(), false); //$NON-NLS-1$
		String session = admin.connect(u);
//		System.out.println(session);
		return session;
	}
	
	/**
	 * 
	 * @return the current connected repository
	 */
	public IRepository getRepository() throws Exception {
		if(sock == null) {
			throw new Exception(Messages.Activator_0);
		}
		if(repository == null) {
			try {
				repository = new Repository(sock);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return repository;
	}

	/**
	 * Set the current connected repository
	 * 
	 * @param repository
	 */
	public void setRepository(IRepository repository) {
		this.repository = repository;
	}

	/**
	 * The constructor
	 */
	public Activator() {

		autoActivationCharacters = new char[] { '{' };
		config = ConfigurationManager.getInstance().getVanillaConfiguration();

		try {
			keyStroke = KeyStroke.getInstance("Ctrl+Space"); //$NON-NLS-1$
		} catch(ParseException e1) {
			e1.printStackTrace();
		}
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
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);

		for(Field f : IconsNames.class.getFields()) {
			try {
				reg.put(f.get(IconsNames.class).toString(), ImageDescriptor.createFromFile(Activator.class, f.get(IconsNames.class).toString())); //$NON-NLS-1$ //$NON-NLS-2$
			} catch(IllegalArgumentException e) {
				e.printStackTrace();
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}

		}

	}

	public WorkflowEditorInput getCurrentInput() {
		try {
			return (WorkflowEditorInput) getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
		} catch(Exception e) {
			return null;
		}
	}

	public void addLoadModelListener(PropertyChangeListener listener) {
		modelLoadListener.addPropertyChangeListener(listener);

	}

	public void fireEventModelOpened(String fileName) {
		modelLoadListener.firePropertyChange(fileName, "", "dd"); //$NON-NLS-1$ //$NON-NLS-2$

	}

	public String getModelXml() throws Exception {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			((WorkflowModel) getCurrentModel()).saveToXml(b);
			String s = b.toString(); //$NON-NLS-1$
			b.close();
			return s;
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(Messages.Activator_4 + e.getMessage());
		}

	}

	public Object getCurrentModel() {
		if(getCurrentInput() != null) {
			return getCurrentInput().getWorkflowModel();
		}
		else {
			return null;
		}
	}

	public Integer getCurrentModelDirectoryItemId() {
		if(getCurrentInput() != null) {
			return getCurrentInput().getDirectoryItemId();
		}
		else {
			return null;
		}
	}

	public String getCurrentModelFileName() {
		return getCurrentInput().getFileName();
	}

	public String getCurrentModelXml() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			((WorkflowModel) getCurrentModel()).saveToXml(bos);
			return bos.toString("UTF-8"); //$NON-NLS-1$
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public IRepositoryApi getRepositoryConnection() {
		IRepositoryContext repContext = getRepositoryContext();
		if(sock == null && repContext != null) {
			sock = new RemoteRepositoryApi(repContext);
		}
		return sock;
	}

	public IRepositoryContext getRepositoryContext() {
		return getSessionSourceProvider().getContext();
	}

	public void setRepositoryContext(IRepositoryContext ctx) {
		getSessionSourceProvider().setContext(ctx);
		sock = null;
		if(ctx == null) {
			sock = null;
		}

		else {
			sock = new RemoteRepositoryApi(ctx);
		}
	}

	public SessionSourceProvider getSessionSourceProvider() {
		ISourceProviderService service = (ISourceProviderService) getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
		return (SessionSourceProvider) service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);

	}

	public int getRepositoryItemType() {
		return IRepositoryApi.BIW_TYPE;
	}

	public boolean isRepositoryConnectionDefined() {
		return sock != null;
	}

	public Object saveCurrentModel() throws Exception {
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

		WorkflowEditorInput doc = Activator.getDefault().getCurrentInput();

		String path = null;
		if(doc == null || doc.getFileName() == null || doc.getFileName().equals("")) { //$NON-NLS-1$
			FileDialog fd = new FileDialog(sh, SWT.SAVE);
			path = fd.open();

		}
		else {
			path = doc.getFileName();
		}

		if(path != null) {
			try {
				if(!path.endsWith(".biw")) { //$NON-NLS-1$
					path += ".biw"; //$NON-NLS-1$
				}
				FileOutputStream fos = new FileOutputStream(path);
				doc.getWorkflowModel().saveToXml(fos);
				doc.setFileName(path);
				return new File(path);

			} catch(Exception e) {
				e.printStackTrace();
				MessageDialog.openError(sh, Messages.Activator_9, Messages.Activator_10 + e.getMessage());
			}

		}
		return null;
	}

	public void setCurrentModel(Object oldModel) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			page.openEditor(new WorkflowEditorInput(null, (WorkflowModel) oldModel), WorkflowMultiEditorPart.ID, true);
			getSessionSourceProvider().setModelOpened(oldModel != null);
			ResourceViewPart v = (ResourceViewPart) page.findView(ResourceViewPart.ID);
			if(v != null) {
				v.refresh();
			}
		} catch(PartInitException e) {
			e.printStackTrace();
		}

	}

	public void setCurrentModel(String modelObjectXmlDefinition, Integer directoryItemId) throws Exception {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		getSessionSourceProvider().setDirectoryItemId(directoryItemId);
		page.openEditor(new WorkflowEditorInput(modelObjectXmlDefinition, directoryItemId), WorkflowMultiEditorPart.ID, true);
		getSessionSourceProvider().setModelOpened(true);
		ResourceViewPart v = (ResourceViewPart) page.findView(ResourceViewPart.ID);
		if(v != null) {
			v.refresh();
		}

	}

	public void setRepositoryConnection(IRepositoryApi sock) {
		this.sock = sock;
	}

	public void setFmContext(FMContext fmctx) {
		this.fmContext = fmctx;
		String dbLogin = config.getProperty("bpm.vanilla.freemetrics.database.userName"); //$NON-NLS-1$
		String dbPassword = config.getProperty("bpm.vanilla.freemetrics.database.password"); //$NON-NLS-1$
		String dbUrl = config.getProperty("bpm.vanilla.freemetrics.database.jdbcUrl"); //$NON-NLS-1$
		String dbDriver = config.getProperty("bpm.vanilla.freemetrics.database.driverClassName"); //$NON-NLS-1$
		fmContext.setDbDriver(dbDriver);
		fmContext.setDbLogin(dbLogin);
		fmContext.setDbPassword(dbPassword);
		fmContext.setDbUrl(dbUrl);
	}

	public FMContext getFmContext() {
		return fmContext;
	}

	public FaApiHelper getFaApiHelper() throws Exception {
		if(faHelper == null) {
			faHelper = new FaApiHelper(sock.getContext().getVanillaContext().getVanillaUrl(), new UnitedOlapLoader());
			RemoteServiceProvider remoteServiceProvider = new RemoteServiceProvider();
			remoteServiceProvider.configure(getVanillaContext());
			UnitedOlapServiceProvider.getInstance().init(remoteServiceProvider.getRuntimeProvider(), remoteServiceProvider.getModelProvider());
		}
		return faHelper;
	}

	@Override
	public void setCurrentModel(InputStream modelObjectXmlDefinition, Integer directoryItemId) throws Exception {

	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.BIW;
	}

	@Override
	public void setCurrentDirectoryItemId(Integer directoryItemId) throws Exception {
		if(getCurrentInput() != null) {
			getCurrentInput().setDirectoryItemId(directoryItemId);
		}
	}

	public RemoteSmartManager getAirRemote() {
//		if(remoteSmart == null){
			try {
				remoteSmart = new RemoteSmartManager(getRepositoryContext().getVanillaContext().getVanillaUrl(),getSession(), Locale.getDefault());
			} catch (Exception e) {
				e.printStackTrace();
			}
//		}
		return remoteSmart;
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
