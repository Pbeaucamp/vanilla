package bpm.fwr.shared.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TreeObjectDTO implements IsSerializable{

	private String name;
	private int id;
	private String type;

	private TreeParentDTO parent;

	public TreeObjectDTO() {			
		setName("");
	}

	public TreeObjectDTO(String name) {			
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setParent(TreeParentDTO parent) {
		this.parent = parent;
	}

	public TreeParentDTO getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}

	public void setName (String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

//	public HashMap<String, String> getLocales() {
//		return locales;
//	}
//
//	public void setLocales(HashMap<String, String> locales) {
//		this.locales = locales;
//	}


}
