package bpm.vanilla.server.ui;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.beans.tasks.TaskList;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.internal.TaskListManager;

public class Activator extends AbstractUIPlugin implements IDesignerActivator<TaskList> {

	public static final String PLUGIN_ID = "bpm.vanilla.server.ui"; //$NON-NLS-1$
	public static final String TASK_LIST_FOLDER = "resources/task-lists/"; //$NON-NLS-1$
	
	private static Activator plugin;

	private ServerType type;
	private String vanillaUrl;
	private IVanillaServerManager remoteServer;

	private IRepositoryContext repositoryContext;
	
	private TaskListManager taskListManager;
	private TaskList currentTaskList;
	
	private Repository selectedRepository;

	public Activator() {
		taskListManager = new TaskListManager();
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		File f = new File(TASK_LIST_FOLDER);

		// ere, added to auto create file
		if (!f.exists()) {
			f.mkdirs();
		}

		for (File _f : f.listFiles()) {
			try {
				taskListManager.loadList(new FileInputStream(_f));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

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

	public void setServerUrl(ServerType type, String serverUrl) {
		this.type = type;

		if(vanillaUrl == null) {
			IVanillaContext ctx = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaContext();
			vanillaUrl = ctx.getVanillaUrl();
		}
		
		if (serverUrl == null) {
			remoteServer = null;
			type = null;
			return;
		}

		IVanillaContext ctx = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaContext();
		switch (type) {
			case REPORTING:
				remoteServer = new RemoteReportRuntime(vanillaUrl, ctx.getLogin(), ctx.getPassword(), serverUrl); //$NON-NLS-1$
				break;
			case GATEWAY:
				remoteServer = new RemoteGatewayComponent(vanillaUrl, ctx.getLogin(), ctx.getPassword(), serverUrl); //$NON-NLS-1$
				break;
			case GED:
//				serverRemote = new ServerClient(type, "server", url, ctx.getLogin(), ctx.getPassword()); //$NON-NLS-1$
				break;
			case WORKFLOW:
				remoteServer = new RemoteWorkflowComponent(vanillaUrl, ctx.getLogin(), ctx.getPassword(), serverUrl);
				break;
		}
	}
	
	public ServerType getServerType() {
		return type;
	}
	
	public String getVanillaUrl() {
		return vanillaUrl != null ? vanillaUrl : ""; //$NON-NLS-1$
	}

	public IVanillaServerManager getRemoteServerManager() {
		return remoteServer;
	}

	public TaskListManager getTaskListManager() {
		return taskListManager;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
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

	public void addLoadModelListener(PropertyChangeListener listener) {
		
	}
	
	public IVanillaAPI getVanillaApi() {
		return bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi();
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

	public int getRepositoryItemType() {
		return IRepositoryApi.TASK_LIST;
	}

	public boolean isRepositoryConnectionDefined() {
		return false;
	}

	public void setCurrentModel(Object oldModel) {
		this.currentTaskList = (TaskList) oldModel;
	}

	public List<Server> getRuntimeNodes() {
		IVanillaAPI api = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi();

		List<Server> servers = new ArrayList<Server>();

		try {
			for (Server s : api.getVanillaSystemManager().getServerNodes(true)) {
				if (VanillaComponentType.COMPONENT_GATEWAY.equals(s.getComponentNature()) || VanillaComponentType.COMPONENT_REPORTING.equals(s.getComponentNature()) || VanillaComponentType.COMPONENT_WORKFLOW.equals(s.getComponentNature()) || VanillaComponentType.COMPONENT_GED.equals(s.getComponentNature())) {

					if (s.getUrl().equals(bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaContext().getVanillaUrl())) {
						s.setName(s.getName() + Messages.Activator_8);
					}
					else {
						s.setName(s.getName() + "(" + s.getUrl() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					servers.add(s);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return servers;
	}

	@Override
	public IRepositoryContext getRepositoryContext() {
		return repositoryContext;
	}

	@Override
	public void setRepositoryContext(IRepositoryContext ctx) {
		this.repositoryContext = ctx;

	}

	@Override
	public IRepositoryApi getRepositoryConnection() {
		return null;
	}

	@Override
	public Object saveCurrentModel() throws Exception {
		return null;
	}

	@Override
	public void setCurrentModel(String modelObjectXmlDefinition, Integer directoryItemId) throws Exception { }

	@Override
	public void setCurrentModel(InputStream modelObjectXmlDefinition, Integer directoryItemId) throws Exception { }

	@Override
	public String getApplicationId() {
		return IRepositoryApi.HYPERVISION;
	}

	public void setSelectedRepository(Repository selectedRepository) {
		this.selectedRepository = selectedRepository;
	}
	
	public Repository getSelectedRepository() {
		return selectedRepository;
	}

	@Override
	public void setCurrentDirectoryItemId(Integer directoryItemId) throws Exception { }
}
