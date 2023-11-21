package bpm.vanilla.platform.core;

import java.io.Serializable;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IGlobalManager {

	public static enum ActionType implements IXmlActionType{
		MANAGE_ITEM(Level.DEBUG);

		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}

	public Serializable manageItem(Serializable item, ManageAction action) throws Exception;

}
