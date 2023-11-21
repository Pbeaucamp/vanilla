package bpm.forms.model.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IInstanciationRule;

public class Form implements IForm{
	
	private InstanciationRule instanciationRule = new InstanciationRule();
	private long id;
	private String name;
	private Date creationDate = Calendar.getInstance().getTime();
	private int creatorId;
	private String description;
	
	private List<IFormDefinition> definitions = new ArrayList<IFormDefinition>();
	private List<Integer> validationGroups = new ArrayList<Integer>();
	
	private int lifeExpectancyHours;
	private int lifeExpectancyDays;
	private int lifeExpectancyMonths;
	private int lifeExpectancyYears;
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	@Override
	public int getCreatorId() {
		return creatorId;
	}
	@Override
	public String getCreatorName() {
		return name;
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
	public void addDefinition(IFormDefinition fd) {
		definitions.add(fd);
		
	}
	@Override
	public List<IFormDefinition> getDefinitionsVersions() {
		return new ArrayList<IFormDefinition>(definitions);
	}
	@Override
	public void removeDefinition(IFormDefinition fd) {
		definitions.remove(fd);
		
	}
	@Override
	public void setCreatorName() {
		
		
	}
	
	@Override
	public IInstanciationRule getInstanciationRule() {
		return instanciationRule;
	}
	@Override
	public List<Integer> getValidatorGroups() {
		return new ArrayList<Integer>(validationGroups);
	}
	public void addValidationGroup(Integer groupId){
		
		if (groupId == null){
			return;
		}
		
		if (groupId > 0){
			for(Integer i : validationGroups){
				if (i.intValue() == groupId){
					return;
				}
			}
		}
		
		validationGroups.add(groupId);
	}
	
	public void removeValidationGroup(Integer groupId){
		if (groupId == null){
			return;
		}
		for(Integer i : validationGroups){
			if (i.intValue() == groupId){
				validationGroups.remove(i);
				return;
			}
		}
	}
	@Override
	public int getLifeExpectancyDays() {
		return lifeExpectancyDays;
		
	}
	@Override
	public int getLifeExpectancyHours() {
		return lifeExpectancyHours;
	}
	@Override
	public int getLifeExpectancyMonths() {
		return lifeExpectancyMonths;
	}
	@Override
	public int getLifeExpectancyYears() {
		return lifeExpectancyYears;
	}
	@Override
	public void setLifeExpectancyDays(int lifeExpectancyDays) {
		this.lifeExpectancyDays = lifeExpectancyDays;
		
	}
	@Override
	public void setLifeExpectancyHours(int lifeExpectancyHours) {
		this.lifeExpectancyHours = lifeExpectancyHours;
		
	}
	@Override
	public void setLifeExpectancyMonths(int lifeExpectancyMonths) {
		this.lifeExpectancyMonths = lifeExpectancyMonths;
		
	}
	@Override
	public void setLifeExpectancyYears(int lifeExpectancyYears) {
		this.lifeExpectancyYears = lifeExpectancyYears;
		
	}
	
	
	
}
