package bpm.es.sessionmanager.api;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs;

public interface IIndexedLog {

	public void indexSessionLog(VanillaLogs log);
	
	public List<String> getIndexKeys();
	
	public List<VanillaLogs> getSessionLogForKey(String key);
}
