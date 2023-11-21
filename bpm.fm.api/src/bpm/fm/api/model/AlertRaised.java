package bpm.fm.api.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.alerts.Alert;

@Entity
@Table (name = "fm_alert_raised")
public class AlertRaised implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "alert_id")
	private int alertId;
	
	@Column (name = "alert_date")
	private Date date;
	
	@Column (name = "handled")
	private boolean hasBeenHandled = false;
	
	@Column (name = "resolution_date")
	private Date resolutionDate;
	
	@Transient
	private Alert alert;
	
	@Transient
	private Metric metric;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAlertId() {
		return alertId;
	}

	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isHasBeenHandled() {
		return hasBeenHandled;
	}

	public void setHasBeenHandled(boolean hasBeenHandled) {
		this.hasBeenHandled = hasBeenHandled;
	}

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) {
		this.alert = alert;
	}

	public Date getResolutionDate() {
		return resolutionDate;
	}

	public void setResolutionDate(Date resolutionDate) {
		this.resolutionDate = resolutionDate;
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}
}
