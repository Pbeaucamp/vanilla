package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class InformationsNews implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int id;
	private Date activationBegin;
	private Date activationEnd;
	private String texte = "";
	
	public InformationsNews() {
	}

	public InformationsNews(String texte) {
		super();
		this.setTexte(texte);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTexte() {
		return texte;
	}

	public void setTexte(String texte) {
		this.texte = texte;
	}

	public Date getActivationBegin() {
		return activationBegin;
	}

	public void setActivationBegin(Date activationBegin) {
		this.activationBegin = activationBegin;
	}

	public Date getActivationEnd() {
		return activationEnd;
	}

	public void setActivationEnd(Date activationEnd) {
		this.activationEnd = activationEnd;
	}
	
}
