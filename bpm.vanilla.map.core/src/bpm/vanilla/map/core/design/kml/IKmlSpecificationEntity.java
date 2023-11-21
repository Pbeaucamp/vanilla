package bpm.vanilla.map.core.design.kml;

/**
 * 
 * This interface defines a Zone (or Point) in a IKmlObject
 * 
 * @author Seb
 *
 */

public interface IKmlSpecificationEntity {
	
	public static final String POINT = "Point";
	public static final String LINE_STRING = "LineString";
	public static final String POLYGON = "Polygon";
	
	/**
	 * 
	 * @return the IMapZone Id
	 */
	public Integer getId();
	
	/**
	 * 
	 * @return the IKmlObject id
	 */
	public Integer getKmlObjectId();
	
	/**
	 * 
	 * @return the IMapDefinition Placemark Id
	 */
	public String getPlacemarkId();
	
	/**
	 * 
	 * @return the IMapDefinition Placemark Id
	 */
	public String getPlacemarkType();
	
	public void setId(Integer id);
	
	public void setKmlObjectId(Integer kmlObjectId);
	
	public void setPlacemarkId(String placemarkId);
	
	public void setPlacemarkType(String placemarkType);
}
