package bpm.vanilla.platform.core.remote.impl.components;

import java.util.List;

import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.IRuntimeState;
import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteServerManager implements IVanillaServerManager {

	protected HttpCommunicator httpCommunicator;
	protected boolean isDispatching = false;
	
	protected static XStream xstream;
	static {
		xstream = new XStream();
	}
	
	private String vanillaUrl;
	
	public RemoteServerManager(HttpCommunicator httpCommunicator, String vanillaUrl, String login, String password, boolean isDispatching) {
		this.httpCommunicator = httpCommunicator;
		this.httpCommunicator.init(vanillaUrl, login, password);
		this.isDispatching = isDispatching;
		this.vanillaUrl = vanillaUrl;
	}

	protected XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	public long[] getMemory() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaServerManager.ActionType.GET_MEMORY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);

		return (long[]) xstream.fromXML(xml);
	}

	@Override
	public ServerConfigInfo getServerConfig() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaServerManager.ActionType.GET_SERVER_CONFIG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		if(xml != null && !xml.isEmpty()) {
			return (ServerConfigInfo) xstream.fromXML(xml);
		}
		else {
			return null;
		}
	}

	@Override
	public void historize() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaServerManager.ActionType.HISTORIZE);
		httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
	}

	@Override
	public boolean isStarted() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaServerManager.ActionType.IS_STARTED);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		if(xml != null && !xml.isEmpty()) {
			return (Boolean) xstream.fromXML(xml);
		}
		else {
			return false;
		}
	}

	@Override
	public void removeTask(IRunIdentifier identifier) throws Exception {
		XmlAction op = new XmlAction(createArguments(identifier), IVanillaServerManager.ActionType.REMOVE_TASK);
		httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
	}

	@Override
	public void resetServerConfig(ServerConfigInfo serverConfigInfo) throws Exception {
		XmlAction op = new XmlAction(createArguments(serverConfigInfo), IVanillaServerManager.ActionType.RESET_SERVER_CONFIG);
		httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
	}

	@Override
	public void startServer() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaServerManager.ActionType.START_SERVER);
		httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
	}

	@Override
	public void stopServer() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaServerManager.ActionType.STOP_SERVER);
		httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
	}

	@Override
	public void stopTask(IRunIdentifier identifier) throws Exception {
		XmlAction op = new XmlAction(createArguments(identifier), IVanillaServerManager.ActionType.STOP_TASK);
		httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<TaskInfo> getRunningTasks() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaServerManager.ActionType.GET_RUNNING_TASKS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		return (List<TaskInfo>) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<TaskInfo> getTasksInfo() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaServerManager.ActionType.GET_TASKS_INFO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		return (List<TaskInfo>) xstream.fromXML(xml);
	}

	@Override
	public TaskInfo getTasksInfo(IRunIdentifier identifier) throws Exception {
		XmlAction op = new XmlAction(createArguments(identifier), IVanillaServerManager.ActionType.GET_TASK_INFO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		return (TaskInfo) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<TaskInfo> getWaitingTasks() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaServerManager.ActionType.GET_WAITING_TASKS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		return (List<TaskInfo>) xstream.fromXML(xml);
	}

	@Override
	public String getUrl() {
		return vanillaUrl;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<IRuntimeState> getPreviousInfos(int repositoryId, int start, int end) throws Exception {
		XmlAction op = new XmlAction(createArguments(repositoryId, start, end), IVanillaServerManager.ActionType.GET_PREVIOUS_TASKS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		return (List<IRuntimeState>) xstream.fromXML(xml);
	}

}
