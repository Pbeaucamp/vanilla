package bpm.united.olap.api.runtime.calculation;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.RunResult;

public class AverageCalculation implements ICalculation {

	private Measure measure;
	private DataCell cell;
	private int count = 0;
	private boolean isPercentile = false;
	private DataObjectItem item;
	
	public AverageCalculation(DataCell cell, Measure measure) {
		this.measure = measure;
		this.item = measure.getItem();
		this.cell = cell;
	}
	
	@Override
	public Measure getMeasure() {
		return measure;
	}

	@Override
	public void makeCalcul() {
//		if(cell.getValues() != null && cell.getValuesByKey(measure.getItem().getName()) != null && cell.getValuesByKey(measure.getItem().getName()).size() > 0) {
//			int count = cell.getValuesByKey(measure.getItem().getName()).size();
//			double value = 0.0;
//			for(Double val : cell.getValuesByKey(measure.getItem().getName())) {
//				value += val;
//			}
//			double result = value/count;
//			cell.setResultValue(result);
//		}
//		else {
//			cell.setResultValue(null);
//		}
		
		if(cell.getResultValue() != null && count > 0) {
			cell.setResultValue(cell.getResultValue() / count);
		}
	}

	@Override
	public void makeCalculDuringQuery(boolean isOnImprovedQuery) {
		if(cell.getValues() != null && cell.getValuesByKey(measure.getUname()) != null && cell.getValuesByKey(measure.getUname()).size() > 0) {
			for(Double val : cell.getValuesByKey(measure.getUname())) {
				if(cell.getResultValue() == null) {
					cell.setResultValue(0.0);
				}
				cell.setResultValue(cell.getResultValue() + val);
			}
			count += cell.getValues().size();
			cell.getValues().clear();
		}
		if(cell.getValues() != null) {
			cell.getValues().clear();
		}
	}

	@Override
	public DataCell getDataCell() {
		return cell;
	}

	@Override
	public boolean isPercentile() {
		return isPercentile;
	}

	@Override
	public void setIsPercentile(boolean isPercentile) {
		this.isPercentile = isPercentile;
		if (isPercentile) {
			cell.setFormat("Percent");
		}
	}

	@Override
	public void setItem(DataObjectItem item) {
		this.item = item;
	}
}
