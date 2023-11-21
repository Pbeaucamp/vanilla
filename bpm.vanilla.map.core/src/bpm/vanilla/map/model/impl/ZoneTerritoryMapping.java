package bpm.vanilla.map.model.impl;

import bpm.vanilla.map.core.design.IZoneTerritoryMapping;

public class ZoneTerritoryMapping implements IZoneTerritoryMapping {

	private Integer id;
	private Integer mapZoneId;
	private Integer territoryId;
	private Integer mapId;
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public Integer getMapZoneId() {
		return mapZoneId;
	}

	@Override
	public Integer getTerritoryId() {
		return territoryId;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public void setMapZoneId(Integer mapZoneId) {
		this.mapZoneId = mapZoneId;
	}

	@Override
	public void setTerritoryId(Integer territoryId) {
		this.territoryId = territoryId;
	}

	@Override
	public Integer getMapId() {
		return mapId;
	}

	@Override
	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}

}
