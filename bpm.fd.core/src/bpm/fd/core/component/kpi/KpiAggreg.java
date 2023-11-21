package bpm.fd.core.component.kpi;

import java.io.Serializable;

import bpm.fm.api.model.Metric;

public class KpiAggreg implements Serializable {

	public static String COLUMN_ID = "Metric id";
	public static String COLUMN_NAME = "Metric name";
	public static String COLUMN_VALUE = "Value";
	public static String COLUMN_OBJ = "Objective";
	public static String COLUMN_MIN = "Minimum";
	public static String COLUMN_MAX = "Maximum";
	
	public static String[] columnNames = new String[]{
		"Metric id" , "Metric name", "Value", "Objective", "Minimum", "Maximum"};

	private Metric metric;
	private String column;
	private String operator;

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
