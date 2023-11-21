package bpm.vanilla.workplace.core.datasource;

import bpm.vanilla.workplace.core.datasource.IDatasourceType.DatasourceType;

public interface IDatasource {
	
	public String getId();
	
	public String getName();
	
	public DatasourceType getType();
	
	public String getExtensionId();
	
	public int getItemId();
}
