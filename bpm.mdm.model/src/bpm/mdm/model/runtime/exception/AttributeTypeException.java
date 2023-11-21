package bpm.mdm.model.runtime.exception;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.exception.AbstractRowException.OperationType;

public class AttributeTypeException extends AbstractRowException{

	public AttributeTypeException(Entity entity, Attribute a, Class<?> cl,
			Object val, Row row, OperationType type) {
		super("The attribute " + a.getName() + " from the entity " + entity.getName()
				+ " should be an instanceof " + cl.getSimpleName() + " and its value is " + val + " which is not compatible"
				, row, type);
	}

}
