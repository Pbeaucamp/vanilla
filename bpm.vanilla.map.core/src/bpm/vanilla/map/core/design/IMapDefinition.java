package bpm.vanilla.map.core.design;

import java.util.List;

import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.kml.IKmlObject;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;

public interface IMapDefinition {

	public static final String MAP_TYPE_CLASSIC = "Classic Map";
	public static final String MAP_TYPE_FM = "FreeMetrics Map";
	public static final String MAP_TYPE_FUSION = "Fusion Map";
	public static final String MAP_TYPE_OPEN_GIS = "Open GIS Map";
	public static final String MAP_TYPE_OPEN_LAYER = "Open Layer Map";
	
	/**
	 * Constant for the servletUrl to add fusionMaps or kml file
	 */
	public static final String ADD_FILE_SERVLET = "/AddFileServlet";
	
	/**
	 * constant for type of an IAddress
	 */
	public static final String ZONE = "Zone";
	public static final String MAP = "Map";
	
	public static final String[] MAP_DEFINITION_TYPES = {ZONE, MAP};
	
	/**
	 * 
	 * @return the IMapDefinition Id
	 */
	public Integer getId();
	
	/**
	 * 
	 * @return the label of an IMapDefinition
	 */
	public String getLabel();
	
	/**
	 * 
	 * @return the description of an IMapDefinition
	 */
	public String getDescription();
	
	/**
	 * 
	 * @return the type of an IMapDefinition
	 */
	public String getMapType();
	
	public Integer getKmlObjectId();
	
	public IKmlObject getKmlObject();
	
	public Long getFusionMapObjectId();
	
	public IFusionMapObject getFusionMapObject();
	
	/**
	 * 
	 * @return the list of Child for an IMapDefinition
	 */	
	public List<IMapDefinition> getMapChilds();
	
	public IMapDefinitionRelation getMapDefinitionRelation();
	
	/**
	 * 
	 * @return if the Map has child
	 */	
	public boolean hasChild();

	public void setId(Integer mapDefId);

	public void setLabel(String label);
	
	public void setDescription(String description);
	
	public void setMapType(String mapType);
	
	public void setKmlObjectId(Integer kmlObjectId);
	
	public void setKmlObject(IKmlObject kmlObject);
	
	public void setFusionMapObjectId(Long fusionMapObjectId);
	
	public void setFusionMapObject(IFusionMapObject fusionMapObject);
	
	public void setHasChild(boolean hasChild);
	
	public void setMapChild(List<IMapDefinition> mapChilds);
	
	public void addMapChild(IMapDefinition mapChild);
	
	public void setMapRelation(IMapDefinitionRelation relation);
	
	public void setOpenLayersObjectId(Integer openLayersObjectId);

	public Integer getOpenLayersObjectId();
	
	public IOpenLayersMapObject getOpenLayersMapObject();
	
	public void setOpenLayersMapObject(IOpenLayersMapObject openLayersMapObject);
}
