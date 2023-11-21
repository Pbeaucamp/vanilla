package bpm.aklabox.workflow.core.model.resources;

import java.io.Serializable;

public class Resource implements Serializable, IResource {

	private static final long serialVersionUID = 1L;

	protected int id;
	protected int userId;
	protected String name;
	protected String address;
	protected String userName;
	protected String password;

	public Resource(String name, String address, String userName, String password) {
		super();
		this.name = name;
		this.address = address;
		this.userName = userName;
		this.password = password;
	}

	public Resource() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
