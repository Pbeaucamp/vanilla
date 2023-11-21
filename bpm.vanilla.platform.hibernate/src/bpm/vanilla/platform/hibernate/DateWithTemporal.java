package bpm.vanilla.platform.hibernate;

import java.util.Date;

import javax.persistence.TemporalType;

/**
 * This class is used to manage Date parameter for hibernate.
 * It allows to set the TemporalType to be managed by hibernate.
 */
public class DateWithTemporal {

	private Date date;
	private TemporalType temporal;
	
	public DateWithTemporal(Date date, TemporalType temporal) {
		this.date = date;
		this.temporal = temporal;
	}
	
	public Date getDate() {
		return date;
	}
	
	public TemporalType getTemporal() {
		return temporal;
	}
}

