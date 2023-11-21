package bpm.nosql.oda.runtime.cassandraUtility;

import java.util.Map;

public class CassandraRow {
	 private String key;
	  private Map<String, String> columns;

	  public String getKey()
	  {
	    return this.key;
	  }
	  public void setKey(String key) {
	    this.key = key;
	  }
	  public Map<String, String> getColumns() {
	    return this.columns;
	  }
	  public void setColumns(Map<String, String> columns) {
	    this.columns = columns;
	  }

	  public String toString()
	  {
	    String asString = this.key + "\n";

	    for (String columnName : this.columns.keySet()) {
	      asString = asString + "\t" + columnName + ": " + (String)this.columns.get(columnName);
	    }

	    return asString;
	  }
}
