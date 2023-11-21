package bpm.vanilla.platform.core;

import java.util.List;

import bpm.vanilla.platform.core.beans.IRuntimeState;
import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

	public interface IVanillaServerManager {public static final String SERVLET_SERVER_MANAGER = "/vanilla40/ServerManagerServlet";
	
	public static enum ActionType implements IXmlActionType{
		START_SERVER(Level.DEBUG), STOP_SERVER(Level.DEBUG), IS_STARTED(Level.DEBUG), GET_SERVER_CONFIG(Level.DEBUG), RESET_SERVER_CONFIG(Level.DEBUG), 
		STOP_TASK(Level.INFO), REMOVE_TASK(Level.DEBUG), HISTORIZE(Level.DEBUG), GET_MEMORY(Level.DEBUG),
		GET_RUNNING_TASKS(Level.DEBUG), GET_TASKS_INFO(Level.DEBUG), GET_WAITING_TASKS(Level.DEBUG), GET_TASK_INFO(Level.DEBUG), GET_PREVIOUS_TASKS(Level.DEBUG);

		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}

	public void startServer() throws Exception;
	
	public void stopServer() throws Exception;
	
	public boolean isStarted()throws Exception;

	public ServerConfigInfo getServerConfig() throws Exception;

	public void resetServerConfig(ServerConfigInfo serverConfigInfo) throws Exception;

	public void stopTask(IRunIdentifier identifier) throws Exception;
	
	public void removeTask(IRunIdentifier identifier) throws Exception;

	public void historize() throws Exception;
	
	public long[] getMemory() throws Exception;
	
	public List<TaskInfo> getRunningTasks() throws Exception;
	
	public List<TaskInfo> getWaitingTasks() throws Exception;
	
	public List<TaskInfo> getTasksInfo() throws Exception;
	
	public TaskInfo getTasksInfo(IRunIdentifier identifier) throws Exception;
	
	public String getUrl();

	public List<IRuntimeState> getPreviousInfos(int repositoryId, int start, int end) throws Exception;
}
