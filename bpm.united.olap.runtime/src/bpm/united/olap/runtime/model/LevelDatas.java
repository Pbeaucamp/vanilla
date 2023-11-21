package bpm.united.olap.runtime.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import bpm.united.olap.api.tools.AlphanumComparator;

public class LevelDatas implements Serializable{
	
	public enum LevelDataType{
		FOREIGN_KEY,PARENT_NAME,MEMBER_NAME,MEMBER_ORDER,MEMBER_LABEL,MEMBER_PROPERTIES,CHILD_NAME,CLOSURE_PARENTKEY
	}
	
	protected LinkedHashMap<LevelDataType, Object> datas = new LinkedHashMap<LevelDataType, Object>();
	
	public void addData(LevelDataType type, Object data) {
		if (type == LevelDataType.FOREIGN_KEY){
			if (datas.get(type) == null){
				datas.put(type, new ArrayList<String>());
			}
			
			if (data instanceof List){
				((List)datas.get(type)).addAll((List)data);
			}
			else{
				((List)datas.get(type)).add(data);
			}
			
		}
		else if (type == LevelDataType.CLOSURE_PARENTKEY){
			if (datas.get(type) == null){
				datas.put(type, new ArrayList<String>());
			}
			
			if (data instanceof List){
				((List)datas.get(type)).addAll((List)data);
			}
			else{
				((List)datas.get(type)).add(data);
			}
			
		}
		else{
			datas.put(type, data);
		}
		
	}
	
	public Object getData(LevelDataType type) {
		Object obj = datas.get(type);
		if(obj != null) {
//			if(obj instanceof String && type.equals(LevelDataType.MEMBER_NAME) || type.equals(LevelDataType.PARENT_NAME)) {
//				return ((String)obj).replace("'", "");
//			}
			if(obj instanceof String && !type.equals(LevelDataType.FOREIGN_KEY)) {
				return ((String)obj).replace("'", "");
			}
			return obj;
		}
		else if(type.equals(LevelDataType.MEMBER_NAME)){
			return "NULL";
		}
		else if(type.equals(LevelDataType.PARENT_NAME)){
			return "NULL";
		}		
		else if(type.equals(LevelDataType.CHILD_NAME)){
			return "NULL";
		}
		return obj;
	}
	
	@Override
	public String toString() {
		return datas.get(LevelDataType.PARENT_NAME) + " - " + datas.get(LevelDataType.MEMBER_NAME) + " - " + datas.get(LevelDataType.MEMBER_LABEL) + " - " + datas.get(LevelDataType.MEMBER_ORDER) + " - " + datas.get(LevelDataType.FOREIGN_KEY) + " - " + datas.get(LevelDataType.CHILD_NAME);
	}
}
