package bpm.forms.model.impl;

import bpm.forms.core.design.IFormUIProperty;

public class FormUIProperty implements IFormUIProperty{

	private long id;
	private String propertyName;
	private String propertyValue;
	private long formDefinitionId;
	private String formUiName;

	
	
	/**
	 * @return the formDefinitionId
	 */
	public long getFormDefinitionId() {
		return formDefinitionId;
	}
	/**
	 * @param formDefinitionId the formDefinitionId to set
	 */
	public void setFormDefinitionId(long formDefinitionId) {
		this.formDefinitionId = formDefinitionId;
	}
	/**
	 * @return the formUiName
	 */
	public String getFormUiName() {
		return formUiName;
	}
	/**
	 * @param formUiName the formUiName to set
	 */
	public void setFormUiName(String formUiName) {
		this.formUiName = formUiName;
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
	
	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}
	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	/**
	 * @return the propertyValue
	 */
	public String getPropertyValue() {
		return propertyValue;
	}
	/**
	 * @param propertyValue the propertyValue to set
	 */
	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
	
	
}
