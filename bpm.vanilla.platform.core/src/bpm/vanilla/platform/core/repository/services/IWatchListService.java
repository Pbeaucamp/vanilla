package bpm.vanilla.platform.core.repository.services;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IWatchListService {
	public static enum ActionType implements IXmlActionType{
		ADD_TO_WATCHLIST(Level.DEBUG), GET_LAST_CONSULTED(Level.DEBUG), GET_WATCH_LIST(Level.DEBUG), REMOVE_FROM_WATCHLIST(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public void addToWatchList(RepositoryItem it) throws Exception;
	public List<RepositoryItem> getLastConsulted() throws Exception;
	public List<RepositoryItem> getWatchList() throws Exception ;
	public void removeFromWatchList(RepositoryItem element)throws Exception;
	
}
