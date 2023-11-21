package bpm.aklabox.workflow.core.model.resources;

public class Parameter extends Resource {

	private static final long serialVersionUID = 1L;

	private String description;
	private String value;

	public Parameter(String name, String description, int userId, String value) {
		this.name = name;
		this.userId = userId;
		this.value = value;
		this.description = description;
	}

	public Parameter() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
