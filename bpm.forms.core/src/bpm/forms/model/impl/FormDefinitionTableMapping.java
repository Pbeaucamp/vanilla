package bpm.forms.model.impl;

public class FormDefinitionTableMapping {
	private long id;
	private long formDefinitionId;
	private long targetTableId;
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
	 * @return the targetTableId
	 */
	public long getTargetTableId() {
		return targetTableId;
	}
	/**
	 * @param targetTableId the targetTableId to set
	 */
	public void setTargetTableId(long targetTableId) {
		this.targetTableId = targetTableId;
	}
	
	
}
