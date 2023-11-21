package bpm.fa.api.olap.projection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ProjectionMeasure implements Serializable {

	private String uname;
	private String formula;
	private List<ProjectionMeasureCondition> conditions;
	
	public String getUname() {
		return uname;
	}
	
	public void setUname(String uname) {
		this.uname = uname;
	}
	
	public String getFormula() {
		return formula;
	}
	
	public void setFormula(String formula) {
		this.formula = formula;
	}

	public void setConditions(List<ProjectionMeasureCondition> conditions) {
		this.conditions = conditions;
	}

	public List<ProjectionMeasureCondition> getConditions() {
		if(conditions == null) {
			conditions = new ArrayList<ProjectionMeasureCondition>();
		}
		return conditions;
	}
	
	public void addCondition(ProjectionMeasureCondition condition) {
		if(conditions == null) {
			conditions = new ArrayList<ProjectionMeasureCondition>();
		}
		conditions.add(condition);
	}
}
