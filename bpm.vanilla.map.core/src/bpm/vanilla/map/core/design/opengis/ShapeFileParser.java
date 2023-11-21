package bpm.vanilla.map.core.design.opengis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

import bpm.vanilla.map.core.design.openlayers.OpenLayersPoint;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

public class ShapeFileParser {

	public static List<IOpenGisMapEntity> parseShapeFile(String filePath, IOpenGisMapObject map) throws Exception {
		if(map != null){
			return parseShapeFile(filePath, map.getId());
		}
		else {
			return parseShapeFile(filePath, -1);
		}
	}

	public static List<IOpenGisMapEntity> parseShapeFile(String filePath, int mapId) throws Exception {
		
		File file = new File(filePath);
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();
		SimpleFeatureIterator it = featureSource.getFeatures().features();
      
		List<IOpenGisMapEntity> entities = new ArrayList<IOpenGisMapEntity>();
		
		while(it.hasNext()) {
			SimpleFeature f =  it.next();
			Geometry geometry = (Geometry)f.getDefaultGeometry();
			
			OpenGisMapEntity entity = new OpenGisMapEntity();
			entity.setEntityId(f.getID());
			if(mapId != -1) {
				entity.setMapId(mapId);
			}
			if(geometry != null) {
				entity.setType(geometry.getGeometryType());
				List<IOpenGisCoordinate> coordinates = new ArrayList<IOpenGisCoordinate>();
				if(geometry.getCoordinates() != null){
					for(Coordinate co : geometry.getCoordinates()){
						coordinates.add(new OpenGisCoordinate(co.x, co.y, co.z));
					}
				}
				entity.setCoordinates(coordinates);
			}
			try {
				entity.setName(f.getAttribute("NAME").toString());
			} catch(Exception e) {
			}
			
			entities.add(entity);
		}
		
		return entities;
	}
	
	public static HashMap<String, List<OpenLayersPoint>> getZonePoints(IOpenGisMapObject map) throws Exception {
		HashMap<String, List<OpenLayersPoint>> pointsByZone = new HashMap<String, List<OpenLayersPoint>>();
		
		if(map.getDatasource() instanceof OpenGisShapeFileDatasource) {
			OpenGisShapeFileDatasource ds = (OpenGisShapeFileDatasource) map.getDatasource();
			
			
	        FileDataStore store = FileDataStoreFinder.getDataStore(new File(ds.getFilePath()));
	        SimpleFeatureSource featureSource = store.getFeatureSource();
	        SimpleFeatureIterator it = featureSource.getFeatures().features();
	        
	        
	        while(it.hasNext()) {
	        	SimpleFeature f =  it.next();
	        	List<OpenLayersPoint> points = new ArrayList<OpenLayersPoint>();
	        	
	        	Iterator<? extends Property> pIt = f.getValue().iterator();
	        	while(pIt.hasNext()) {
	        		MultiPolygon pol = (MultiPolygon) pIt.next();
	              	for(Coordinate c : pol.getCoordinates()) {	
	            		double x = c.x;
	            		double y = c.y;
	            		OpenLayersPoint p = new OpenLayersPoint(x+"", y+"");
	            		points.add(p);
	              	}
	        	}
	        	
	        	pointsByZone.put(f.getIdentifier().getID(), points);
	        }
			
		}
		
		return pointsByZone;
	}
	
}
