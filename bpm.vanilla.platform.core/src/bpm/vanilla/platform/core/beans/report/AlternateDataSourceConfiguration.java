package bpm.vanilla.platform.core.beans.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Object to configure the DataSOurce's Connection to use for runtime
 * 
 * @author ludo
 * 
 */
public class AlternateDataSourceConfiguration implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, String> config = new HashMap<String, String>();

	public void setConnection(String dataSourceName, String connectionToUse) {
		this.config.put(dataSourceName, connectionToUse);
	}

	public String getConnection(String dataSourceName) {
		return this.config.get(dataSourceName);
	}

	public List<String> getDataSourcesNames() {
		List<String> l = new ArrayList<String>(config.keySet());
		return l;
	}

	public HashMap<String, String> getAlternateConnections() {
		return config;
	}
	
	public int getCount() {
		return config.keySet().size();
	}
}
