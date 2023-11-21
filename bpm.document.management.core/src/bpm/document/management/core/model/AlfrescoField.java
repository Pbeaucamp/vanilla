package bpm.document.management.core.model;

import java.io.Serializable;

public class AlfrescoField implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int typeId;
	private int fieldId;
	
	public AlfrescoField(int id, int typeId, int fieldId) {
		super();
		this.id = id;
		this.typeId = typeId;
		this.fieldId = fieldId;
	}
	public AlfrescoField(int typeId, int fieldId) {
		super();
		this.typeId = typeId;
		this.fieldId = fieldId;
	}
	public AlfrescoField() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public int getFieldId() {
		return fieldId;
	}
	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	
}
