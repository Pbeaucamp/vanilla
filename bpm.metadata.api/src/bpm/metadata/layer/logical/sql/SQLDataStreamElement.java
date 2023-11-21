package bpm.metadata.layer.logical.sql;

import java.util.HashMap;
import java.util.List;

import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.sql.SQLColumn;

public class SQLDataStreamElement extends IDataStreamElement {

	/**
	 * do not use
	 */
	public SQLDataStreamElement(){}
	
	public SQLDataStreamElement(SQLColumn origin) {
		super(origin);
		name = origin.getLabel();
	}
	
	

	@Override
	public boolean isCalculated() {
		return false;
	}



	@Override
	public IColumn getOrigin() {
		return origin;
	}



	@Override
	public List<String> getDistinctValues() throws Exception{
		if (origin == null){
			if (originName != null){
				throw new Exception("Physical Table " + originName + " not found in table ");
			}
			else{
				throw new Exception("Physical Table " + getOriginName() + " not set ");
			}
			
		}
		return origin.getValues();
	}

	@Override
	public String getJavaClassName() throws Exception {
		if (origin != null){
			return origin.getClassName();
		}
		throw new Exception("No physical column associated with the DataStreamElement");
	}

	@Override
	public List<String> getDistinctValues(HashMap<String, String> parentValues) throws Exception {
		if (origin == null){
			if (originName != null){
				throw new Exception("Physical Table " + originName + " not found in table ");
			}
			else{
				throw new Exception("Physical Table " + getOriginName() + " not set ");
			}
			
		}
		return origin.getValues(parentValues);
	}


}
