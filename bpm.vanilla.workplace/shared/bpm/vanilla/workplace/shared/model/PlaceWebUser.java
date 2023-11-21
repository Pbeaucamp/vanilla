package bpm.vanilla.workplace.shared.model;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;


public class PlaceWebUser implements IsSerializable {

	private int id;
	private String name;
	private String password;
	private String mail;
	
	private Boolean isAdmin;
	private Boolean valid;
	
	private String hashCode;
	
	private Date creationDate;
	
	public PlaceWebUser() { }
	
	public PlaceWebUser(String name, String password, String mail, Boolean isAdmin, 
			String hashCode, boolean valid, Date creationDate){
		this.setName(name);
		this.setPassword(password);
		this.setMail(mail);
		this.setIsAdmin(isAdmin);
		this.setHashCode(hashCode);
		this.setValid(valid);
		this.setCreationDate(creationDate);
	}
	
	public PlaceWebUser(int id, String name, String password, String mail, Boolean isAdmin, Date creationDate){
		this.setId(id);
		this.setName(name);
		this.setPassword(password);
		this.setMail(mail);
		this.setIsAdmin(isAdmin);
		this.setCreationDate(creationDate);
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

	public String getName() {
		return name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getMail() {
		return mail;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}

	public String getHashCode() {
		return hashCode;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}
}
