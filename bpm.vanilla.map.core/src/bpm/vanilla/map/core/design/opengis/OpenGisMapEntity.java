package bpm.vanilla.map.core.design.opengis;

import java.util.List;

public class OpenGisMapEntity implements IOpenGisMapEntity {

	private int id;
	private int mapId;
	private String entityId;
	private String name;
	private String type;
	private List<IOpenGisCoordinate> coordinates;
	
	public OpenGisMapEntity(){}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getEntityId() {
		return entityId;
	}

	@Override
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	@Override
	public int getMapId() {
		return mapId;
	}

	@Override
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<IOpenGisCoordinate> getCoordinates() {
		return coordinates;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setCoordinates(List<IOpenGisCoordinate> coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getCoordinatesAsString() {
		if(coordinates != null){
			StringBuilder buf = new StringBuilder();
			for(IOpenGisCoordinate co : coordinates){
				buf.append(co.toString());
			}
			return buf.toString();
		}
		return "";
	}
}
