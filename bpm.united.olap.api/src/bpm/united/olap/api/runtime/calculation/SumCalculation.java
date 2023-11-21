package bpm.united.olap.api.runtime.calculation;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.RunResult;

public class SumCalculation implements ICalculation {

	private DataCell cell;
	private Measure measure;
	private boolean isPercentile = false;
	private DataObjectItem item;
	
	public SumCalculation(DataCell cell, Measure measure) {
		this.cell = cell;
		this.measure = measure;
		this.item = measure.getItem();
	}

	@Override
	public void makeCalcul() {
		
//		if(cell.getValues() != null && cell.getValuesByKey(measure.getItem().getName()) != null && cell.getValuesByKey(measure.getItem().getName()).size() > 0) {
//			double result = 0.0;
//			for(Double val : cell.getValuesByKey(measure.getItem().getName())) {
//				result += val;
//			}
//			
//			cell.setResultValue(result);
//			
//		}
//		
//		else {
//			cell.setResultValue(null);
//		}
		
	}

	@Override
	public Measure getMeasure() {
		return this.measure;
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
