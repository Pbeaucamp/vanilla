package bpm.document.management.core.model;

import java.io.Serializable;

public class OrganigramElementSecurity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Gestion des labels dans le LabelHelper
	 */
	public enum Fonction {
		DIRECTEUR, DIRECTEURSERVICE, CHEFSERVICE, COLLABORATEUR,
		COORDINATEURVOYAGE, PRESCRIPTEURMARCHE, ASSISTANTDIRECTION,
		
		AUTRE
	} 
	
	private int id;
	private User user;
	private String role = "Lecture seule";
	private Fonction fonction;
	
	
	private int elementId;
	private int userId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		if(user != null) {
			userId = user.getUserId();
		}
		this.user = user;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getElementId() {
		return elementId;
	}

	public void setElementId(int elementId) {
		this.elementId = elementId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Fonction getFonction() {
		return fonction;
	}

	public void setFonction(Fonction fonction) {
		this.fonction = fonction;
	}

}
