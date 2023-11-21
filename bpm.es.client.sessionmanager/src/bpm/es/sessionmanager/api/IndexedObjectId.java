package bpm.es.sessionmanager.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bpm.vanilla.platform.core.beans.VanillaLogs;

public class IndexedObjectId implements IIndexedLog {

	private Map<String, List<VanillaLogs>> indexedLogs;
	
	public IndexedObjectId() {
		indexedLogs = new TreeMap<String, List<VanillaLogs>>();
	}
	
	public void indexSessionLog(VanillaLogs log) {
		String key = null;
		
		String name = log.getObjectName();
		key = name == null ? "unknown" : "" + name; //$NON-NLS-1$ //$NON-NLS-2$
		
		if (indexedLogs.containsKey(key)) {
			indexedLogs.get(key).add(log);
		}
		else {
			List<VanillaLogs> logs = new ArrayList<VanillaLogs>();
			logs.add(log);
			indexedLogs.put(key, logs);
		}
	}
	
	public List<VanillaLogs> getLogsByObjectId(int objectId) {
		List<VanillaLogs> logList = new ArrayList<VanillaLogs>();
		
		if (objectId == -1) {
			for (List<VanillaLogs> logs : indexedLogs.values()) {
				logList.addAll(logs);	
			}
			return logList;
		}
		else {
			for (List<VanillaLogs> logs : indexedLogs.values()) {
				if (logs.get(0).getDirectoryItemId() == objectId) {
					logList.addAll(logs);	
				}
			}
			return logList;
		}
	}
	
	public List<VanillaLogs> getUniqueLogs() {
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
