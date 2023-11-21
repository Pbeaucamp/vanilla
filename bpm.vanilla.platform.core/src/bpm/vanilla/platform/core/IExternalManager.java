package bpm.vanilla.platform.core;

import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.resources.D4C;
import bpm.vanilla.platform.core.beans.resources.D4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItem.TypeD4CItem;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IExternalManager {

	public static enum ActionType implements IXmlActionType{
		GET_D4C_DEFINITIONS(Level.DEBUG), GET_D4C_ITEMS(Level.DEBUG);

		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}

	public List<D4C> getD4CDefinitions() throws Exception;
	
	public HashMap<String, HashMap<String, List<D4CItem>>> getD4cItems(int parentId, TypeD4CItem type) throws Exception;
}
