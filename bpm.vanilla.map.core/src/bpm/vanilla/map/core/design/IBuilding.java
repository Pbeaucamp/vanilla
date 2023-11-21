package bpm.vanilla.map.core.design;

import java.util.List;

public interface IBuilding {
	
	/**
	 * constant for type of building
	 */
	public static final String FIELD_TYPE = "Field";
	
	/**
	 * constant for type of building
	 */
	public static final String BUILDING_TYPE = "Building";
	
	/**
	 * constant for type of building
	 */
	public static final String HOUSE_TYPE = "House";
	
	public static final String[] BUILDING_TYPES = {FIELD_TYPE, BUILDING_TYPE, HOUSE_TYPE};
	
	/**
	 * 
	 * @return the IBuilding Id
	 */
	public Integer getId();
	
	/**
	 * 
	 * @return the IBuilding label
	 */
	public String getLabel();
	
	/**
	 * 
	 * @return the IBuilding type
	 */
	public String getType();
	
	/**
	 * 
	 * @return the IBuilding latitude
	 */
	public Double getLatitude();
	
	/**
	 * 
	 * @return the IBuilding longitude
	 */
	public Double getLongitude();
	
	/**
	 * 
	 * @return the IBuilding altitude
	 */
	public Double getAltitude();
	
	/**
	 * 
	 * @return the IBuilding surface
	 */
	public Double getSurface();
	
	/**
	 * 
	 * @return the IBuilding X size
	 */
	public Double getSizeX();
	
	/**
	 * 
	 * @return the IBuilding Y size
	 */
	public Double getSizeY();
	
	/**
	 * 
	 * @return the IBuilding number of level
	 */
	public Integer getNbFloors();
	
	/**
	 * 
	 * @return the IImage attached to the IBuilding
	 */	
	public IImage getImage();
	
	/**
	 * 
	 * @return the list of IFloor for a IBuilding
	 */	
	public List<IBuildingFloor> getFloors();
	
	/**
	 * 
	 * @return the list of ICell for a IBuilding
	 */	
	public List<ICell> getCells();

	public void setImageId(Integer imageId);

	public void setId(int buildingId);

	public void addFloor(IBuildingFloor floor);

	public void addCell(ICell cell);

	public Integer getImageId();

	public void setImage(IImage iImage);

	public void setNbFloors(int i);

	public void removeFloor(IBuildingFloor f);

	public void removeCell(ICell cell);

	public void setAddressId(Integer id);

	public void setLabel(String text);

	public void setType(String text);

	public void setLatitude(Double parseDouble);

	public void setLongitude(Double parseDouble);
	
	public void setAltitude(Double altitude);

	public void setSurface(Double parseDouble);

	public void setSizeX(Double parseDouble);

	public void setSizeY(Double parseDouble);
}
