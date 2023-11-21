package bpm.gateway.core.server.database.nosql;

import bpm.gateway.core.DataStream;

public interface IFieldsDefinition extends DataStream {

	public void addColumnType(String key, String value);
	
	public void removeNotUsedColumn();

}
