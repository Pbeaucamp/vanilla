package bpm.es.sessionmanager.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bpm.vanilla.platform.core.beans.VanillaLogs;

public class IndexedApplication implements IIndexedLog {

	private Map<String, List<VanillaLogs>> indexedLogs;
	
	public IndexedApplication() {
		indexedLogs = new TreeMap<String, List<VanillaLogs>>();
	}
	
	public void indexSessionLog(VanillaLogs log) {
		//log.
		String key = null;
		String app = log.getApplication();
		
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
	
	public List<VanillaLogs> getLogsByApplication(String application) {
		List<VanillaLogs> logList = new ArrayList<VanillaLogs>();
		
		if (application.equals("")) { //$NON-NLS-1$
			for (List<VanillaLogs> logs : indexedLogs.values()) {
				logList.addAll(logs);	
			}
			return logList;
		}
		else {
			for (List<VanillaLogs> logs : indexedLogs.values()) {
				if (logs.get(0).getApplication().equals(application)) {
					logList.addAll(logs);	
				}
			}
			return logList;
		}
	}
	
	public List<VanillaLogs> getAllLogs() {
		List<VanillaLogs> logList = new ArrayList<VanillaLogs>();
		
		for (List<VanillaLogs> logs : indexedLogs.values()) {
			logList.addAll(logs);
		}
		return logList;
	}

	public List<VanillaLogs> getUniqueApplicationLogs() {
		List<VanillaLogs> logList = new ArrayList<VanillaLogs>();
		
		for (List<VanillaLogs> logs : indexedLogs.values()) {
			logList.add(logs.get(0));
		}
		return logList;
	}
	
	public List<String> getIndexKeys() {
		return new ArrayList<String>(indexedLogs.keySet());
	}
	
	public List<VanillaLogs> getSessionLogForKey(String key) {
		return indexedLogs.get(key);
	}
}
