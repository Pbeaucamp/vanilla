package bpm.vanilla.map.core.design.openlayers;

public interface IOpenLayersMapObjectProperty {
	
	public static final String PROP_NAME = "name";
	public static final String PROP_URL = "url";
	public static final String PROP_TYPE = "type";
	public static final String PROP_LAYERS = "layers";
	
	public Integer getId();
	
	public void setId(Integer id);
	
	public String getName();
	
	public void setName(String name);
	
	public String getValue();
	
	public void setValue(String value);
	
	public Integer getOpenLayersMapObjectId();
	
	public void setOpenLayersMapObjectId(Integer id);
	
}
