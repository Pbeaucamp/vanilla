package bpm.vanilla.platform.core.beans.ged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;



/**
 * Object use to store the mapping between a Field and its value
 * @author vanilla
 *
 */
public class ComProperties implements IComProperties {
	private Properties simples = new Properties();
	private HashMap<String, List<String>> multiples = new HashMap<String, List<String>>();
	
	public void setProperty(Definition definition, String value) {
		if (definition.multiple()) {
			if (multiples.get(definition.getName()) == null) {
				multiples.put(definition.getName(), new ArrayList<String>());
			}
			multiples.get(definition.getName()).add(value);
		}
		else {
			simples.setProperty(definition.getName(), value);
		}
	}
	
	public void setSimpleProperty(String pName, String value){
		simples.setProperty(pName, value);
	}
	
	public void setValues(Definition definition, List<String> values) throws Exception {
		if (!definition.multiple()) {
			throw new Exception("Definition " + definition.getName() + " isnot a multiple definition"); 
		}
		multiples.put(definition.getName(), values);
	}
	
	public String getValueForField(String pname) {
		return simples.getProperty(pname);
	}

	public List<String> getValuesForField(String name) {
		return multiples.get(name);
	}
	
	public List<String> getKeys() {
		List<String> res = new ArrayList<String>();
		for (Object s : simples.keySet()) {
			res.add((String) s);
		}
		for (String s : multiples.keySet()) {
			res.add(s);
		}
		return res;
	}
	
	public boolean isMultivalued(String pname) {
		return multiples.keySet().contains(pname);
	}

}
