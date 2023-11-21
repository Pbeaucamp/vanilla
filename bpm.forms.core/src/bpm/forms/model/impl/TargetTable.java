package bpm.forms.model.impl;

import bpm.forms.core.design.ITargetTable;

public class TargetTable implements ITargetTable{
	private String databasePhysicalName;
	private String description;
	private long id;
	private String name;
	
	@Override
	public String getDatabasePhysicalName() {
		return databasePhysicalName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
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
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
