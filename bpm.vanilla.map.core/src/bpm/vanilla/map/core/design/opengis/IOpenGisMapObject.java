package bpm.vanilla.map.core.design.opengis;

import java.util.List;

public interface IOpenGisMapObject {

	public int getId();
	public void setId(int id);
	
	public String getName();
	public void setName(String name);
	
	public List<IOpenGisMapEntity> getEntities();
	public void setEntities(List<IOpenGisMapEntity> entities);
	public void addEntity(IOpenGisMapEntity entity);
	
	public IOpenGisDatasource getDatasource();
	public void setDatasource(IOpenGisDatasource datasource);
	public int getDatasourceId();
	public void setDatasourceId(int datasourceId);

	public void setMapDefinitionId(int mapDefinitionId);
	public int getMapDefinitionId();
	
}
