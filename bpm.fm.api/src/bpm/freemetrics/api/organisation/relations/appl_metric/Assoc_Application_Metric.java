package bpm.freemetrics.api.organisation.relations.appl_metric;

import java.util.List;

import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.metrics.Metric;

public class Assoc_Application_Metric {
	
	private int id;
	private String name;
	
	private int app_ID;
	private int metr_ID;
	
	private List<Application> applications;
	private Metric metric;
	
	/**
	 * @return the app_ID
	 */
	public int getApp_ID() {
		return app_ID;
	}

	/**
	 * @param app_ID the app_ID to set
	 */
	public void setApp_ID(int app_ID) {
		this.app_ID = app_ID;
	}

	/**
	 * @return the metr_ID
	 */
	public int getMetr_ID() {
		return metr_ID;
	}

	/**
	 * @param metr_ID the metr_ID to set
	 */
	public void setMetr_ID(int metr_ID) {
		this.metr_ID = metr_ID;
	}

	public Assoc_Application_Metric() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}

	public List<Application> getApplications() {
		return applications;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
		metr_ID = metric.getId();
	}

	public Metric getMetric() {
		return metric;
	}
	
	public String getApplicationsName() {
		StringBuilder buf = new StringBuilder();
		
		for(Application app : applications) {
			buf.append(app.getName() + "_");
		}
		if (buf.length() == 0){
			return "";
		}
		return buf.toString().substring(0, buf.length() - 1);
	}
}
