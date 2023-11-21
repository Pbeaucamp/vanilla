package bpm.sqldesigner.api.model;

public class Type {

	protected int id;
	protected String name;
	protected String params = "";

	public Type() {
	}

	public Type(int id, String name) {
		this.id = id;
		this.name = name;
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Type) {
			Type type = (Type) obj;
			return (type.getId() == id) && (type.getName().equals(name));
		}
		return false;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

}
