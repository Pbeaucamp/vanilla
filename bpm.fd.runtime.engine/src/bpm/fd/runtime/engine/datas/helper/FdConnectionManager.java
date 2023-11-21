package bpm.fd.runtime.engine.datas.helper;

import java.util.HashMap;

import bpm.fd.api.core.model.datas.DataSource;

public class FdConnectionManager {
	
	private static FdConnectionManager instance;
	
	private HashMap<String, Integer> connectionsMap = new HashMap<String, Integer>();
	
	public static FdConnectionManager getInstance() {
		if(instance == null) {
			instance = new FdConnectionManager();
		}
		return instance;
	}
	
	public synchronized void addConnection(DataSource ds, Integer itemId, Integer repId) {
		
		StringBuilder key = new StringBuilder();
		key.append(ds.getName());
		key.append("itemid");
		key.append(itemId+"");
		key.append("repid");
		key.append(repId+"");
		Integer i = connectionsMap.get(key.toString());
		
		if(i == null) {
			i = 0;
		}
		i = i+1;
		connectionsMap.put(key.toString(), i);
	}
	
	public synchronized void clearConnection(DataSource ds, Integer itemId, Integer repId) {
		StringBuilder key = new StringBuilder();
		key.append(ds.getName());
		key.append("itemid");
		key.append(itemId+"");
		key.append("repid");
		key.append(repId+"");
		Integer i = connectionsMap.get(key.toString());
		if(i == null || (i - 1) == 0) {
			ds.closeConnections();
			connectionsMap.remove(key.toString());
		}
		else {
			i = i-1;
			connectionsMap.put(key.toString(), i);
		}
	}
	

}
