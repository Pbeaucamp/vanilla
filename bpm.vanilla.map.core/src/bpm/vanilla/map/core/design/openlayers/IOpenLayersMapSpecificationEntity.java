package bpm.vanilla.map.core.design.openlayers;

public interface IOpenLayersMapSpecificationEntity {

	public Integer getId();
	
	public void setId(Integer id);
	
	public Integer getOpenLayersMapObjectId();
	
	public void setOpenLayersMapObjectId(Integer openLayersMapObjectId);
	
	public String getInternalId();
	
	public void setInternalId(String internalId);
	
	public String getShortName();
	
	public void setShortName(String shortName);
	
	public String getLongName();
	
	public void setLongName(String longName);
	
}
