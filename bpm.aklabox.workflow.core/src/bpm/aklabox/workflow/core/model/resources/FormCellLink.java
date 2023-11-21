package bpm.aklabox.workflow.core.model.resources;

import java.io.Serializable;

import bpm.document.management.core.model.FormField;

public class FormCellLink implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int idOCRForm;
	private int idOCRFormCell;
	private int idAklaFormField;
	private int idAklaForm;
	
	//to get more info
	private FormField field;

	public FormCellLink() {}
	
	public FormCellLink(int idOCRForm, int idOCRFormCell, int idAklaFormField, int idAklaForm) {
		super();
		this.idOCRForm = idOCRForm;
		this.idOCRFormCell = idOCRFormCell;
		this.idAklaFormField = idAklaFormField;
		this.idAklaForm = idAklaForm;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdOCRForm() {
		return idOCRForm;
	}

	public void setIdOCRForm(int idOCRForm) {
		this.idOCRForm = idOCRForm;
	}

	public int getIdOCRFormCell() {
		return idOCRFormCell;
	}

	public void setIdOCRFormCell(int idOCRFormCell) {
		this.idOCRFormCell = idOCRFormCell;
	}

	public int getIdAklaFormField() {
		return idAklaFormField;
	}

	public void setIdAklaFormField(int idAklaFormField) {
		this.idAklaFormField = idAklaFormField;
	}

	public int getIdAklaForm() {
		return idAklaForm;
	}

	public void setIdAklaForm(int idAklaForm) {
		this.idAklaForm = idAklaForm;
	}

	public FormField getField() {
		return field;
	}

	public void setField(FormField field) {
		this.field = field;
	}


}
