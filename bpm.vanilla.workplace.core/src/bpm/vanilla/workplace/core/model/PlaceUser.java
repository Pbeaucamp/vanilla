package bpm.vanilla.workplace.core.model;

import java.util.Date;

import bpm.vanilla.workplace.core.IUser;

public class PlaceUser implements IUser {

	private int id;
	private String name;
	private String password;
	private String mail;
	
	private boolean isAdmin;
	
	private Date creationDate;
	
	public PlaceUser() { }
	
	public PlaceUser(String name, String password, String mail, boolean isAdmin, Date creationDate) {
		this.name = name;
		this.password = password;
		this.mail = mail;
		this.isAdmin = isAdmin;
		this.creationDate = creationDate;
	}
	
	public PlaceUser(int id, String name, String password, String mail, boolean isAdmin, Date creationDate) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.mail = mail;
		this.isAdmin = isAdmin;
		this.creationDate = creationDate;
	}
	
	@Override
	public Boolean getIsAdmin() {
		return isAdmin;
	}

	@Override
	public String getMail() {
		return mail;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}
