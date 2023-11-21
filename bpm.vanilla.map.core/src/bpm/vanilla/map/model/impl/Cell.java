package bpm.vanilla.map.model.impl;

import bpm.vanilla.map.core.design.ICell;
import bpm.vanilla.map.core.design.IImage;

public class Cell implements ICell{
	
	private Integer id;
	private String label;
	private Integer buildingId;
	private Integer floorId;
	private Double positionX;
	private Double positionY;
	private Double surface;
	private Integer imageId;
	private IImage image;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public IImage getImage() {
		return image;
	}
	
	@Override
	public Double getPositionX() {
		return positionX;
	}
	
	@Override
	public Double getPositionY() {
		return positionY;
	}
	
	@Override
	public Double getSurface() {
		return surface;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setBuildingId(Integer buildingId) {
		this.buildingId = buildingId;
	}
	
	public void setFloorId(Integer floorId) {
		this.floorId = floorId;
	}
	
	public void setImage(IImage image){
		this.image = image;
	}
	
	public void setPositionX(Double positionX) {
		this.positionX = positionX;
	}
	
	public void setPositionY(Double positionY) {
		this.positionY = positionY;
	}
	
	public Integer getBuildingId() {
		return buildingId;
	}
	
	public Integer getFloorId() {
		return floorId;
	}
	
	public Integer getImageId() {
		return imageId;
	}
	
	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public void setSurface(Double surface) {
		this.surface = surface;
	}

	

}
