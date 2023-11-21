package bpm.fm.api.model.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.Metric;

/**
 * A class to get all the informations needed for the loader values
 * 
 * Contains metric/date infos
 *          a list of AxisInfos with all the levelMembers
 *          a list of loaderValues (the existing values)
 * 
 * @author Marc
 *
 */
public class LoaderDataContainer implements Serializable {

	private Metric metric;
	
	private List<AxisInfo> axisInfos = new ArrayList<AxisInfo>();
	
	private List<LoaderMetricValue> values = new ArrayList<LoaderMetricValue>();

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public List<AxisInfo> getAxisInfos() {
		return axisInfos;
	}

	public void setAxisInfos(List<AxisInfo> axisInfos) {
		this.axisInfos = axisInfos;
	}
	
	public void addAxisInfo(AxisInfo axisInfo) {
		this.axisInfos.add(axisInfo);
	}

	public List<LoaderMetricValue> getValues() {
		return values;
	}

	public void setValues(List<LoaderMetricValue> values) {
		this.values = values;
	}
	
	public void addValue(LoaderMetricValue value) {
		this.values.add(value);
	}
}
