package bpm.gateway.core.server.database.jdbc;

import java.lang.reflect.Field;
import java.util.HashMap;

import bpm.gateway.core.StreamDescriptor;

public class JDBCToolBox {

	private static HashMap<Integer, String> typeNames;
	
	/*
	 * perform an init of the typeNames
	 */
	static{
		typeNames = new HashMap<Integer, String>();
		
		
		Field[] fields = java.sql.Types.class.getFields();
		for(Field f : fields){
			try {
				typeNames.put((Integer)f.get(null), f.getName());
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	
	public static String getSqlTypeName(int sqlTypeCode){
		return typeNames.get(sqlTypeCode);
	}
	
	
	
	public static String getTableCreationInstruction(String tableName, StreamDescriptor descriptor){
		StringBuffer buf = new StringBuffer();
		
		//TODO : perform some check on the tableName field(only digit, letters, and _ are valid)
		
		buf.append("CREATE TABLE " + tableName + " (\n");
		
		
		for(int i = 0; i < descriptor.getColumnCount(); i++){
			if ( i != 0){
				buf.append(",\n");
			}
			
			if (descriptor.getTypeName(i) == null){
				String s =  descriptor.getStreamElements().get(i).className;
				s= s.substring(s.lastIndexOf(".") + 1);
				if (s.equals("String")){
					buf.append(descriptor.getColumnName(i) + " " + "VARCHAR(55)");
				}
				else{
					buf.append(descriptor.getColumnName(i) + " " + s);
				}
			}else{
				buf.append(descriptor.getColumnName(i) + " " + descriptor.getTypeName(i));
			}
			
			
			if (descriptor.getTypeName(i) !=null && descriptor.getTypeName(i).equalsIgnoreCase("VARCHAR")){
				buf.append("(" + descriptor.getSize(i) + ")");
			}
			
		}
		
		buf.append(")");
		
		return buf.toString();
	}
	
}
