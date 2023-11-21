package bpm.vanilla.platform.core;

import java.util.List;

import bpm.vanilla.platform.core.beans.UOlapPreloadBean;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

/**
 * Used to create preload configuration for UnitedOlap Schemas
 * @author ludo
 *
 */
public interface IUnitedOlapPreloadManager {
	public static enum ActionType implements IXmlActionType{
		ADD_PRELOAD(Level.INFO), LIST_PRELOAD(Level.DEBUG), DEL_PRELOAD(Level.INFO);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public void addPreload(UOlapPreloadBean bean) throws Exception;
	
	public void removePreload(UOlapPreloadBean bean) throws Exception;
	
	public List<UOlapPreloadBean> getPreloadForIdentifier(IObjectIdentifier identifier) throws Exception;
}
