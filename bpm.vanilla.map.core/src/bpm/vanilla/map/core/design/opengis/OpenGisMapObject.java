package bpm.vanilla.map.core.design.opengis;

import java.util.ArrayList;
import java.util.List;

public class OpenGisMapObject implements IOpenGisMapObject {

	private int id;
	private String name;
	private IOpenGisDatasource datasource;
	private List<IOpenGisMapEntity> entities;
	private int datasourceId;
	private String datasourceType;
	private int mapDefinitionId;
	
	@Override
	public void addEntity(IOpenGisMapEntity entity) {
		if(entities == null) {
			entities = new ArrayList<IOpenGisMapEntity>();
		}
		entities.add(entity);
	}

	@Override
	public IOpenGisDatasource getDatasource() {
		return datasource;
	}

	@Override
	public List<IOpenGisMapEntity> getEntities() {
		return entities;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setDatasource(IOpenGisDatasource datasource) {
		this.datasource = datasource;
		if(datasource != null){
			this.datasourceId = datasource.getId();
			this.datasourceType = datasource.getType();
		}
	}

	@Override
	public void setEntities(List<IOpenGisMapEntity> entities) {
		this.entities = entities;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setDatasourceId(int datasourceId) {
		this.datasourceId = datasourceId;
	}

	@Override
	public int getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceType(String datasourceType) {
		this.datasourceType = datasourceType;
	}

	public String getDatasourceType() {
		return datasourceType;
	}

	@Override
	public int getMapDefinitionId() {
		return mapDefinitionId;
	}

	@Override
	public void setMapDefinitionId(int mapDefinitionId) {
		this.mapDefinitionId = mapDefinitionId;
	}
}
