package bpm.es.web.shared.beans;

public class Parameter {

	private String id;
	private String sheet;
	private String type;
	
	private String name;
	private int valid = 1;
	
	private Parameter parent;

	public Parameter() { }

	public Parameter(String sheet, String type, String name) {
		this.sheet = sheet;
		this.type = type;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSheet() {
		return sheet;
	}

	public void setSheet(String sheet) {
		this.sheet = sheet;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValid() {
		return valid;
	}

	public void setValid(int valid) {
		this.valid = valid;
	}

	public Parameter getParent() {
		return parent;
	}

	public void setParent(Parameter parent) {
		this.parent = parent;
	}
}
