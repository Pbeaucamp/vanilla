package bpm.mdm.model.runtime.exception;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.Row;

public class AttributeDefaultValueException extends AbstractRowException{

	public AttributeDefaultValueException(Entity entity, Attribute a, Row row, OperationType type) {
		super("The attribute " + a.getName() + " from the entity " + entity.getName()
				+ " couldnt be inited from its default value " + a.getDefaultValue()
				, row, type);
	}

}
