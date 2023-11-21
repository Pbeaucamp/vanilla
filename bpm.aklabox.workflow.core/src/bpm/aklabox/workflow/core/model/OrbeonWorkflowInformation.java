package bpm.aklabox.workflow.core.model;

import java.io.Serializable;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Form;

public class OrbeonWorkflowInformation implements Serializable {

	private Workflow workflow;
	private Instance instance;
	private Documents document;
	private Form form;

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public Documents getDocument() {
		return document;
	}

	public void setDocument(Documents document) {
		this.document = document;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

}
