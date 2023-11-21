package bpm.vanilla.map.core.design;

import java.io.InputStream;
import java.util.List;

import bpm.vanilla.map.core.design.opengis.IOpenGisCoordinate;
import bpm.vanilla.map.core.design.opengis.IOpenGisMapEntity;
import bpm.vanilla.map.core.design.opengis.IOpenGisMapObject;

/**
 * A service to get/add/remove openGis maps
 * @author Marc Lanquetin
 *
 */
public interface IOpenGisMapService {

	public List<IOpenGisMapObject> getOpenGisMaps() throws Exception;
	
	public IOpenGisMapObject getOpenGisMapById(int mapId) throws Exception;
	
	public List<IOpenGisMapEntity> getOpenGisMapEntities(IOpenGisMapObject map) throws Exception;
	
	public void addOpenGisMap(IOpenGisMapObject map) throws Exception;
	
	public void addOpenGisMap(IOpenGisMapObject map, InputStream fileShape) throws Exception;
	
	public void updateOpenGisMap(IOpenGisMapObject map) throws Exception;
	
	public void deleteOpenGisMap(IOpenGisMapObject map) throws Exception;
	
	public void saveShapeFile(int openGisMapId, InputStream fis) throws Exception;

	public List<IOpenGisCoordinate> getOpenGisCoordinates(int entityId) throws Exception;
	
	public IOpenGisMapObject getOpenGisMapByDefinitionId(int mapDefinitionId) throws Exception;
}
