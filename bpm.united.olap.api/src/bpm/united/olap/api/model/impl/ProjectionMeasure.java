package bpm.united.olap.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.united.olap.api.model.Measure;

/**
 * A projection measure. Contains the conditions to make the calcul.
 * If it's an extrapolation, the formula will contains the type of extrapolation.
 * 
 * @author Marc Lanquetin
 *
 */
public class ProjectionMeasure extends MeasureImpl {

	private List<ProjectionMeasureCondition> conditions;
	private String formula;
	private Measure measure;

	public void setConditions(List<ProjectionMeasureCondition> conditions) {
		this.conditions = conditions;
	}

	public List<ProjectionMeasureCondition> getConditions() {
		return conditions;
	}
	
	public ProjectionMeasure() {
		
	}
	
	public ProjectionMeasure(Measure mes) {
		this.setMeasure(mes);
		this.setCalculationType(mes.getCalculationType());
	}

	public void setMeasure(Measure measure) {
		this.measure = measure;
	}

	public Measure getMeasure() {
		return measure;
	}

	public void addCondition(ProjectionMeasureCondition c) {
		if(conditions == null) {
			conditions = new ArrayList<ProjectionMeasureCondition>();
		}
		conditions.add(c);
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getFormula() {
		return formula;
	}
	
}
