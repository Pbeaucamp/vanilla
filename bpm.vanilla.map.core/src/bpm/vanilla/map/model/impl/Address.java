package bpm.vanilla.map.model.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IAddressMapDefinitionRelation;
import bpm.vanilla.map.core.design.IAddressRelation;
import bpm.vanilla.map.core.design.IBuilding;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;

public class Address implements IAddress{
	
	private Integer id;
	private String label;
	private String addressType;
	private String street1;
	private String street2;
	private String bloc;
	private String arrondissement;
	private Integer zipCode;
	private Integer inseeCode;
	private String city;
	private String country;
	private IAddressRelation relation;
	private boolean hasChild;
	private List<IAddress> addressChilds = new ArrayList<IAddress>();
	private List<IBuilding> buildings = new ArrayList<IBuilding>();
	private List<IFusionMapSpecificationEntity> zones = new ArrayList<IFusionMapSpecificationEntity>();
	private List<IMapDefinition> maps = new ArrayList<IMapDefinition>();

	@Override
	public Integer getId() {
		return this.id;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getStreet1() {
		return street1;
	}
	
	@Override
	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	@Override
	public String getStreet2() {
		return street2;
	}
	
	@Override
	public void setStreet2(String street2) {
		this.street2 = street2;
	}
	
	@Override
	public String getBloc() {
		return bloc;
	}
	
	@Override
	public void setBloc(String bloc) {
		this.bloc = bloc;
	}
	
	@Override
	public String getArrondissement() {
		return arrondissement;
	}
	
	@Override
	public void setArrondissement(String arrondissement) {
		this.arrondissement = arrondissement;
	}

	@Override
	public Integer getZipCode() {
		return zipCode;
	}
	@Override
	public void setZipCode(Integer zipCode) {
		this.zipCode = zipCode;
	}
	
	@Override
	public Integer getINSEECode() {
		return inseeCode;
	}

	@Override
	public String getCity() {
		return city;
	}
	
	@Override
	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String getCountry() {
		return country;
	}
	
	@Override
	public void setCountry(String country) {
		this.country = country;
	}
	
	@Override
	public List<IBuilding> getBuildings() {
		return buildings;
	}
	
	public void setBuildings(List<IBuilding> buildings) {
		this.buildings = buildings;
	}
	
	public void addBuilding(IBuilding building){
		buildings.add(building);
	}

	@Override
	public String getAddressType() {
		return addressType;
	}

	@Override
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	@Override
	public void setINSEECode(Integer inseeCode) {
		this.inseeCode = inseeCode;
	}

	@Override
	public void addAddressChild(IAddress addressChild) {
		this.addressChilds.add(addressChild);
	}

	@Override
	public List<IAddress> getAddressChild() {
		return addressChilds;
	}

	@Override
	public void setAddressChild(List<IAddress> addressChilds) {
		this.addressChilds = addressChilds;
	}

	@Override
	public boolean hasChild() {
		return hasChild;
	}

	@Override
	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}

	@Override
	public IAddressRelation getAddressRelation() {
		return relation;
	}

	@Override
	public void setAddressRelation(IAddressRelation relation) {
		this.relation = relation;
	}

	@Override
	public void addAddressZones(IFusionMapSpecificationEntity zone) {
		this.zones.add(zone);
	}

	@Override
	public List<IFusionMapSpecificationEntity> getAddressZones() {
		return zones;
	}

	@Override
	public void setAddressZones(List<IFusionMapSpecificationEntity> zones) {
		this.zones = zones;
	}

	@Override
	public void removeAddressZones(IFusionMapSpecificationEntity zone) {
		this.zones.remove(zone);
	}

	@Override
	public void addMap(IMapDefinition mapDefinition) {
		this.maps.add(mapDefinition);
	}

	@Override
	public List<IMapDefinition> getMaps() {
		return maps;
	}

	@Override
	public void removeMaps(IMapDefinition mapDefinition) {
		this.maps.remove(mapDefinition);
	}

	@Override
	public void setMaps(List<IMapDefinition> maps) {
		this.maps = maps;
	}
}
