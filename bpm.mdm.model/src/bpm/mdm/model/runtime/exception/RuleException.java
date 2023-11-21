package bpm.mdm.model.runtime.exception;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Rule;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.exception.AbstractRowException.OperationType;

public class RuleException extends AbstractRowException{
	public RuleException(Rule rule, Row row, OperationType type){
		super("The rule " + rule.getName() + " for the entity " + ((Entity)rule.eContainer().eContainer()).getName() + "'s attribute " + ((Attribute)rule.eContainer()).getName() + " is not satisfied", row, type);
	}
}
