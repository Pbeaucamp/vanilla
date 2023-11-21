package bpm.vanilla.workplace.core.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

public class VanillaPackage {

	private String name, description;
	private Date date = Calendar.getInstance().getTime();

	private boolean includeHistorics;
	private boolean includeGroups, includeRoles, includeGrants;
	
	private List<User> users = new ArrayList<User>();

	private List<PlaceImportDirectory> directories = new ArrayList<PlaceImportDirectory>();
	private List<PlaceImportItem> items = new ArrayList<PlaceImportItem>();

	private List<Group> groups = new ArrayList<Group>();
	
	private HashMap<Group, List<Integer>> groupsRole = new HashMap<Group, List<Integer>>();

	public VanillaPackage() { }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void addUsers(User user) {
		if(users == null){
			users = new ArrayList<User>();
		}
		users.add(user);
	}

	public void setDirectories(List<PlaceImportDirectory> directories) {
		this.directories = directories;
	}

	public List<PlaceImportDirectory> getDirectories() {
		return directories;
	}

	public void addDirectory(PlaceImportDirectory dir) {
		if(directories == null){
			directories = new ArrayList<PlaceImportDirectory>();
		}
		directories.add(dir);
	}

	public void setItems(List<PlaceImportItem> items) {
		this.items = items;
	}

	public List<PlaceImportItem> getItems() {
		return items;
	}
	
	public void addItem(PlaceImportItem item){
		if(items == null){
			items = new ArrayList<PlaceImportItem>();
		}
		items.add(item);
	}

	public List<User> getUsers() {
		return users;
	}

	public boolean isIncludeGroups() {
		return includeGroups;
	}

	public void setIncludeGroups(boolean includeGroups) {
		this.includeGroups = includeGroups;
	}

	public boolean isIncludeRoles() {
		return includeRoles;
	}

	public void setIncludeRoles(boolean includeRoles) {
		this.includeRoles = includeRoles;
	}

	public boolean isIncludeGrants() {
		return includeGrants;
	}

	public void setIncludeGrants(boolean includeGrants) {
		this.includeGrants = includeGrants;
	}

	public void setIncludeHistorics(boolean includeHistorics) {
		this.includeHistorics = includeHistorics;
	}

	public boolean isIncludeHistorics() {
		return includeHistorics;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
		groupsRole.clear();
		for(Group g : groups) {
			groupsRole.put(g, new ArrayList<Integer>());
		}
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroupsRole(HashMap<Group, List<Integer>> groupsRole) {
		this.groupsRole = groupsRole;
	}

	public HashMap<Group, List<Integer>> getGroupsRole() {
		return groupsRole;
	}
	
	public void addGroupRole(Group g, int role) {
		groupsRole.get(g).add(role);
	}

	public int size() {
		return 1;
	}
}
