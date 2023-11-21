package bpm.vanilla.api.runtime.dto.kpi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.MetricValue;

public class MetricValueDTO {
	
	//private int metricID;
	//private String metricName;
	private List<LevelMemberDTO> axis;
	private double value;
	private int tendancy;
	private int health = 0;
	private Date date;
	private double objective;
	private double minimum;
	private double maximum;	
	
	
	
	
	public MetricValueDTO(MetricValue value) {
		
		//this.metricID = value.getMetric().getId();
		//this.metricName= value.getMetric().getName();
		List<LevelMember> levels = value.getAxis();
		this.axis = new ArrayList<>();
		if (levels != null) {
			for (LevelMember lvl : levels) {
				this.axis.add(new LevelMemberDTO(lvl));
			}
		}
		
		
		this.value = value.getValue();
		this.tendancy = value.getTendancy();
		this.date = value.getDate();
		this.objective = value.getObjective();
		this.minimum = value.getMaximum();
		this.maximum = value.getMinimum();
		this.health = value.getHealth();
		
	}
	

	
	


	@Override
	public String toString() {
		return "MetricValueDTO [axis=" + axis + ", value=" + value + ", tendancy=" + tendancy + ", health=" + health + ", date=" + date + ", objective=" + objective + ", minimum=" + minimum + ", maximum=" + maximum + "]";
	}


	public List<LevelMemberDTO> getAxis() {
		return axis;
	}

	public double getValue() {
		return value;
	}
	
	public int getTendancy() {
		return tendancy;
	}

	public Date getDate() {
		return date;
	}

	public double getObjective() {
		return objective;
	}

	public double getMinimum() {
		return minimum;
	}

	public double getMaximum() {
		return maximum;
	}

	public int getHealth() {
		return health;
	}
	
	
}
