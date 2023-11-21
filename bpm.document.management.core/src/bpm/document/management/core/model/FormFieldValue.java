package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

import bpm.document.management.core.model.FormField.FormFieldType;

public class FormFieldValue implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int formFieldId;
	private String value;
	private int userId;
	private FormFieldType type;
	private int formId;
	private int docId;
	private Date creationDate = new Date();
	private int metadataLinkId;
	
	private FormField field;

	public FormFieldValue() {
	}

	public FormFieldValue(int formFieldId, String value, FormFieldType type, int docId) {
		super();
		this.formFieldId = formFieldId;
		this.value = value;
		this.type = type;
		this.docId = docId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFormFieldId() {
		return formFieldId;
	}

	public void setFormFieldId(int formFieldId) {
		this.formFieldId = formFieldId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public FormFieldType getType() {
		return type;
	}

	public void setType(FormFieldType type) {
		this.type = type;
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

	
	public int getMetadataLinkId() {
		return metadataLinkId;
	}
	
	public void setMetadataLinkId(int metadataLinkId) {
		this.metadataLinkId = metadataLinkId;
	}

	public FormField getField() {
		return field;
	}

	public void setField(FormField field) {
		this.field = field;
	}
}
