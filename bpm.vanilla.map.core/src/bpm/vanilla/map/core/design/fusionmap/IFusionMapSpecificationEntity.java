package bpm.vanilla.map.core.design.fusionmap;

/**
 * Base interface to store datas for a FusionMap
 * It match to the FusionMap specification sheet structure
 * @author ludo
 *
 */
public interface IFusionMapSpecificationEntity {
	public long getId();
	public void setId(long id);
	
	/**
	 * 
	 * @return the id of the fusionMapObject on whom this entity apply
	 */
	public long getFusionMapObjectId();
	public void setFusionMapObjectId(long fusionMapObjectId);
	
	public String getFusionMapInternalId();
	public void setFusionMapInternalId(String internalId);
	
	public String getFusionMapShortName();
	public void setFusionMapShortName(String shortName);
	
	public String getFusionMapLongName();
	public void setFusionMapLongName(String longName);
}
