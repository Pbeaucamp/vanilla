package bpm.vanilla.map.core.design.fusionmap;

import java.util.List;

/**
 * This interface represent a FusionMap Object.
 * 
 * A FusionMapObject is :
 *  - a SWF file located by an URL matching to the map
 *  - general information (name, description, ....)
 *  - a List of IFusionMapSpecificationEntity which describe the Specification Sheet from the FusionMap documentation
 * 
 * @author ludo
 *
 */
public interface IFusionMapObject {
	
	public static final String FUSIONMAP_TYPE = "Fusion Map";
	public static final String VANILLAMAP_TYPE = "Vanilla Map";
	
	/**
	 * The path to the swf File
	 */
	public static final String FUSION_MAP_PATH = "/fusionMap/Maps/";
	
	/**
	 * the id representing this FusionMapObject within an IFusionMapRegistry
	 * @return
	 */
	public long getId();
	public void setId(long id);
	
	/**
	 * the name of this FusionMapObject
	 * @return
	 */
	public String getName();
	public void setName(String name);
	
	/**
	 * 
	 * @return the fileName of the SWF files stored in the storage system
	 */
	public String getSwfFileName();
	
	/**
	 * 
	 * @param the fileName of the SWF files stored in the storage system
	 */
	public void setSwfFileName(String swfUrl);
	
	/**
	 * 
	 * @return a description of this FusionMapObject
	 */
	public String getDescription();
	public void setDescription(String description);
	

	public List<IFusionMapSpecificationEntity> getSpecificationsEntities();
	
	public void addSpecificationEntity(IFusionMapSpecificationEntity entity);
	
	public void removeSpecificationEntity(IFusionMapSpecificationEntity entity);
	
	/**
	 * Used to know if the map is a fusionMap or a vanillaMap
	 * @param type can be "FusionMap" or "VanillaMapFlash"
	 */
	public void setType(String type);
	
	public String getType();
	
	
}
