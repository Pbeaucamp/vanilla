package bpm.vanilla.platform.core.beans.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class HbaseTable implements Serializable {
	
	private static final long serialVersionUID = 5839346913549389332L;
	
	private String name;
	private HashMap<String, List<String>> families = new LinkedHashMap<String, List<String>>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, List<String>> getFamilies() {
		return families;
	}

	public void setFamilies(HashMap<String, List<String>> families) {
		this.families = families;
	}

}
