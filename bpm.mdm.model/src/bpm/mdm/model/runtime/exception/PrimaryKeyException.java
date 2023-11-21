package bpm.mdm.model.runtime.exception;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.exception.AbstractRowException.OperationType;

/**
 * This Exception should be thrown when trying to create a row with an
 * existing primaryKey value when flushing an IEntityStore
 * @author ludo
 *
 */
public class PrimaryKeyException extends AbstractRowException{
	private String primaryKey;
	
	public PrimaryKeyException(Entity entity, Row row, OperationType type){
		super("The entity " + entity.getName() + " has a row with the primary key ", row, type);
		
		StringBuilder s = new StringBuilder();
		for(Attribute a : entity.getAttributesId()){
			if (s.length() > 0){
				s.append(";");
			}
			s.append(a.getName() + "=" + row.getValue(a));
		}
		primaryKey = s.toString();
	}

	public String getPrimaryKey(){
		return primaryKey;
	}
	

}
