package bpm.forms.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.design.IFormUi;
import bpm.forms.core.design.ITargetTable;


public class FormDefinition implements IFormDefinition{


	
	
	
	private String description = "";
	
	private long id;
	private long formId;
	
	private Date startDate;
	private Date stopDate;
	private int version;
	
	private int active;
	private int fullyDesigned;
	private Date creationDate;
	private int creatorId;
	
	private IFormUi formUI;
	
	private List<IFormFieldMapping> fieldMappings = new ArrayList<IFormFieldMapping>();
	private List<ITargetTable> targetTables = new ArrayList<ITargetTable>();
	
	
	
	
	
	/**
	 * @return the formUI
	 */
	public IFormUi getFormUI() {
		return formUI;
	}

	/**
	 * @param formUI the formUI to set
	 */
	public void setFormUI(IFormUi formUI) {
		if (getId() > 0){
			formUI.setFormDefinitionId(getId());
		}
		
		this.formUI = formUI;
		
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @return the creatorId
	 */
	public int getCreatorId() {
		return creatorId;
	}

	@Override
	public boolean isDesigned() {
		return fullyDesigned > 0;
	}

	

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public List<IFormFieldMapping> getIFormFieldMappings() {
		return new ArrayList<IFormFieldMapping>(fieldMappings);
	}

	@Override
	public List<ITargetTable> getITargetTables() {
		return new ArrayList<ITargetTable>(targetTables);
	}

	@Override
	public long getId() {
		return id;
	}

	

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getStopDate() {
		return stopDate;
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public boolean isActivated() {
		return active > 0;
	}

	/**
	 * @param fullyDesigned the fullyDesigned to set
	 */
	public void setFullyDesigned(int fullyDesigned) {
		this.fullyDesigned = fullyDesigned;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @param creatorId the creatorId to set
	 */
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @param stopDate the stopDate to set
	 */
	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	

	/**
	 * @param active the active to set
	 */
	public void setActive(int active) {
		this.active = active;
	}

	@Override
	public long getFormId() {
		return formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(long formId) {
		this.formId = formId;
	}

	public void addTargetTable(ITargetTable table) {
		targetTables.add(table);
		
	}


	
	/**
	 * @return the fullyDesigned
	 */
	public int getFullyDesigned() {
		return fullyDesigned;
	}

	/**
	 * @return the active
	 */
	public int getActive() {
		return active;
	}

	public void addFormFieldMapping(IFormFieldMapping field) {
		if (field  != null && !fieldMappings.contains(field)){
			if (field.getFormDefinitionId() <= 0){
				((FormFieldMapping)field).setFormDefinitionId(getId());
			}
			fieldMappings.add(field);
		}

	}

	public void removeFormFieldMapping(IFormFieldMapping field) {
		fieldMappings.remove(field);
		
	}

	public void removeTargetTable(ITargetTable table) {
		targetTables.remove(table);
		
	}

	
	

	@Override
	public void setIsDesigned(boolean value) {
		if (value){
			setFullyDesigned(1);
		}
		else{
			setFullyDesigned(0);
		}
		
	}

	@Override
	public void setActivated(boolean v) {
		if (v){
			setActive(1);
		}
		else{
			setActive(0);
		}
		
	}

	@Override
	public void setDesigned(boolean value) {
		if (value){
			setFullyDesigned(1);
		}
		else{
			setFullyDesigned(0);
		}
	}

	
}
