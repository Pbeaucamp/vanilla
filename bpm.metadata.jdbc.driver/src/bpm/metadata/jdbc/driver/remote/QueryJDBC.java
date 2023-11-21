package bpm.metadata.jdbc.driver.remote;

import java.io.Serializable;

public class QueryJDBC implements Serializable{
	
	private String sql;

	public QueryJDBC(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}
	
	
	
	
}
