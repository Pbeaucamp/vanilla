package bpm.vanilla.platform.core.repository.services;

import java.util.Collection;
import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.RepositoryLog;
import bpm.vanilla.platform.core.xstream.IXmlActionType;


public interface IRepositoryLogService {
	public static enum ActionType implements IXmlActionType{
		ADD_LOG(Level.DEBUG), DEL_LOG(Level.DEBUG), FIND_LOG(Level.DEBUG), LIST_LOG(Level.DEBUG), LIST_LOG_FOR_ITEM(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	public int addLog(RepositoryLog d) throws Exception;
	public void delReportModel(RepositoryLog d) throws Exception;
	public RepositoryLog getById(int logId) throws Exception;
	public Collection<RepositoryLog> getLogs() throws Exception;
	public List<RepositoryLog> getLogsFor(RepositoryItem i) throws Exception;
}
