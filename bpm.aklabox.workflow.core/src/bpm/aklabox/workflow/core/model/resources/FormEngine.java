package bpm.aklabox.workflow.core.model.resources;

public class FormEngine extends Resource {

	private static final long serialVersionUID = 1L;

	private String description;
	private String formType;
	private String applicationName;
	private String formName;
	
	public FormEngine(String name, String formType){
		this.name = name;
		this.formType = formType;
	}
	
	public FormEngine(String name, int userId,  String description, String address, String formType, String formName, String applicationName) {

		this.name = name;
		this.userId = userId;
		this.description = description;
		this.address = address;
		this.formType = formType;
		this.formName = formName;
		this.applicationName = applicationName;

	}

	public FormEngine() {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

}
