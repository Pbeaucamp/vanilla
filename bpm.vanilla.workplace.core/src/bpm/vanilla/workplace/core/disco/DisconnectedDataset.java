package bpm.vanilla.workplace.core.disco;

import java.util.ArrayList;
import java.util.List;

public class DisconnectedDataset {

	private String name;
	private String datasourceName;
	
	private String queryText;
	private String querySqlite;
	
	private List<DisconnectedDatasetParameter> params;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDatasourceName() {
		return datasourceName;
	}

	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}

	public String getQueryText() {
		return queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}

	public String getQuerySqlite() {
		return querySqlite;
	}

	public void setQuerySqlite(String querySqlite) {
		this.querySqlite = querySqlite;
	}

	public List<DisconnectedDatasetParameter> getParams() {
		return params;
	}
	
	public void addParam(DisconnectedDatasetParameter param) {
		if(params == null) {
			this.params = new ArrayList<DisconnectedDatasetParameter>();
		}
		this.params.add(param);
	}
}
