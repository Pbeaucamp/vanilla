package bpm.vanilla.server.ui.views.helpers;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.components.VanillaComponentType;

public class ViewServerInfoHelper {

	public static List<ServerType> getRunningComponentTypes(List<Server> servers) throws Exception {
		List<ServerType> types = new ArrayList<ServerType>();

		for (Server s : servers) {
			ServerType type = null;
			if (s.getComponentNature().equals(VanillaComponentType.COMPONENT_GATEWAY)) {
				type = ServerType.GATEWAY;
			}
			else if (s.getComponentNature().equals(VanillaComponentType.COMPONENT_REPORTING)) {
				type = ServerType.REPORTING;
			}
			else if (s.getComponentNature().equals(VanillaComponentType.COMPONENT_GED)) {
				type = ServerType.GED;
			}
			else if (s.getComponentNature().equals(VanillaComponentType.COMPONENT_WORKFLOW)) {
				type = ServerType.WORKFLOW;
			}

			if (type != null) {
				types.add(type);
			}

		}

		return types;
	}

}
