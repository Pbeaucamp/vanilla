package bpm.fd.api.core.model.resources;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

/**
 * 
 * @author ludo
 *
 */
public class Palette implements Serializable {

	private static final long serialVersionUID = 1L;
	private HashMap<String, String> colors = new HashMap<String, String>();
	private String name;
//	private String description;
	
	
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * add a color for the given key. If the color is used by another Key, it will fail except if force is set to true
	 * @param key
	 * @param color
	 * @param force
	 * @return : true if the color as been set
	 */
	public boolean addColor(String key, String color, boolean force){
		if (!force){
			for(String s : colors.values()){
				if (s.equals(color)){
					return false;
				}
			}
		}
		colors.put(key, color);
		return true;
		
		
	}
	public String getColor(String key){
		return colors.get(key);
	}
	
	public Collection<String> getKeys(){
		return colors.keySet();
	}
	public void remove(String o) {
		colors.remove(o);
		
	}
	
	
	
	
}
