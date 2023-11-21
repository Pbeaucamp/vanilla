package bpm.vanilla.server.ui;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.repository.api.model.IRepositoryConnection;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.server.client.ReportingServer;
import bpm.vanilla.server.client.ServerClient;
import bpm.vanilla.server.client.ServerType;
import bpm.vanilla.server.client.communicators.TaskList;
import bpm.vanilla.server.client.communicators.freemetadata.FmdtServerClient;
import bpm.vanilla.server.client.communicators.gateway.GatewayServerClient;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.internal.TaskListManager;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IDesignerActivator<TaskList>{

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.vanilla.server.ui";
	public static final String TASK_LIST_FOLDER = "resources/task-lists/";
	// The shared instance
	private static Activator plugin;
	
	private ServerClient serverRemote;
	private TaskListManager taskListManager;
	
	
	private TaskList currentTaskList;
	
	/**
	 * The constructor
	 */
	public Activator() {
		taskListManager = new TaskListManager();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		File f = new File(TASK_LIST_FOLDER);
		
			for(File _f : f.listFiles()){
				try{
					taskListManager.loadList(new FileInputStream(_f));
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		
			
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

	
	public void setServerUrl(ServerType type, String url){
				
		IViewPart v = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("bpm.vanilla.server.ui.freemetadata.views.FmdtViewPart");
		
		if (type == ServerType.FREEMETADATA){
			try {
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("bpm.vanilla.server.ui.freemetadata.views.FmdtViewPart", null,IWorkbenchPage.VIEW_CREATE  );
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		else{
			if (v != null){
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(v);
			}
		}
		if (url == null){
			serverRemote = null;
			return;
		}
		
		
		switch(type){
		case REPORTING:
			serverRemote = new ReportingServer("server", url);
			
			break;
		case GATEWAY:
			serverRemote = new GatewayServerClient("server", url);
			break;
		case FREEMETADATA:
			serverRemote = new FmdtServerClient("server", url);
			break;
		}
		
	}

	public ServerClient getServerRemote(){
		return serverRemote;
	}
	
	public TaskListManager getTaskListManager(){
		return taskListManager;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		for(Field f : Icons.class.getFields()){
			
			try {
				reg.put((String)f.get(null), ImageDescriptor.createFromFile(Activator.class, "icons/" + f.get(null)));
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				
				e.printStackTrace();
			}
			
		}

	}

	public void addLoadModelListener(PropertyChangeListener listener) {
		
		
	}

	public TaskList getCurrentModel() {
		
		return currentTaskList;
	}

	public Integer getCurrentModelDirectoryItemId() {
		
		return null;
	}

	public String getCurrentModelFileName() {
		
		return null;
	}

	public String getCurrentModelXml() {
		return this.currentTaskList.asXmlString();
	}

	public IRepositoryConnection getRepositoryConnection() {
		
		return null;
	}

	public int getRepositoryItemType() {
		
		return IRepositoryConnection.TASK_LIST;
	}

	public boolean isRepositoryConnectionDefined() {
		
		return false;
	}

	public Object saveCurrentModel() throws Exception {
		
		return null;
	}

	public void setCurrentModel(String modelObjectXmlDefinition,
			Integer directoryItemId) throws Exception {
		
		
	}

	public void setCurrentModel(InputStream modelObjectXmlDefinition, Integer directoryItemId) throws Exception {
		
		
	}

	public void setCurrentModel(Object oldModel) {
		this.currentTaskList = (TaskList)oldModel;
		
	}

	public void setRepositoryConnection(IRepositoryConnection sock) {
		
		
	}
}
