package bpm.united.olap.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.aggregation.ILevelAggregation;

public class DynamicMeasure extends MeasureImpl {

	private List<ILevelAggregation> aggregations;

	public void setAggregations(List<ILevelAggregation> aggregations) {
		this.aggregations = aggregations;
	}

	public List<ILevelAggregation> getAggregations() {
		return aggregations;
	}
	
	public void addAggregation(ILevelAggregation aggregation) {
		if(aggregations == null) {
			aggregations = new ArrayList<ILevelAggregation>();
		}
		aggregations.add(aggregation);
	}
	
	private List<Measure> items;
	private String formula;
	
	public List<Measure> getItems() {
		return items;
	}
	
	public void setItems(List<Measure> items) {
		this.items = items;
	}
	
	public void addItem(Measure item) {
		if(items == null) {
			items = new ArrayList<Measure>();
		}
		items.add(item);
	}
	
	public String getFormula() {
		return formula;
	}
	
	public void setFormula(String formula) {
		this.formula = formula;
	}
}
