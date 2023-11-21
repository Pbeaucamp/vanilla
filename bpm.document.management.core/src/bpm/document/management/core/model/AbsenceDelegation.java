package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AbsenceDelegation implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private int idUserSuccessor;
	private int idDelegation;
	private int idAbsence;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AbsenceDelegation(){
		
	}

	public int getIdDelegation() {
		return idDelegation;
	}

	public void setIdDelegation(int idDelegation) {
		this.idDelegation = idDelegation;
	}

	public int getIdUserSuccessor() {
		return idUserSuccessor;
	}

	public void setIdUserSuccessor(int idUserSuccessor) {
		this.idUserSuccessor = idUserSuccessor;
	}

	public int getIdAbsence() {
		return idAbsence;
	}

	public void setIdAbsence(int idAbsence) {
		this.idAbsence = idAbsence;
	}
}
