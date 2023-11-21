package bpm.gateway.core.tools;

public class APIBanField {

	private String id;
	private String description;
	private int type;
	private String typeName;
	private String className;

	public APIBanField(String id, String description, int type, String typeName, String className) {
		this.id = id;
		this.description = description;
		this.type = type;
		this.typeName = typeName;
		this.className = className;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public int getType() {
		return type;
	}

	public String getTypeName() {
		return typeName;
	}
	
	public String getClassName() {
		return className;
	}
}