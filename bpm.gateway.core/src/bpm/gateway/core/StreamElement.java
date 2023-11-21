package bpm.gateway.core;


/**
 * class that represent a Field from a stream
 * 
 * @author LCA
 *
 */
public class StreamElement {
	public String name = "";
	public String className = "";
	public int type = 0;
	public String typeName;
	public String tableName = "";
	public String transfoName = "";
	public String originTransfo = "";
	public boolean isNullable = true;
	public boolean isPrimaryKey = true;
	public String defaultValue = null;
	public Integer precision = null;
	public Integer decimal = null;
	public Integer size = null;
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StreamElement)){
			return false;
		}
		StreamElement s = (StreamElement)obj;
		if (this == obj){
			return true;
		}
		if(className == null || typeName == null){
			return name.equals(s.name) && type == s.type && transfoName.equals(s.transfoName) && originTransfo.equals(s.originTransfo);
		}
		return name.equals(s.name)&& className.equals(s.className) && type == s.type && tableName.equals(s.tableName) && transfoName.equals(s.transfoName) && originTransfo.equals(s.originTransfo);
	}
	
	public StreamElement clone(String transfoName, String originTransfoName){
		StreamElement e = new StreamElement();
		 e.name = new String(name);
		 e.className = new String(className);
		 e.type = type;
		 e.tableName = new String(tableName);
		 e.transfoName = new String(transfoName);
		 e.originTransfo = new String(originTransfoName);
		 e.isNullable = isNullable;
		 e.isPrimaryKey = isPrimaryKey;
		 
		 if (defaultValue != null){
			 e.defaultValue = new String(defaultValue);
			 
		 }
		 
		 if (typeName!= null){
			 e.typeName= new String(typeName);
			 
		 }
		 
		 if (size != null){
			 e.size = new Integer(size); 
		 }
		 
		 if (decimal != null){
			 e.decimal = new Integer(decimal); 
		 }
		 if (precision != null){
			 e.precision = new Integer(precision); 
		 }
		 
		
		 return e;
	}
	
	public String getFullName(){
		return originTransfo + "::" + name;
	}
}

