package bpm.vanilla.map.model.openlayers.impl;

import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObjectProperty;

public class OpenLayersMapObjectProperty implements IOpenLayersMapObjectProperty {

	private Integer id;
	private Integer openLayersMapObjectId;
	private String name;
	private String value;
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Integer getOpenLayersMapObjectId() {
		return openLayersMapObjectId;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setOpenLayersMapObjectId(Integer id) {
		this.openLayersMapObjectId = id;		
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

}
