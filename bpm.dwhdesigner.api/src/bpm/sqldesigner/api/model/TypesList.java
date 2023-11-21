package bpm.sqldesigner.api.model;

import java.util.HashMap;

public class TypesList {
	
	protected HashMap<String,Type> types = new HashMap<String,Type>();
	
	public void addType(Type t){
		types.put(t.getName(), t);
	}
	
	public Type getType(String tName){
		return types.get(tName);
	}

	public HashMap<String, Type> getTypes() {
		return types;
	}
}
