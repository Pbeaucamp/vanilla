package bpm.vanilla.map.model.impl;

import bpm.vanilla.map.core.design.IAddressMapDefinitionRelation;

public class AddressMapDefinitionRelation implements IAddressMapDefinitionRelation{
	
	private Integer id;
	private Integer addressId;
	private Integer mapDefinitionId;

	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public Integer getAddressId() {
		return addressId;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	@Override
	public Integer getMapDefinitionId() {
		return mapDefinitionId;
	}

	@Override
	public void setMapDefinitionId(Integer mapDefinitionId) {
		this.mapDefinitionId = mapDefinitionId;
	}
	
}
