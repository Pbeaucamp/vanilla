package bpm.vanilla.map.dao.opengis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import bpm.vanilla.map.core.design.IOpenGisMapService;
import bpm.vanilla.map.core.design.opengis.IOpenGisCoordinate;
import bpm.vanilla.map.core.design.opengis.IOpenGisDatasource;
import bpm.vanilla.map.core.design.opengis.IOpenGisMapEntity;
import bpm.vanilla.map.core.design.opengis.IOpenGisMapObject;
import bpm.vanilla.map.core.design.opengis.OpenGisShapeFileDatasource;
import bpm.vanilla.map.core.design.opengis.ShapeFileParser;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;


public class OpenGisMapDAO extends HibernateDaoSupport implements IOpenGisMapService {
	private String shapeFileLocation;

	@SuppressWarnings("unchecked")
	private IOpenGisDatasource getDatasource(IOpenGisMapObject map) throws Exception {
		List<IOpenGisDatasource> ds = getHibernateTemplate().find("from OpenGisShapeFileDatasource where id = " + map.getDatasourceId());
		if(ds != null && !ds.isEmpty()){
			return ds.get(0);
		}
		return null;
	}

	@Override
	public void addOpenGisMap(IOpenGisMapObject map) throws Exception {
		if(map.getDatasource() != null){
			getHibernateTemplate().save(map.getDatasource());
			map.setDatasourceId(map.getDatasource().getId());
		}
		getHibernateTemplate().save(map);
		for(IOpenGisMapEntity entity : map.getEntities()) {
			entity.setMapId(map.getId());
			getHibernateTemplate().save(entity);
			
			if(entity.getCoordinates() != null){
				for(IOpenGisCoordinate coor : entity.getCoordinates()){
					coor.setEntityId(entity.getId());
					getHibernateTemplate().save(coor);
				}
			}
		}
	}

	@Override
	public void deleteOpenGisMap(IOpenGisMapObject map) throws Exception {
		for(IOpenGisMapEntity entity : map.getEntities()) {
			
			if(entity.getCoordinates() != null){
				for(IOpenGisCoordinate coor : entity.getCoordinates()){
					getHibernateTemplate().delete(coor);
				}
			}
			
			getHibernateTemplate().delete(entity);
		}
		getHibernateTemplate().delete(map.getDatasource());
		getHibernateTemplate().delete(map);
	}

	@Override
	@SuppressWarnings("unchecked")
	public IOpenGisMapObject getOpenGisMapById(int mapId) throws Exception {
		List<IOpenGisMapObject> maps = getHibernateTemplate().find("from OpenGisMapObject where id=" + mapId);
		if(!maps.isEmpty()){
			IOpenGisMapObject map = maps.get(0);
			map.setEntities(getOpenGisMapEntities(map));
			map.setDatasource(getDatasource(map));
			return map;
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<IOpenGisMapEntity> getOpenGisMapEntities(IOpenGisMapObject map) throws Exception {
		List<IOpenGisMapEntity> entities = getHibernateTemplate().find("from OpenGisMapEntity where mapId = " + map.getId());
		for(IOpenGisMapEntity entity : entities){
			entity.setCoordinates(getOpenGisCoordinates(entity.getId()));
		}
		return entities;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<IOpenGisCoordinate> getOpenGisCoordinates(int entityId) throws Exception {
		List<IOpenGisCoordinate> coordinates = getHibernateTemplate().find("from OpenGisCoordinate where entityId = " + entityId);
		return coordinates;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<IOpenGisMapObject> getOpenGisMaps() throws Exception {
		List<IOpenGisMapObject> maps = getHibernateTemplate().find("from OpenGisMapObject");
		
		for(IOpenGisMapObject map : maps) {
			map.setEntities(getOpenGisMapEntities(map));
			map.setDatasource(getDatasource(map));
		}
		
		return maps;
	}

	@Override
	public void updateOpenGisMap(IOpenGisMapObject map) throws Exception {
		//Update existing entities and create new ones
		for(IOpenGisMapEntity entity : map.getEntities()) {
			getHibernateTemplate().saveOrUpdate(entity);
			
			if(entity.getCoordinates() != null){
				for(IOpenGisCoordinate coor : entity.getCoordinates()){
					getHibernateTemplate().saveOrUpdate(coor);
				}
			}
		}
		
		//Update or create a new datasource
		getHibernateTemplate().saveOrUpdate(map.getDatasource());
		getHibernateTemplate().update(map);
	}

	public void setShapeFileLocation(String shapeFileLocation) {
		this.shapeFileLocation = shapeFileLocation;
	}

	public String getShapeFileLocation() {
		return shapeFileLocation;
	}

	@Override
	public void addOpenGisMap(IOpenGisMapObject map, InputStream fileShape) throws Exception {
		if(shapeFileLocation == null || shapeFileLocation.isEmpty()){
			throw new Exception("The variable to the Shape File Location is not define into the vanilla.properties file.");
		}
		String filePath = shapeFileLocation + "/" + map.getName() + ".shp";
		saveFile(filePath, fileShape);
		
		OpenGisShapeFileDatasource ds = new OpenGisShapeFileDatasource();
		ds.setFilePath(filePath);
		ds.setFormat(OpenGisShapeFileDatasource.FORMAT_SHAPE);
		
		List<IOpenGisMapEntity> entities = ShapeFileParser.parseShapeFile(filePath, map);
		
		map.setDatasource(ds);
		map.setEntities(entities);
		
		addOpenGisMap(map);
	}
	
	private void saveFile(String filePath, InputStream fileShape) throws Exception{
		OutputStream out = new FileOutputStream(new File(filePath));
	 
		int read = 0;
		byte[] bytes = new byte[1024];
	 
		while ((read = fileShape.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
	 
		fileShape.close();
		out.flush();
		out.close();
	}

	public void saveEntities(List<IOpenGisMapEntity> entities) throws Exception {
		if(entities != null){
			for(IOpenGisMapEntity entity : entities) {
				getHibernateTemplate().save(entity);
				
				if(entity.getCoordinates() != null){
					for(IOpenGisCoordinate coor : entity.getCoordinates()){
						coor.setEntityId(entity.getId());
						getHibernateTemplate().save(coor);
					}
				}
			}
		}
	}

	@Override
	public IOpenGisMapObject getOpenGisMapByDefinitionId(int mapDefinitionId) throws Exception {
		List<IOpenGisMapObject> maps = getHibernateTemplate().find("from OpenGisMapObject where mapDefinitionId=" + mapDefinitionId);
		if(!maps.isEmpty()){
			IOpenGisMapObject map = maps.get(0);
			map.setEntities(getOpenGisMapEntities(map));
			map.setDatasource(getDatasource(map));
			return map;
		}
		return null;
	}

	@Override
	public void saveShapeFile(int openGisMapId, InputStream fis) throws Exception {
		if(shapeFileLocation == null || shapeFileLocation.isEmpty()){
			throw new Exception("The variable to the Shape File Location is not define into the vanilla.properties file.");
		}
		IOpenGisMapObject map = getOpenGisMapById(openGisMapId);
		if(map == null){
			throw new Exception("Couldn't find the Open GIS Map with id " + openGisMapId + ". Please, check that it exist.");
		}
		
		String filePath = shapeFileLocation + map.getName() + ".shp";
		saveFile(filePath, fis);
		
		OpenGisShapeFileDatasource ds = new OpenGisShapeFileDatasource();
		ds.setFilePath(filePath);
		ds.setFormat(OpenGisShapeFileDatasource.FORMAT_SHAPE);
		map.setDatasource(ds);
		
		getHibernateTemplate().save(ds);
		getHibernateTemplate().update(map);
		
		List<IOpenGisMapEntity> entities = ShapeFileParser.parseShapeFile(filePath, openGisMapId);
		saveEntities(entities);
	}

}
