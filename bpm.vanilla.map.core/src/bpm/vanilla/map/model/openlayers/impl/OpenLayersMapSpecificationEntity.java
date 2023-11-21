package bpm.vanilla.map.model.openlayers.impl;

import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapSpecificationEntity;

public class OpenLayersMapSpecificationEntity implements IOpenLayersMapSpecificationEntity {

	private Integer id;
	private Integer openLayersMapObjectId;
	private String internalId;
	private String shortName;
	private String longName;
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getInternalId() {
		return internalId;
	}

	@Override
	public String getLongName() {
		return longName;
	}

	@Override
	public Integer getOpenLayersMapObjectId() {
		return openLayersMapObjectId;
	}

	@Override
	public String getShortName() {
		return shortName;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	@Override
	public void setLongName(String longName) {
		this.longName = longName;
	}

	@Override
	public void setOpenLayersMapObjectId(Integer openLayersMapObjectId) {
		this.openLayersMapObjectId = openLayersMapObjectId;
	}

	@Override
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}
