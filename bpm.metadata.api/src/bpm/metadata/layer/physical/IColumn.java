package bpm.metadata.layer.physical;

import java.util.HashMap;
import java.util.List;

public interface IColumn {

	public String getName();
	
	public String getClassName();
	
	public Class<?> getJavaClass() throws Exception;
	
	public ITable getTable();
	
	public List<String> getValues();
	
	public String getShortName();

	public List<String> getValues(HashMap<String, String> parentValues);
}
