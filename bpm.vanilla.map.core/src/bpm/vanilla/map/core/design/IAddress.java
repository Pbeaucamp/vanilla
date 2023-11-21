package bpm.vanilla.map.core.design;

import java.util.List;

import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;

public interface IAddress {
	
	/**
	 * constant for type of an IAddress
	 */
	public static final String HOUSE = "House";
	public static final String BUILDING = "Building";
	public static final String STREET = "Street";
	public static final String NEIGHBORHOOD = "Neighborhood";
	public static final String COUNTY = "County";
	public static final String CITY = "City";
	public static final String STATE = "State";
	public static final String COUNTRY = "Country";
	public static final String CONTINENT = "Continent";
	
	public static final String[] ADDRESS_TYPES = {HOUSE, BUILDING, STREET, NEIGHBORHOOD, COUNTY, 
		CITY, STATE, COUNTRY, CONTINENT};

	/**
	 * 
	 * @return the IAddress Id
	 */
	public Integer getId();
	
	/**
	 * 
	 * @return the IAddress Label
	 */
	public String getLabel();
	
	/**
	 * 
	 * @return the IAddress type
	 */
	public String getAddressType();
	
	/**
	 * 
	 * @return the first street of an IAddress
	 */
	public String getStreet1();
	
	/**
	 * 
	 * @return the second street of an IAddress
	 */
	public String getStreet2();
	
	/**
	 * 
	 * @return the bloc of an IAddress
	 */
	public String getBloc();
	
	/**
	 * 
	 * @return the arrondissement of an IAddress
	 */
	public String getArrondissement();
	
	/**
	 * 
	 * @return the IAddress zipCode
	 */
	public Integer getZipCode();
	
	/**
	 * 
	 * @return the IAddress INSEECode
	 */
	public Integer getINSEECode();
	
	/**
	 * 
	 * @return the IAddress city
	 */
	public String getCity();
	
	/**
	 * 
	 * @return the IAddress country
	 */
	public String getCountry();
	
	/**
	 * 
	 * @return the list of Child for an IAddress
	 */	
	public List<IAddress> getAddressChild();
	
	/**
	 * 
	 * @return the list of Child for an IAddress
	 */	
	public boolean hasChild();
	
	/**
	 * 
	 * @return the list of IBuilding for an IAddress
	 */	
	public List<IBuilding> getBuildings();
	
	public IAddressRelation getAddressRelation();
	
	public List<IFusionMapSpecificationEntity> getAddressZones();
	
	public List<IMapDefinition> getMaps();
	

	public void setId(int id);
	
	public void setLabel(String text);
	
	public void setAddressType(String addressType);

	public void setStreet1(String text);

	public void setStreet2(String text);

	public void setBloc(String text);

	public void setArrondissement(String text);

	public void setINSEECode(Integer inseeCode);

	public void setCity(String text);

	public void setCountry(String text);

	public void setZipCode(Integer zipCode);
	
	public void setHasChild(boolean hasChild);
	
	public void setAddressChild(List<IAddress> addressChilds);
	
	public void addAddressChild(IAddress addressChild);
	
	public void setAddressRelation(IAddressRelation relation);
	
	public void addAddressZones(IFusionMapSpecificationEntity zone);
	
	public void setAddressZones(List<IFusionMapSpecificationEntity> zones);
	
	public void removeAddressZones(IFusionMapSpecificationEntity zone);
	
	public void addMap(IMapDefinition mapDefinition);
	
	public void setMaps(List<IMapDefinition> maps);
	
	public void removeMaps(IMapDefinition mapDefinition);
}
