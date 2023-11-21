package bpm.forms.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IFormInstanceFieldState;

public class FormInstance implements IFormInstance{

	private Date creationDate;
	private int groupId;
	private long formDefinitionId;
	private long id;
	
	
	private int formSubmited = 0;
	private int formValidated = 0;
	
	
	private Date lastSubmitionDate;
	private Date lastValidationDate;
	private String submiterIp;
	private String validatorIp;
	
	
	private Integer submiterUserId;
	private Integer validatorUserId;

	private Date expirationDate;
	
	public FormInstance(){}
	

	/**
	 * @return the expirationDate
	 */
	public Date getExpirationDate() {
		return expirationDate;
	}

	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * @return the submiterUserId
	 */
	public Integer getSubmiterUserId() {
		return submiterUserId;
	}

	/**
	 * @param submiterUserId the submiterUserId to set
	 */
	public void setSubmiterUserId(Integer submiterUserId) {
		this.submiterUserId = submiterUserId;
	}

	/**
	 * @return the vaidatorUserId
	 */
	public Integer getValidatorUserId() {
		return validatorUserId;
	}

	/**
	 * @param vaidatorUserId the validatorUserId to set
	 */
	public void setValidatorUserId(Integer validatorUserId) {
		this.validatorUserId = validatorUserId;
	}

	/**
	 * @return the lastSubmitionDate
	 */
	public Date getLastSubmitionDate() {
		return lastSubmitionDate;
	}

	/**
	 * @param lastSubmitionDate the lastSubmitionDate to set
	 */
	public void setLastSubmitionDate(Date lastSubmitionDate) {
		this.lastSubmitionDate = lastSubmitionDate;
	}

	/**
	 * @return the lastValidationDate
	 */
	public Date getLastValidationDate() {
		return lastValidationDate;
	}

	/**
	 * @param lastValidationDate the lastValidationDate to set
	 */
	public void setLastValidationDate(Date lastValidationDate) {
		this.lastValidationDate = lastValidationDate;
	}

	/**
	 * @return the submiterIp
	 */
	public String getSubmiterIp() {
		return submiterIp;
	}

	/**
	 * @param submiterIp the submiterIp to set
	 */
	public void setSubmiterIp(String submiterIp) {
		this.submiterIp = submiterIp;
	}

	/**
	 * @return the validatorIp
	 */
	public String getValidatorIp() {
		return validatorIp;
	}

	/**
	 * @param validatorIp the validatorIp to set
	 */
	public void setValidatorIp(String validatorIp) {
		this.validatorIp = validatorIp;
	}

	/**
	 * @return the formSubmited
	 */
	public int getFormSubmited() {
		return formSubmited;
	}

	/**
	 * @param formSubmited the formSubmited to set
	 */
	public void setFormSubmited(Integer formSubmited) {
		if (formSubmited != null){
			this.formSubmited = formSubmited;
		}
		
	}

	/**
	 * @return the formValidated
	 */
	public int getFormValidated() {
		return formValidated;
	}

	/**
	 * @param formValidated the formValidated to set
	 */
	public void setFormValidated(Integer formValidated) {
		if (formValidated != null){
			this.formValidated = formValidated;
		}
	}

	
	
	
	private List<IFormInstanceFieldState> fieldsStates = new ArrayList<IFormInstanceFieldState>();
	
	
	
	
	
	

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
	 * @param groupId the groupId to set
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	
	}

	/**
	 * @param formDefinitionId the formDefinitionId to set
	 */
	public void setFormDefinitionId(long formDefinitionId) {
		this.formDefinitionId = formDefinitionId;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public List<IFormInstanceFieldState> getFieldsStates() {
		return new ArrayList<IFormInstanceFieldState>(fieldsStates);
	}

	@Override
	public int getGroupId() {
		return groupId;
	}

	@Override
	public long getFormDefinitionId() {
		return formDefinitionId;
	}

	/**
	 * @param createDate the createDate to set
	 */
	public void setCreationDate(Date createDate) {
		this.creationDate = createDate;
	}

	public void addFieldState(IFormInstanceFieldState fieldState) {
		
		if (!fieldsStates.contains(fieldState) && fieldState != null){
			fieldsStates.add(fieldState);
		}
		
		
	}

	
	@Override
	public boolean isSubmited() {
		return formSubmited > 0;
	}

	@Override
	public boolean isValidated() {
		return formValidated > 0;
	}

	@Override
	public void setIsSubmited(boolean v) {
		if (v){
			setFormSubmited(1);
		}
		else{
			setFormSubmited(0);
		}
		
	}

	@Override
	public void setIsValidated(boolean v) {
		if (v){
			setFormValidated(1);
		}
		else{
			setFormValidated(0);
		}
		
	}

	@Override
	public boolean hasBeenValidated() {
		return getLastValidationDate() != null;
	}

	@Override
	public boolean hasBeenSubmited() {
		return getLastSubmitionDate() != null;
	}

}
