package bpm.vanilla.map.core.design;

public interface ICell {
	
	/**
	 * 
	 * @return the ICell Id
	 */
	public Integer getId();
	
	/**
	 * 
	 * @return the ICell label
	 */
	public String getLabel();
	
	/**
	 * 
	 * @return the ICell X position
	 */
	public Double getPositionX();
	
	/**
	 * 
	 * @return the ICell Y position
	 */
	public Double getPositionY();
	
	/**
	 * 
	 * @return the ICell surface
	 */
	public Double getSurface();
	
	/**
	 * 
	 * @return the IImage attached to the IBuilding
	 */	
	public IImage getImage();

	public void setFloorId(Integer floorId);

	public void setBuildingId(Integer buildingId);

	public void setImageId(Integer imageId);

	public void setId(int id);

	public Integer getImageId();

	public void setImage(IImage iImage);

	public void setLabel(String string);

	public Integer getFloorId();

	public void setPositionX(Double parseDouble);

	public void setPositionY(Double parseDouble);

	public void setSurface(Double parseDouble);
}
