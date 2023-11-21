package bpm.sqldesigner.api.constants.types.dbms;

import java.util.HashMap;

import bpm.sqldesigner.api.model.Type;

public class DBMSTypes {

	protected HashMap<String, Type> equivStandardTypes = new HashMap<String, Type>();
	protected HashMap<String, Type> equivMySQLTypes = new HashMap<String, Type>();

	public DBMSTypes(){
		initEquivStandardTypes();
		initEquivMySQLTypes();		
	}
	
	public Type getStandardType(String type) {
		//->MySQL -> Standard
		return equivStandardTypes.get(type);
	}
	
	public Type getMySQLType(String type){
		//->Standard -> MySQL
		return equivStandardTypes.get(type);
	}
	
	public void initEquivStandardTypes(){}
	
	public void initEquivMySQLTypes(){}
}
