package bpm.faweb.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TreeObjectDTO implements IsSerializable{

	private String name;
	private int id;
	private String type;

	private TreeObjectDTO parent;

	public TreeObjectDTO() {			
		setName("");
	}

	public TreeObjectDTO(String name) {			
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setParent(TreeObjectDTO parent) {
		this.parent = parent;
	}

	public TreeObjectDTO getParent() {
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


}
