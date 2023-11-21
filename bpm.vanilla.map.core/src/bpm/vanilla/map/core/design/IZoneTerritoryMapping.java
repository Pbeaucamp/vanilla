package bpm.vanilla.map.core.design;

/**
 * 
 * This interface is used to create mapping between a map zone and a FreeMetrics application (territory)
 * 
 * @author Marc Lanquetin
 *
 */
public interface IZoneTerritoryMapping {

	public Integer getId();
	
	public void setId(Integer id);
	
	public void setMapZoneId(Integer mapZoneId);
	
	public Integer getMapZoneId();
	
	public Integer getTerritoryId();
	
	public void setTerritoryId(Integer territoryId);
	
	public void setMapId(Integer mapId);
	
	public Integer getMapId();
}
