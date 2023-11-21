package bpm.vanilla.map.core.design;

import java.io.Serializable;
import java.util.List;

public class MapVanilla implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private String name;
	private String description;

	private int zoom;

	private Double originLat;
	private Double originLong;
	private Double boundLeft;
	private Double boundBottom;
	private Double boundRight;
	private Double boundTop;

	private String projection;

	private String type = "database";
	
	private List<MapDataSet> dataSetList;

	public MapVanilla() {

	}

	public MapVanilla(String name, String description, int zoom, Double originLat, Double originLong, Double boundLeft, Double boundBottom, Double boundRight, Double boundTop, String projection, List<MapDataSet> dataSetList) {
		this.name = name;
		this.description = description;
		this.zoom = zoom;
		this.originLat = originLat;
		this.originLong = originLong;
		this.boundLeft = boundLeft;
		this.boundBottom = boundBottom;
		this.boundRight = boundRight;
		this.boundTop = boundTop;
		this.projection = projection;
		this.dataSetList = dataSetList;

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	public Double getOriginLat() {
		return originLat;
	}

	public void setOriginLat(Double originLat) {
		this.originLat = originLat;
	}

	public Double getOriginLong() {
		return originLong;
	}

	public void setOriginLong(Double originLong) {
		this.originLong = originLong;
	}

	public Double getBoundLeft() {
		return boundLeft;
	}

	public void setBoundLeft(Double boundLeft) {
		this.boundLeft = boundLeft;
	}

	public Double getBoundBottom() {
		return boundBottom;
	}

	public void setBoundBottom(Double boundBottom) {
		this.boundBottom = boundBottom;
	}

	public Double getBoundRight() {
		return boundRight;
	}

	public void setBoundRight(Double boundRight) {
		this.boundRight = boundRight;
	}

	public Double getBoundTop() {
		return boundTop;
	}

	public void setBoundTop(Double boundTop) {
		this.boundTop = boundTop;
	}

	public String getProjection() {
		return projection;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<MapDataSet> getDataSetList() {
		return dataSetList;
	}

	public void setDataSetList(List<MapDataSet> dataSetList) {
		this.dataSetList = dataSetList;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		MapVanilla other = (MapVanilla) obj;
		if(id != other.id)
			return false;
		return true;
	}
	
	
}
