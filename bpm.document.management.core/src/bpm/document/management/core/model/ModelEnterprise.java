package bpm.document.management.core.model;

import java.io.Serializable;

public class ModelEnterprise implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id = 0;
	private int idEnterprise;
	private int idDocuments;
	private int linkType = 0;
	private Documents document;

	public int getIdEnterprise() {
		return idEnterprise;
	}

	public void setIdEnterprise(int idEnterprise) {
		this.idEnterprise = idEnterprise;
	}

	public int getIdDocuments() {
		return idDocuments;
	}

	public void setIdDocuments(int idDocuments) {
		this.idDocuments = idDocuments;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLinkType() {
		return linkType;
	}

	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}

	public Documents getDocument() {
		return document;
	}

	public void setDocument(Documents document) {
		this.document = document;
	}
	
	@Override
	public String toString() {
		return document != null ? document.getName() : "Inconnu";
	}
}
