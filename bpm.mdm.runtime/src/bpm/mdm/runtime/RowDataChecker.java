package bpm.mdm.runtime;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Rule;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.exception.AbstractRowException;
import bpm.mdm.model.runtime.exception.AttributeTypeException;
import bpm.mdm.model.runtime.exception.NullAttributeException;
import bpm.mdm.model.runtime.exception.RowsExceptionHolder;
import bpm.mdm.model.runtime.exception.RuleException;
import bpm.mdm.model.runtime.exception.AbstractRowException.OperationType;
import bpm.mdm.model.storage.IEntityStorage;

public class RowDataChecker {
	private Entity entity;
	private IEntityStorage storage;
	public RowDataChecker(Entity entity, IEntityStorage storage){
		this.entity = entity;
		this.storage = storage;
	}
	
	
	public void checkAllEntity() throws Exception{
		RuntimeEntityReader reader = new RuntimeEntityReader(storage, entity);
		reader.open();
		List<AbstractRowException> errors = new ArrayList<AbstractRowException>();
		
		while(reader.hasNext()){
			Row r = reader.next();
			try{
				checkRow(r, null);
			}catch(AbstractRowException e){
				errors.add(e);
			}
		}
		
		reader.close();
		if (!errors.isEmpty()){
			throw new RowsExceptionHolder(errors);
		}
		
	}
	
	public void checkRow(Row row, OperationType type) throws Exception{
		for(Attribute a : entity.getAttributes()){
			checkAttributeValue(a, row, type);
		}
	}
	
	/**
	 * extract the value from the row for the given attribute
	 * if every conditions are satisfied, nothing happens,
	 * otherwise, an exception will be thrown
	 * 
	 * the attribute properties are checked, then the attribte's value DataType
	 * is verified, and finally, all the actives rules are validated
	 * 
	 * this method should be use for each attribute when performing a writing
	 * operation on an entity row(create and upate operations)
	 * 
	 * @param attribute
	 * @param row
	 * @throws Exception
	 */
	public void checkAttributeValue(Attribute attribute, Row row, OperationType type) throws Exception{
		Object value = row.getValue(attribute);
		
		//check attribute type
		if (value != null){
			Class<?> cl = attribute.getDataType().getJavaClass();
			try{
				cl.cast(value);
			}catch(ClassCastException ex){
				Constructor<?> constructor = cl.getConstructor(String.class);
				if (constructor != null){
					try{
						constructor.newInstance(value.toString());
					}catch(Exception ex2){
						throw new AttributeTypeException(entity, attribute, cl, value, row, type);
					}
				}
				
			}
			
		}

		//check attribute options
		if (!attribute.isNullAllowed() && value == null){
			//try to apply default value
			if (attribute.getDefaultValue() == null){
				throw new NullAttributeException(attribute, row,type);
			}
			
		}
		
		//check rules
		for(Rule r : attribute.getRules()){
			if (r.isActive()){
				if (!r.evaluate(value)){
					throw new RuleException(r, row, type);
				}
			}
		}
	}
}
