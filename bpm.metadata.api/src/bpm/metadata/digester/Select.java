package bpm.metadata.digester;


public class Select {
	String colName;
	String tableName;
	String aggregateName;
	
	public void setColName(String col){
		colName = col;
	}
	
	public void setStreamName(String s){
		tableName = s;
	}
	
	public void setAggregate(String col){
		aggregateName = col;
	}
}
