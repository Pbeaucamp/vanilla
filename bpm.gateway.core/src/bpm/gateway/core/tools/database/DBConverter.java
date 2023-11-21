package bpm.gateway.core.tools.database;

import java.util.HashMap;

public class DBConverter {
	private String jdbcDriverName;
	
	private HashMap<String, TypeInfo> knownTypes = new HashMap<String, TypeInfo>();
	
	
	public void setJdbcDriverName(String jdbcDriverName){
		this.jdbcDriverName = jdbcDriverName;
	}
	
	public String getJdbcDriverName(){
		return jdbcDriverName;
	}
	
	public void addType(TypeInfo info){
		knownTypes.put(info.getName(), info);
	}
	
	public String getTypeName(String name){
		for(TypeInfo t : knownTypes.values()){
			if (t.getName().toLowerCase().equals(name.toLowerCase())){
				return t.getMatching();
			}
		}
		return "%" + name + " UNKNOWN TYPE%";
	}

	public boolean hasPrecision(String typeName) {
		for(TypeInfo t : knownTypes.values()){
			if (t.getName().toLowerCase().equals(typeName.toLowerCase())){
				return t.isHasPrecision();
			}
		}
		return false;
	}
	
}	
