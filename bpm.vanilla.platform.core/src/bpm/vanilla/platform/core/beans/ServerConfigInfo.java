package bpm.vanilla.platform.core.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ServerConfigInfo {

	private Properties prop;
	
	public ServerConfigInfo(Properties prop){
		this.prop  = prop;
	}
	
	public Properties getProperties(){
		return prop;
	}
	
	public void setValue(String shortName, String value){
		for(Object o : prop.keySet()){
			String s = (String)o;
			if (s.endsWith("." + shortName)){
				prop.setProperty((String)o, value);
			}
		}
	}
	
	public List<String> getPropertiesShortNames(){
		List<String> l = new ArrayList<String>();
		
		for(Object o : prop.keySet()){
			int start = ((String)o).lastIndexOf(".") + 1;
			if (start >= 0){
				l.add(((String)o).substring(start));
			}
			else{
				l.add(((String)o));
			}
		}
		
		return l;
	}
	
	
	public String getValue(String shortName){
		String fullName = null;
		for(Object o : prop.keySet()){
			String tmp = (String)o;
			if (tmp.endsWith("." + shortName)){
				return prop.getProperty(tmp);
			}
		}
		return null;
	}
}
