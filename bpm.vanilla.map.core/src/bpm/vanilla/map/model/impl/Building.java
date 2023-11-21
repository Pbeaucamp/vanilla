package bpm.vanilla.map.model.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.map.core.design.IBuilding;
import bpm.vanilla.map.core.design.ICell;
import bpm.vanilla.map.core.design.IBuildingFloor;
import bpm.vanilla.map.core.design.IImage;

public class Building implements IBuilding{
	
	private Integer id;
	private Integer addressId;
	private String label = "";
	private String type;
	private Double latitude = -1.00;
	private Double longitude = -1.00;
	private Double altitude = -1.00;
	private Double surface = -1.00;
	private Double sizeX = -1.00;
	private Double sizeY = -1.00;
	private Integer nbFloors = 0;
	private Integer imageId;
	private IImage image;
	private List<IBuildingFloor> floors = new ArrayList<IBuildingFloor>();
	private List<ICell> cells = new ArrayList<ICell>();

	public Building(){
		
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public List<ICell> getCells() {
		return cells;
	}

	@Override
	public List<IBuildingFloor> getFloors() {
		return floors;
	}

	@Override
	public IImage getImage() {
		return image;
	}
	
	public Integer getAddressId() {
		return addressId;
	}

	@Override
	public Double getLatitude() {
		return latitude;
	}

	@Override
	public Double getLongitude() {
		return longitude;
	}

	@Override
	public Integer getNbFloors() {
		return nbFloors;
	}

	@Override
	public Double getSizeX() {
		return sizeX;
	}

	@Override
	public Double getSizeY() {
		return sizeY;
	}

	@Override
	public Double getSurface() {
		return surface;
	}

	@Override
	public String getType() {
		return type;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	
	public void addFloor(IBuildingFloor floor){
		floors.add(floor);
	}
	
	public void addCell(ICell cell){
		cells.add(cell);
	}
	
	public void setImage(IImage image){
		this.image = image;
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
	
	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public void setNbFloors(int nbFloors) {
		this.nbFloors = nbFloors;
	}
	
	public void setSizeX(Double sizeX) {
		this.sizeX = sizeX;
	}
	
	public void setSizeY(Double sizeY) {
		this.sizeY = sizeY;
	}
	
	public void setSurface(Double surface) {
		this.surface = surface;
	}
	
	public void setCells(List<ICell> cells) {
		this.cells = cells;
	}
	
	public void setFloors(List<IBuildingFloor> floors) {
		this.floors = floors;
	}
	
	public void removeFloor(IBuildingFloor floor){
		floors.remove(floor);
	}
	
	public void removeCell(ICell cell){
		cells.remove(cell);
	}

	@Override
	public Double getAltitude() {
		return altitude;
	}

	@Override
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}
}
