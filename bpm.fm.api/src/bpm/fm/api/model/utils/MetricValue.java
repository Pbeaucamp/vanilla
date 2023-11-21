package bpm.fm.api.model.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.AlertRaised;
import bpm.fm.api.model.CalculatedFactTable;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricAction;

public class MetricValue implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int HEALTH_BAD = -1;
	public static final int HEALTH_OK = 0;
	public static final int HEALTH_GOOD = 1;
	
	private Metric metric;
	
	private double value;
	private Date date;
	
	private double objective;
	private double minimum;
	private double maximum;
	
	private double tolerance;
	
	private int health = 0;
	
	private HashMap<MetricAction, ActionResult> actionResults = new HashMap<MetricAction, ActionResult>();
	private List<MetricValue> children = new ArrayList<MetricValue>();
	
	private List<LevelMember> axis;
	
	private List<AlertRaised> raised = new ArrayList<AlertRaised>();
	
	private int tendancy;
	
	private boolean dummy;

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

	public int getHealth() {
		
		if(metric.getMetricType().equals(Metric.METRIC_TYPES.get(Metric.TYPE_KAPLAN))) {
			int goodHealth = 0;
			for(MetricValue val : children) {
				if(val.getHealth() > 0) {
					goodHealth++;
				}
			}
			String calc = ((CalculatedFactTable)metric.getFactTable()).getCalculation();
			if(calc.equals(Metric.KAPLAN_TYPES.get(Metric.KAPLAN_ALL))) {
				if(goodHealth == children.size()) {
					return 1;
				}
				return -1;
			}
			else if(calc.equals(Metric.KAPLAN_TYPES.get(Metric.KAPLAN_MAJ))) {
				if(goodHealth > children.size() / 2) {
					return 1;
				}
				return -1;
			}
			else if(calc.equals(Metric.KAPLAN_TYPES.get(Metric.KAPLAN_ONE))) {
				if(goodHealth >0) {
					return 1;
				}
				return -1;
			}
		}
		else {
			
			double objectiveMin = objective - ((objective * tolerance) /100);
			double objectiveMax = objective + ((objective * tolerance) /100);
			
			if(metric.getDirection().equals(Metric.DIRECTIONS.get(Metric.DIRECTION_BOTTOM))) {
				if(value < objectiveMin) {
					return 1;
				}
				else if(value <= objectiveMax && value >= objectiveMin) {
					return 0;
				}
				else if(value > objectiveMax) {
					return -1;
				}
			}
			else if(metric.getDirection().equals(Metric.DIRECTIONS.get(Metric.DIRECTION_TOP))) {
				if(value < objectiveMin) {
					return -1;
				}
				else if(value <= objectiveMax && value >= objectiveMin) {
					return 0;
				}
				else if(value > objectiveMax) {
					return 1;
				}
			}
			else {
				if(value == objective) {
					return 1;
				}
				else if(value <= objectiveMax && value >= objectiveMin) {
					return 0;
				}
				else {
					return -1;
				}
			}
		}
		
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * Can be null
	 * @return
	 */
	public List<LevelMember> getAxis() {
		return axis;
	}

	public void setAxis(List<LevelMember> axis) {
		this.axis = axis;
	}
	
	@Override
	public String toString() {
		return "value = " + value + "\nobj = " + objective + "\nmin = " + minimum + "\nmax = " + maximum + "\ndate = " + 
				(date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + (date.getDate()) + "\nhealth = " + health + "\naxis = " + axis;
	}

	public void add(LevelMember val) {
		if(this.axis == null) {
			axis = new ArrayList<LevelMember>();
		}
		this.axis.add(val);
	}

	public List<MetricValue> getChildren() {
		return children;
	}

	public void setChildren(List<MetricValue> children) {
		this.children = children;
	}

	public int getTendancy() {
		return tendancy;
	}

	public void setTendancy(int tendancy) {
		this.tendancy = tendancy;
	}

	public List<AlertRaised> getRaised() {
		return raised;
	}

	public void setRaised(List<AlertRaised> raised) {
		this.raised = raised;
	}

	public HashMap<MetricAction, ActionResult> getActionResults() {
		return actionResults;
	}

	public void setActionResults(HashMap<MetricAction, ActionResult> actionResults) {
		this.actionResults = actionResults;
	}

	public double getTolerance() {
		return tolerance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public boolean isDummy() {
		return dummy;
	}

	public void setDummy(boolean dummy) {
		this.dummy = dummy;
	}
}
