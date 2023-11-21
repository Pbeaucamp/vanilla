package bpm.vanilla.map.model.kml.impl;

import bpm.vanilla.map.core.design.kml.IKmlSpecificationEntity;

public class KmlSpecificationEntity implements IKmlSpecificationEntity {

	private Integer id;
	private Integer kmlObjectId;
	private String placemarkId;
	private String placemarkType;
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getPlacemarkId() {
		return placemarkId;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public void setPlacemarkId(String placemarkId) {
		this.placemarkId = placemarkId;
	}

	@Override
	public String getPlacemarkType() {
		return placemarkType;
	}

	@Override
	public void setPlacemarkType(String placemarkType) {
		this.placemarkType = placemarkType;
	}

	@Override
	public Integer getKmlObjectId() {
		return kmlObjectId;
	}

	@Override
	public void setKmlObjectId(Integer kmlObjectId) {
		this.kmlObjectId = kmlObjectId;
	}

}
