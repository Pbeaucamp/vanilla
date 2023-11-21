package bpm.vanilla.server.gateway.server.tasks;

import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import bpm.gateway.core.DocumentGateway;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceConfiguration;
import bpm.vanilla.platform.core.beans.tasks.ITaskState;
import bpm.vanilla.platform.core.beans.tasks.TaskPriority;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.gateway.IGatewayRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.listeners.event.impl.ObjectExecutedEvent;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.tasks.GatewayState;
import bpm.vanilla.server.commons.server.tasks.ITask;
import bpm.vanilla.server.commons.server.tasks.TaskPriorityComparator;
import bpm.vanilla.server.gateway.server.GatewayServer;
import bpm.vanilla.server.gateway.server.GatewayServerConfig;
import bpm.vanilla.server.gateway.server.internal.FactoryRunnableGateway;
import bpm.vanilla.server.gateway.server.internal.GatewayRedefinedPropertiesExtractor;
import bpm.vanilla.server.gateway.server.internal.RunnableGateway;
import bpm.vanilla.server.gateway.server.internal.GatewayRedefinedPropertiesExtractor.PropertiesIdentifier;
import bpm.vanilla.server.gateway.server.internal.GatewayRedefinedPropertiesExtractor.PropertiesObjectType;

public class TaskGateway implements ITask {

	private static final long serialVersionUID = -6775193887164522401L;

	private long taskId;
	private transient IGatewayRuntimeConfig runtimeConfig;
	private transient Server server;
	private transient PoolableModel<DocumentGateway> gatewayModel;
	private HashMap<PropertiesIdentifier, Properties> overridenProperties;

	private transient RunnableGateway thread;
	private TaskPriority taskPriority;
	private String sessionId;

	private GatewayState state = new GatewayState(this);

	public TaskGateway(long taskId, IGatewayRuntimeConfig runtimeConfig, Server server, PoolableModel<DocumentGateway> poolableModel, String sessionId) {
		this.taskPriority = TaskPriority.NORMAL_PRIORITY;
		this.taskId = taskId;
		this.runtimeConfig = runtimeConfig;
		this.gatewayModel = poolableModel;
		this.server = server;
		this.sessionId = sessionId;

		extractAlternateConnectionProperties(runtimeConfig);
	}

	public Server getServer() {
		return server;
	}

	public TaskPriority getTaskPriority() {
		return taskPriority;
	}

	public void setTaskPriority(TaskPriority taskPriority) {
		this.taskPriority = taskPriority;
	}

	public long getId() {
		return taskId;
	}

	public ITaskState getTaskState() {
		return state;
	}

	public boolean isRunning() {
		return thread != null && thread.isAlive();
	}

	public boolean isStopped() {
		return state.isStopped();
	}

	public void startTask() {
		thread = FactoryRunnableGateway.create(runtimeConfig, getPoolableModel().getItemKey(), (GatewayServer) getServer(), this, 
				((GatewayServerConfig) server.getConfig()).getMaxRows(), gatewayModel.getModel(), ((GatewayServer) server).getRuntimeLogger(), 
				((GatewayServer) server).getServerLogger(), overridenProperties);
		thread.start();
	}

	protected void extractAlternateConnectionProperties(IGatewayRuntimeConfig runtimeConfig) {
		overridenProperties = GatewayRedefinedPropertiesExtractor.extract(runtimeConfig);
		if (runtimeConfig.getAlternateDataSourceConfiguration() != null) {
			AlternateDataSourceConfiguration alternate = runtimeConfig.getAlternateDataSourceConfiguration();

			// extract the alternate Connections to use
			HashMap<String, String> alternateConnections = new HashMap<String, String>();

			if (alternate.getDataSourcesNames() != null) {
				for (String connectionName : alternate.getDataSourcesNames()) {
					if (alternate.getConnection(connectionName) != null && !alternate.getConnection(connectionName).isEmpty()) {
						Logger.getLogger(getClass()).info("AlternateConnections, will use connection named " + alternate.getConnection(connectionName) + " for ds " + connectionName);
						alternateConnections.put(connectionName, alternate.getConnection(connectionName));
					}
					else {
						Logger.getLogger(getClass()).debug("empty keyval, ignoring");
					}
				}
			}

			for (String s : alternateConnections.keySet()) {
				Properties p = new Properties();
				p.put(alternateConnections.get(s), alternateConnections.get(s));
				overridenProperties.put(new PropertiesIdentifier(PropertiesObjectType.ALTERNATE_CONNECTION, s), p);
			}
		}
	}

	public void stopTask() throws Exception {
		if (thread != null && thread.isAlive()) {
			try {
				thread.interrupt();
			} catch (Exception ex) {
			} finally {
				try {
					thread.interrupt();
				} catch (Exception e) {
				}
			}
		}

		state.setStopped();
		server.getTaskManager().removeFinishedTasksFromRunningList(this);
		
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();

		RemoteVanillaPlatform api = new RemoteVanillaPlatform(conf);
//		IRunIdentifier runId = new SimpleRunIdentifier(getServer().getComponentIdentifier().getComponentUrl() + "-" + getServer().getComponentIdentifier().getComponentId() + "-" + getId());
		
		api.getListenerService().fireEvent(new ObjectExecutedEvent(getServer().getComponentIdentifier(), null, runtimeConfig.getObjectIdentifier(), runtimeConfig.getVanillaGroupId(), state));

	}
	
	public String getItemName() {
		return gatewayModel != null && gatewayModel.getDirectoryItem() != null ? gatewayModel.getDirectoryItem().getName() : "Unknown";
	}

	protected PoolableModel<?> getPoolableModel() {
		return gatewayModel;
	}

	public int compareTo(Object arg0) {
		return TaskPriorityComparator.instance.compare(this, (ITask) arg0);
	}

	public GatewayRuntimeState getStepsInfosMessage() throws Exception {
		if (thread != null) {
			return thread.getStepsInfosMessage();
		}
		else {
			throw new Exception("Task " + getId() + " has not started yet, the GatewaySteps informations are not available.");
		}

	}

	@Override
	public void join() throws Exception {
		if (thread != null && thread.isAlive()) {
			try {
				thread.join();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public IVanillaComponentIdentifier getComponentIdentifier() {
		return getServer().getComponentIdentifier();
	}

	@Override
	public int getGroupId() {
		return getPoolableModel().getItemKey().getRepositoryContext().getGroup().getId();
	}

	@Override
	public IObjectIdentifier getObjectIdentifier() {
		if(getPoolableModel() != null) {
			return new ObjectIdentifier(getPoolableModel().getItemKey().getRepositoryContext().getRepository().getId(), getPoolableModel().getItemKey().getDirectoryItemId());
		}
		else {
			return null;
		}
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}
}
