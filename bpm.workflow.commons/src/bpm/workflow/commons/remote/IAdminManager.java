package bpm.workflow.commons.remote;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IAdminManager {
	
	public static enum ActionType implements IXmlActionType{
		LOGIN(Level.INFO), CONNECT(Level.INFO), LOGOUT(Level.INFO);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public User login(String login, String password, String locale) throws Exception;
	
	public String connect(User user) throws Exception;
	
	public void logout() throws Exception;
}
