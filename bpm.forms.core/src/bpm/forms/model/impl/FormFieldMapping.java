package bpm.forms.model.impl;

import bpm.forms.core.design.IFormFieldMapping;

public class FormFieldMapping implements IFormFieldMapping{

	private String databasePhysicalName;
	private String description;
	private String formFieldId;
	private String formFieldName;
	private long formDefinitionId;
	private Long targetTableId;
	private long id;
	private int multivalued;
	private String sqlDataType = "VARCHAR(255)";
	
	@Override
	public String getDatabasePhysicalName() {
		return databasePhysicalName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getFormFieldId() {
		return formFieldId;
	}

	@Override
	public String getFormFieldName() {
		return formFieldName;
	}

	@Override
	public long getFormDefinitionId() {
		return formDefinitionId;
	}

	@Override
	public Long getTargetTableId() {
		return targetTableId;
	}

	@Override
	public long getId() {
		return id;
	}

	public int getMultivalued(){
		return multivalued;
	}
	
	@Override
	public boolean isMultiValued() {
		return multivalued != 0;
	}

	/**
	 * @param databasePhysicalName the databasePhysicalName to set
	 */
	public void setDatabasePhysicalName(String databasePhysicalName) {
		this.databasePhysicalName = databasePhysicalName;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param formFieldId the formFieldId to set
	 */
	public void setFormFieldId(String formFieldId) {
		this.formFieldId = formFieldId;
	}

	/**
	 * @param formFieldName the formFieldName to set
	 */
	public void setFormFieldName(String formFieldName) {
		this.formFieldName = formFieldName;
	}

	/**
	 * @param formDefinitionId the formDefinitionId to set
	 */
	public void setFormDefinitionId(long formDefinitionId) {
		this.formDefinitionId = formDefinitionId;
	}

	/**
	 * @param targetTableId the targetTableId to set
	 */
	public void setTargetTableId(Long targetTableId) {
		this.targetTableId = targetTableId;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param multivalued the multivalued to set
	 */
	public void setMultivalued(int multivalued) {
		this.multivalued = multivalued;
	}

	@Override
	public String getSqlDataType() {
		return sqlDataType;
	}
	
	public void setSqlDataType(String sqlDataType){
		if (sqlDataType != null){
			this.sqlDataType = sqlDataType;
		}
	}

	@Override
	public void setIsMultivalued(boolean value) {
		if (value){
			setMultivalued(1);
		}
		else{
			setMultivalued(0);
		}
		
	}

	
}
