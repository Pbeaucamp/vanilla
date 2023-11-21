package org.fasd.molap;

import java.util.HashMap;



public class MOLAPMappingContext {
	
	private HashMap<String, String> molap = new HashMap<String, String>();
	
	public void addItem(String id, String molapId, String fileName){
		
		molap.put(molapId, id);

	}
	
	
	public String getOriginalId(String molapId){
		return molap.get(molapId);
	}

	public HashMap<String, String> getMap() {
		return molap;
	}

}
