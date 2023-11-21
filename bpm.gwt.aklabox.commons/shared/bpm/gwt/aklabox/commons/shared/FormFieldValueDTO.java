package bpm.gwt.aklabox.commons.shared;

import java.io.Serializable;
import java.util.Date;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.FormField;
import bpm.document.management.core.model.FormField.FormFieldType;

public class FormFieldValueDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private FormFieldType fieldType;
	private String fieldValue;
	private String fieldName;
	private Date creationDate;
	private String user;
	private String documentName;
	private FormField field;
	private Documents document;
	
	public FormFieldValueDTO() {
		// TODO Auto-generated constructor stub
	}

	
	
	public FormFieldValueDTO(int id, FormFieldType fieldType, String fieldValue, FormField field, Date creationDate, String user, String documentName) {
		super();
		this.fieldType = fieldType;
		this.fieldValue = fieldValue;
		this.fieldName = field.getLabel();
		this.creationDate = creationDate;
		this.user = user;
		this.id = id;
		this.documentName = documentName;
		this.setField(field);
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FormFieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FormFieldType fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public FormField getField() {
		return field;
	}

	public void setField(FormField field) {
		this.field = field;
	}
	
	public Documents getDocument() {
		return document;
	}

	public void setDocument(Documents document) {
		this.document = document;
	}
	
}
