package bpm.vanilla.platform.core;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.feedback.Feedback;
import bpm.vanilla.platform.core.beans.feedback.UserInformations;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface ICommunicationManager {
	
	public enum ActionType implements IXmlActionType {
		SEND_INFOS(Level.INFO), SEND_FEEDBACK(Level.INFO);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public void sendInfos(UserInformations userInfos) throws Exception;
	
	public void sendFeedback(Feedback feedback) throws Exception;
}
