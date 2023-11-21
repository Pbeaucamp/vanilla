package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class SuspendedTasks implements Serializable {

	private String email;
	private Date suspendedDate;
	private int nbTasks;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getNbTasks() {
		return nbTasks;
	}

	public void setNbTasks(int nbTasks) {
		this.nbTasks = nbTasks;
	}

	public Date getSuspendedDate() {
		return suspendedDate;
	}

	public void setSuspendedDate(Date suspendedDate) {
		this.suspendedDate = suspendedDate;
	}

}
