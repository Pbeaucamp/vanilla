package bpm.mdm.model.runtime.exception;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.exception.AbstractRowException.OperationType;

public class NullAttributeException extends AbstractRowException{
	public NullAttributeException(Attribute attribute, Row row, OperationType type){
		super("The entity " + ((Entity)attribute.eContainer()).getName() + " cannot have null values for its attribute " + attribute.getName(), row, type);
	}
}
