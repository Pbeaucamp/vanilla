package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TypeTask implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	
	private List<User> users = new ArrayList<User>();
	private List<Tasks> tasks = new ArrayList<Tasks>();

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

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Tasks> getTasks() {
		return tasks;
	}

	public void setTasks(List<Tasks> tasks) {
		this.tasks = tasks;
	}

}
