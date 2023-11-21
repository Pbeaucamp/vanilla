package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class ReferenceDocuments implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	
	private int refA;
	private int refB;
	
	private Date dateAttach;
	
	//Transient
	private IObject attachItem;

	public ReferenceDocuments() { }

	public ReferenceDocuments(int refA, int refB) {
		super();
		this.refA = refA;
		this.refB = refB;
		this.dateAttach = new Date();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRefA() {
		return refA;
	}

	public void setRefA(int refA) {
		this.refA = refA;
	}

	public int getRefB() {
		return refB;
	}

	public void setRefB(int refB) {
		this.refB = refB;
	}
	
	public Date getDateAttach() {
		return dateAttach;
	}
	
	public void setDateAttach(Date dateAttach) {
		this.dateAttach = dateAttach;
	}
	
	public IObject getAttachItem() {
		return attachItem;
	}
	
	public void setAttachItem(IObject attachItem) {
		this.attachItem = attachItem;
	}

}
