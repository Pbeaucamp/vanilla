package bpm.gateway.core.server.database.nosql;

import java.util.HashMap;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.MappingException;

public interface IOutputNoSQL extends Transformation {
	
	public static final String KEY_DEFINITION = "KEY";
	
	public static final String ID_DEFINITION = "_id";
		
	public void createMapping(Transformation input, String inputCol, String outputCol);
	
	public void deleteMapping(Transformation input, String inputCol);
	
	public HashMap<String, String> getMappingsFor(Transformation t);
	
	public boolean isMapped(Transformation t, String colName);

	public int getMappingsIndexFor(Transformation input, String column) throws Exception;

	public int getIndexKey(Transformation input) throws MappingException, Exception;

	public boolean isIndexMap(Transformation input) throws Exception;
}
