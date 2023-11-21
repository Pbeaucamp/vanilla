package bpm.forms.model.impl;

import bpm.forms.core.runtime.IFormInstanceFieldState;

public class FormInstanceFieldState implements IFormInstanceFieldState{

	private long formFieldMappingId;
	private long formInstanceId;
	private String value;
	private long id;
	private int validated;
	
	
	
	
	/**
	 * @return the validated
	 */
	public int getValidated() {
		return validated;
	}

	/**
	 * @param validated the validated to set
	 */
	public void setValidated(int validated) {
		this.validated = validated;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public long getFormFieldMappingId() {
		return formFieldMappingId;
	}

	@Override
	public long getFormInstanceId() {
		return formInstanceId;
	}

	@Override
	public String getValue() {
		return value;
	}

	/**
	 * @param fieldMappingId the fieldMappingId to set
	 */
	public void setFormFieldMappingId(long fieldMappingId) {
		this.formFieldMappingId = fieldMappingId;
	}

	/**
	 * @param formInstanceId the formInstanceId to set
	 */
	public void setFormInstanceId(long formInstanceId) {
		this.formInstanceId = formInstanceId;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
