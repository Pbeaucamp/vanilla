package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class SocialMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1701736157901176279L;
	
	public enum ActionType{
		POST, WRITE, COMMENT, SHARE
	}
	
	private int id;
	private int idUser;
	private Date date;
	private String message;
	private int idParent;
	private ActionType type;
	
	private User user;

	public SocialMessage() {
		super();
	}

	public SocialMessage(int idUser, Date date, String message, int idParent, ActionType type) {
		super();
		this.idUser = idUser;
		this.date = date;
		this.message = message;
		this.idParent = idParent;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getIdParent() {
		return idParent;
	}

	public void setIdParent(int idParent) {
		this.idParent = idParent;
	}

	public ActionType getType() {
		return type;
	}

	public void setType(ActionType type) {
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	

}
