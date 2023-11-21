package bpm.vanilla.map.model.fusionmap.impl;

import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;

public class FusionMapSpecificationEntity implements IFusionMapSpecificationEntity{

	private long id;
	private long fusionMapObjectId;
	private String fusionMapInternalId;
	private String fusionMapShortName;
	private String fusionMapLongName;
	
	@Override
	public String getFusionMapInternalId() {
		return fusionMapInternalId;
	}

	@Override
	public void setFusionMapInternalId(String internalId) {
		this.fusionMapInternalId = internalId;
	}

	@Override
	public String getFusionMapLongName() {
		return fusionMapLongName;
	}

	@Override
	public void setFusionMapLongName(String longName) {
		this.fusionMapLongName = longName;
		
	}

	@Override
	public String getFusionMapShortName() {
		return fusionMapShortName;
	}

	@Override
	public void setFusionMapShortName(String shortName) {
		this.fusionMapShortName = shortName;
		
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
		
	}

	@Override
	public long getFusionMapObjectId() {
		return fusionMapObjectId;
	}

	@Override
	public void setFusionMapObjectId(long fusionMapObjectId) {
		this.fusionMapObjectId = fusionMapObjectId;
		
	}

}
