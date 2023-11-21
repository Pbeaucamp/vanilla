package bpm.document.management.core.model;

import java.io.Serializable;

public class OrbeonFormInstance implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int formId;
	private int docId;
	private String orbeonInstanceId;
	
	private Form form;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public String getOrbeonInstanceId() {
		return orbeonInstanceId;
	}

	public void setOrbeonInstanceId(String orbeonInstanceId) {
		this.orbeonInstanceId = orbeonInstanceId;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

}
