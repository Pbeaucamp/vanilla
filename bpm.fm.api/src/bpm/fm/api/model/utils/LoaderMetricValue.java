package bpm.fm.api.model.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.model.Metric;

/**
 * A class used in FmLoaderWeb I created this because it has a lot of info
 * completly useless in other software. It's to keep good performance.
 * 
 * @author Marc
 * 
 */
public class LoaderMetricValue implements Serializable {

	private static final long serialVersionUID = 8872162198324249455L;

	private Metric metric;

	private double value;
	private Date date;

	private double objective;
	private double minimum;
	private double maximum;
	
	private boolean isNew;
	private boolean isUpdated;
	private boolean isDeleted;

	private List<LevelMember> members = new ArrayList<LevelMember>();

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getObjective() {
		return objective;
	}

	public void setObjective(double objective) {
		this.objective = objective;
	}

	public double getMinimum() {
		return minimum;
	}

	public void setMinimum(double minimum) {
		this.minimum = minimum;
	}

	public double getMaximum() {
		return maximum;
	}

	public void setMaximum(double maximum) {
		this.maximum = maximum;
	}
	
	@Override
	public String toString() {
		return "value = " + value + "\nobj = " + objective + "\nmin = " + minimum + "\nmax = " + maximum + "\ndate = " + 
				(date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + (date.getDay() + 1);
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isUpdated() {
		return isUpdated;
	}

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}
	
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public List<LevelMember> getMembers() {
		return members;
	}

	public void setMembers(List<LevelMember> members) {
		this.members = members;
	}

	@Override
	public boolean equals(Object obj) {
		int i = 0;
		for(LevelMember axis : members) {
			if(!((LoaderMetricValue)obj).getMembers().get(i).equals(axis)) {
				return false;
			}
			i++;
		}
		
		return metric.getId() == ((LoaderMetricValue)obj).getMetric().getId();
	}

	public void addMember(LevelMember member) {
		this.members.add(member);
	}

}
