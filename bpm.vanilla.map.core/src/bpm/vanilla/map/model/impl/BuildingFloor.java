package bpm.vanilla.map.model.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.map.core.design.IBuildingFloor;
import bpm.vanilla.map.core.design.ICell;
import bpm.vanilla.map.core.design.IImage;

public class BuildingFloor implements IBuildingFloor{
	
	private Integer id;
	private String label;
	private Integer buildingId;
	private Integer level;
	private Integer height;
	private IImage image;
	private Integer imageId;
	private List<ICell> cells = new ArrayList<ICell>();
	
	@Override
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public void addCell(ICell cell) {
		this.cells.add(cell);
	}

	@Override
	public Integer getBuildingId() {
		return buildingId;
	}

	@Override
	public List<ICell> getCells() {
		return cells;
	}

	@Override
	public Integer getHeight() {
		return height;
	}

	@Override
	public IImage getImage() {
		return image;
	}

	@Override
	public Integer getLevel() {
		return level;
	}

	@Override
	public void removeCell(ICell cell) {
		this.cells.remove(cell);
	}

	@Override
	public void setBuildingId(Integer id) {
		this.buildingId = id;
	}

	@Override
	public void setCells(List<ICell> cells) {
		this.cells = cells;
	}

	@Override
	public void setHeight(Integer height) {
		this.height = height;
	}

	@Override
	public void setImage(IImage image) {
		this.image = image;
	}

	@Override
	public void setLevel(Integer level) {
		this.level = level;
	}

	@Override
	public Integer getImageId() {
		return imageId;
	}

	@Override
	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

}
