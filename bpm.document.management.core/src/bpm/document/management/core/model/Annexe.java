package bpm.document.management.core.model;

import java.io.Serializable;

public class Annexe implements Serializable{


	private static final long serialVersionUID = 1L;
 
	
	private int id;
	private int idDoc;
	private int idDocRef;
	
	
	public Annexe() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(int idDoc) {
		this.idDoc = idDoc;
	}

	public int getIdDocRef() {
		return idDocRef;
	}

	public void setIdDocRef(int idDocRef) {
		this.idDocRef = idDocRef;
	}
	
}
