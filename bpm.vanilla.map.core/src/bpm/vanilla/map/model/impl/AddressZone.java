package bpm.vanilla.map.model.impl;

import bpm.vanilla.map.core.design.IAddressZone;

public class AddressZone implements IAddressZone{
	
	private Integer id;
	private Integer addressId;
	private Long mapZoneId;

	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public Integer getAddressId() {
		return addressId;
	}
	
	@Override
	public Long getMapZoneId() {
		return mapZoneId;
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
	public void setMapZoneId(Long mapZoneId) {
		this.mapZoneId = mapZoneId;
	}
	
}
