package bpm.fm.api.model.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;

public class MetricSqlQueries implements Serializable {
	
	private Metric metric;
	
	private String factQuery;
	private String objectiveQuery;
	
	private List<AxisQueries> axisQueries = new ArrayList<AxisQueries>();
	
	public MetricSqlQueries() {}
	
	public MetricSqlQueries(Metric metric) {
		this.metric = metric;
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public String getFactQuery() {
		return factQuery;
	}

	public void setFactQuery(String factQuery) {
		this.factQuery = factQuery;
	}

	public String getObjectiveQuery() {
		return objectiveQuery;
	}

	public void setObjectiveQuery(String objectiveQuery) {
		this.objectiveQuery = objectiveQuery;
	}

	public List<AxisQueries> getAxisQueries() {
		return axisQueries;
	}

	public void setAxisQueries(List<AxisQueries> axisQueries) {
		this.axisQueries = axisQueries;
	}
	
}
