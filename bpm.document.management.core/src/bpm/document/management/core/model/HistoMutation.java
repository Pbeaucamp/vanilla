package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class HistoMutation implements Serializable {

	private int id;
	private String origin;
	private String destination;
	private Date dateMutation;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Date getDateMutation() {
		return dateMutation;
	}

	public void setDateMutation(Date dateMutation) {
		this.dateMutation = dateMutation;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
