package bpm.vanilla.workplace.core.model;

import java.util.ArrayList;
import java.util.List;

public class ImportItem {
	
	private String name;
	private int id;
	private int directoryId;
	private int type;
	private int subtype;
	private String path = "root";
	private List<Integer> dependancies = new ArrayList<Integer>();
	private List<Replacement> replacement = new ArrayList<Replacement>();

	/**
	 * Field to store the external document's path
	 */
	private String filepath;

	public void addAutoReplacement(String old, String newValue) {
		Replacement r = new Replacement(this);
		r.setOriginalString(old);
		r.setReplacementString(newValue);
		replacement.add(r);
	}

	public void removeAutoReplacement(Replacement r) {
		replacement.remove(r);
	}

	public List<Replacement> getAutoReplacements() {

		return replacement;
	}

	public void addNeeded(int id) {
		for (Integer i : dependancies) {
			if (id == i.intValue()) {
				return;
			}
		}
		dependancies.add(id);
	}

	public void addNeeded(String id) {
		addNeeded(Integer.parseInt(id));
	}

	public List<Integer> getNeeded() {
		return dependancies;
	}

	public void setPath(String path) {
		if (path != null && !path.equalsIgnoreCase("null")) {
			this.path = path;
		}
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}

	public int getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(int directoryId) {
		this.directoryId = directoryId;
	}

	public void setDirectoryId(String directoryId) {
		this.directoryId = Integer.parseInt(directoryId);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setType(String type) {
		this.type = Integer.parseInt(type);
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}

	public int getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = Integer.parseInt(subtype);
	}

}
