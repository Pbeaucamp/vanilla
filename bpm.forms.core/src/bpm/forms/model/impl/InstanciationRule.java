package bpm.forms.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.forms.core.design.IInstanciationRule;

public class InstanciationRule implements IInstanciationRule{

	private long id;
	private long formId;
	
	
	
	private List<Integer> groupsId = new ArrayList<Integer>();
	
	
	
	
	private Mode mode = Mode.MANUAL;

	private ScheduledType scheduledType;
	
	private boolean isUniqueIp;
	
	
	
	

	public Mode getMode(){
		return mode;
	}
	
	public int getModeInt(){
		return mode.ordinal();
	}
	
	public void setModeInt(int modeInt){
		try{
			this.mode = Mode.values()[modeInt];
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setMode(Mode mode){
		this.mode = mode;
	}

	/**
	 * @return the scheduledType
	 */
	public ScheduledType getScheduledType() {
		return scheduledType;
	}

	/**
	 * @param scheduledType the scheduledType to set
	 */
	public void setScheduledType(ScheduledType scheduledType) {
		this.scheduledType = scheduledType;
	}

	/**
	 * @return the isUniqueIp
	 */
	public boolean isUniqueIp() {
		return isUniqueIp;
	}

	/**
	 * @param isUniqueIp the isUniqueIp to set
	 */
	public void setUniqueIp(boolean isUniqueIp) {
		this.isUniqueIp = isUniqueIp;
	}

	@Override
	public long getId() {
		return id;
	}

	/**
	 * @return the formDefinitionId
	 */
	public long getFormId() {
		return formId;
	}

	/**
	 * @param formDefinitionId the formDefinitionId to set
	 */
	public void setFormId(long formId) {
		this.formId = formId;
	}

	public void setId(long id){
		this.id = id;
	}
	
	

	@Override
	public List<Integer> getGroupId() {
		return new ArrayList<Integer>(groupsId);
	}
	
	public void addGroupId(Integer groupId){
		if (groupsId != null){
			for(Integer i : groupsId){
				if (i.intValue() == groupId.intValue()){
					return;
				}
				
			}
			groupsId.add(groupId);
		}
	}
	
	public void removeGroupId(Integer groupId){
		if (groupId == null){
			return;
		}
		for(Integer i : groupsId){
			if (i.intValue() == groupId.intValue()){
				groupsId.remove(i);
				return;
			}
		}
	}

}
