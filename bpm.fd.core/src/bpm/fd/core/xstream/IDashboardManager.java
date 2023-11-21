package bpm.fd.core.xstream;

import java.util.List;

import bpm.fd.core.Dashboard;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IDashboardManager {

	public static enum ActionType implements IXmlActionType {
		PREVIEW_DASHBOARD(Level.DEBUG), GET_DEFAULT_CSS(Level.DEBUG), SAVE_DASHBOARD(Level.INFO), OPEN_DASHBOARD(Level.INFO);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public String previewDashboard(Dashboard dashboard) throws Exception;
	
	public String getDefaultCssFile() throws Exception;

	public Integer saveDashboard(RepositoryDirectory target, Dashboard dashboard, boolean update, List<Group> groups) throws Exception;
	
	public Dashboard openDashboard(int itemId) throws Exception;
}
