package bpm.mdm.model.runtime.exception;

import bpm.mdm.model.Entity;

public class UpdateEntityPrimaryKeyException extends Exception{
	private String entityUuid;
	public UpdateEntityPrimaryKeyException(Entity entity){
		super("You are trying to change the entity " + entity.getName() + " primary Key althought it has datas.");
		this.entityUuid = entity.getUuid();
	}
	
	public String getEntityUuid(){
		return entityUuid;
	}
}
