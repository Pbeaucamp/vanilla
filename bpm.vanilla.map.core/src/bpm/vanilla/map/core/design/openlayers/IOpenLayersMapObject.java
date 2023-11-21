package bpm.vanilla.map.core.design.openlayers;

import java.util.List;

public interface IOpenLayersMapObject {

	public static final String TYPE_WMS = "WMS";
	
	public Integer getId();
	
	public void setId(Integer id);
	
	public String getType();
	
	public void setType(String type);
	
	public String getXml();
	
	public void setXml(String xml);
	
	public List<IOpenLayersMapObjectProperty> getProperties();
	
	public void setProperties(List<IOpenLayersMapObjectProperty> properties);
	
	public String getName();
	
	public void setName(String name);
	
	public void setEntities(List<IOpenLayersMapSpecificationEntity> entities);

	public List<IOpenLayersMapSpecificationEntity> getEntities();
}
