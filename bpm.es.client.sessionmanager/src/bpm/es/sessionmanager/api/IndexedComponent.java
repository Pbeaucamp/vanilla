package bpm.es.sessionmanager.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bpm.vanilla.platform.core.beans.VanillaLogs;

public class IndexedComponent implements IIndexedLog {

	private Map<String, List<VanillaLogs>> indexedLogs = new TreeMap<String, List<VanillaLogs>>();
	
	@Override
	public void indexSessionLog(VanillaLogs log) {
		String key = null;
		String app = log.getApplication();
		app = app.substring(app.lastIndexOf(".") + 1, app.length());
		
		key = app == null ? "unknown" : app; //$NON-NLS-1$
		
		if (indexedLogs.containsKey(key)) {
			indexedLogs.get(key).add(log);
		}
		else {
			List<VanillaLogs> logs = new ArrayList<VanillaLogs>();
			logs.add(log);
			indexedLogs.put(key, logs);
		}
	}

	public List<String> getIndexKeys() {
		return new ArrayList<String>(indexedLogs.keySet());
	}
	
	public List<VanillaLogs> getSessionLogForKey(String key) {
		return indexedLogs.get(key);
	}

}
