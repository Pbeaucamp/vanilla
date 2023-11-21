package bpm.vanilla.portal.server.apiImpl;

import bpm.vanilla.platform.core.IPreferencesManager;
import bpm.vanilla.platform.core.beans.OpenPreference;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.server.security.PortalSession;

public class PreferencesImpl {

	public static void addOpenItem(RepositoryItem item, PortalSession session) throws Exception {
		OpenPreference op = new OpenPreference();
		op.setItemId(item.getId());
		op.setItemName(item.getItemName());
		op.setUserId(session.getUser().getId());

		IPreferencesManager manager = session.getPreferencesManager();

		manager.addOpenPreference(op);

	}

	public static void removeOpenItem(RepositoryItem item, PortalSession session) throws Exception {
		OpenPreference op = new OpenPreference();
		op.setItemId(item.getId());
		op.setItemName(item.getItemName());
		op.setUserId(session.getUser().getId());

		IPreferencesManager manager = session.getPreferencesManager();

		manager.delOpenPreference(op);

	}

}
