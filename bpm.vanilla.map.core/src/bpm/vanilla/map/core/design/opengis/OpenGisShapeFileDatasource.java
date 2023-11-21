package bpm.vanilla.map.core.design.opengis;

public class OpenGisShapeFileDatasource implements IOpenGisDatasource {
	public static final String FORMAT_SHAPE = "shp";

	private int id;
	private String filePath;
	private String format;
	private String type = IOpenGisDatasource.SHAPE_FILE;
	
	public OpenGisShapeFileDatasource(){}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	
}
