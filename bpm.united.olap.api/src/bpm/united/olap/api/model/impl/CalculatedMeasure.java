package bpm.united.olap.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.united.olap.api.model.Measure;

public class CalculatedMeasure extends MeasureImpl {

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
