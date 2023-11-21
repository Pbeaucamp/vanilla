package bpm.faweb.client.projection;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProjectionMeasure implements IsSerializable {

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
