package bpm.united.olap.api.runtime.calculation;

import java.util.Collections;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.RunResult;

public class MinimumCalculation implements ICalculation {

	private DataCell cell;
	private Measure measure;
	private boolean isPercentile = false;
	private DataObjectItem item;
	
	public MinimumCalculation(DataCell cell, Measure measure) {
		this.measure = measure;
		this.cell = cell;
		this.item = measure.getItem();
	}
	
	@Override
	public Measure getMeasure() {
		return measure;
	}

	@Override
	public void makeCalcul() {
//		if(cell.getValues() != null && cell.getValuesByKey(measure.getItem().getName()) != null && cell.getValuesByKey(measure.getItem().getName()).size() > 0) {
//			Double result = Collections.min(cell.getValuesByKey(measure.getItem().getName()));
//			cell.setResultValue(result);
//		}
//		else {
//			cell.setResultValue(null);
//		}
	}

	@Override
	public void makeCalculDuringQuery(boolean isOnImprovedQuery) {
		if(cell.getValues() != null && cell.getValuesByKey(measure.getUname()) != null && cell.getValuesByKey(measure.getUname()).size() > 0) {
			Double result = Collections.min(cell.getValuesByKey(measure.getUname()));
			if(cell.getResultValue() == null) {
				cell.setResultValue(result);
			}
			else {
				if(cell.getResultValue() > result) {
					cell.setResultValue(result);
				}
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
