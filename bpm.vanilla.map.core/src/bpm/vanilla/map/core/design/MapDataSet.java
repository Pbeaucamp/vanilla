package bpm.vanilla.map.core.design;

import java.io.Serializable;

public class MapDataSet implements Serializable {

	private static final long serialVersionUID = 1L;
	private String query, latitude, longitude, idZone, name, parent, type, markerUrl, zoneLabel, parentName;

	private int idMapVanilla;
	private int id, idDataSource, markerSizeMin, markerSizeMax, points;
	private MapDataSource dataSource;
	private Integer parentId;
	
	public MapDataSet() {
	}

	public MapDataSet(int id, String name, String query, String idZone, String latitude,
			String longitude, String zoneLabel, MapDataSource dataSource, String parent, String type, int idMapVanilla) {
		this.name = name;
		this.query = query;
		this.latitude = latitude;
		this.longitude = longitude;
		this.idZone = idZone;
		this.id = id;
		this.dataSource = dataSource;
		this.parent = parent;
		this.type = type;
		this.idMapVanilla = idMapVanilla;
		this.zoneLabel = zoneLabel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getIdZone() {
		return idZone;
	}

	public void setIdZone(String idZone) {
		this.idZone = idZone;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MapDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(MapDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int getIdDataSource() {
		return idDataSource;
	}

	public void setIdDataSource(int idDataSource) {
		this.idDataSource = idDataSource;
	}
	
	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getIdMapVanilla() {
		return idMapVanilla;
	}

	public void setIdMapVanilla(int idMapVanilla) {
		this.idMapVanilla = idMapVanilla;
	}

	public String getMarkerUrl() {
		return markerUrl;
	}

	public void setMarkerUrl(String markerUrl) {
		this.markerUrl = markerUrl;
	}

	public int getMarkerSizeMin() {
		return markerSizeMin;
	}

	public void setMarkerSizeMin(int markerSizeMin) {
		this.markerSizeMin = markerSizeMin;
	}

	public int getMarkerSizeMax() {
		return markerSizeMax;
	}

	public void setMarkerSizeMax(int markerSizeMax) {
		this.markerSizeMax = markerSizeMax;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getZoneLabel() {
		return zoneLabel;
	}

	public void setZoneLabel(String zoneLabel) {
		this.zoneLabel = zoneLabel;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	@Override
	public String toString() {
		return name;
	}

	
}
