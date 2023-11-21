package bpm.aklabox.workflow.core.model.resources;

import java.io.Serializable;

public class FormTextTemplate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2412163510878688589L;

	private int id;
	private int idForm;
	private String textModel;
	
	public FormTextTemplate() {
		super();
	}
	
	public FormTextTemplate(int idForm, String textModel) {
		super();
		this.idForm = idForm;
		this.textModel = textModel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdForm() {
		return idForm;
	}

	public void setIdForm(int idForm) {
		this.idForm = idForm;
	}

	public String getTextModel() {
		return textModel;
	}

	public void setTextModel(String textModel) {
		this.textModel = textModel;
	}
	
	
}
