package bpm.fm.api.model;

import java.io.Serializable;
import java.util.List;

import bpm.fm.api.model.utils.MapZoneValue;

public class ComplexObjectViewer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ComplexMapLevel cpxLevel;
	private ComplexMapMetric cpxMetric;
	private Metric metric;
	private Level level;
	private List<MapZoneValue> mapvalues;
	
	public ComplexObjectViewer() {
		super();
	}

	public ComplexObjectViewer(ComplexMapMetric cpxMetric, ComplexMapLevel cpxLevel,
			List<MapZoneValue> mapvalues) {
		super();
		this.cpxMetric = cpxMetric;
		this.cpxLevel = cpxLevel;
		this.metric = cpxMetric.getMetric();
		this.level = cpxLevel.getLevel();
		this.mapvalues = mapvalues;
	}

	

	public ComplexMapLevel getCpxLevel() {
		return cpxLevel;
	}

	public void setCpxLevel(ComplexMapLevel cpxLevel) {
		this.cpxLevel = cpxLevel;
	}

	public ComplexMapMetric getCpxMetric() {
		return cpxMetric;
	}

	public void setCpxMetric(ComplexMapMetric cpxMetric) {
		this.cpxMetric = cpxMetric;
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public List<MapZoneValue> getMapvalues() {
		return mapvalues;
	}

	public void setMapvalues(List<MapZoneValue> mapvalues) {
		this.mapvalues = mapvalues;
	}
	
	
	
	

}
