package bpm.aklabox.workflow.core.model.resources;

public class Variable extends Resource {

	private static final long serialVersionUID = 1L;

	private String description;
	private String type;
	private String value;

	public Variable(String name, String description, int userId, String type, String value) {
		this.name = "{$" + name + "}";
		this.userId = userId;
		this.type = type;
		this.value = value;
		this.description = description;
	}

	public Variable() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
