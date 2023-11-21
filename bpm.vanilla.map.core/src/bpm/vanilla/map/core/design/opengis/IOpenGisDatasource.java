package bpm.vanilla.map.core.design.opengis;

public interface IOpenGisDatasource {

	public static final String SHAPE_FILE = "ShapeFile";
	public static final String POSTGIS = "Postgis";
	
	public int getId();

	public void setId(int id);
	
	public void setType(String type);

	public String getType();
}
