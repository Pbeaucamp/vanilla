package bpm.vanilla.map.core.design.opengis;

import java.util.List;

public interface IOpenGisMapEntity {

	public int getId();
	public void setId(int id);

	public String getEntityId();
	public void setEntityId(String entityId);

	public int getMapId();
	public void setMapId(int mapId);

	public String getName();
	public void setName(String name);
	
	public List<IOpenGisCoordinate> getCoordinates();
	public void setCoordinates(List<IOpenGisCoordinate> coordinates);
	public String getCoordinatesAsString();
	
	public String getType();
	public void setType(String type);
}
