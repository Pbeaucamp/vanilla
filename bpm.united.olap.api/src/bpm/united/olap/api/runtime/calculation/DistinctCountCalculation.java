package bpm.united.olap.api.runtime.calculation;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.RunResult;

public class DistinctCountCalculation implements ICalculation {

	private Measure measure;
	private DataCell cell;
	private List<Double> values = new ArrayList<Double>();
	private boolean isPercentile = false;
	private DataObjectItem item;
	
	public DistinctCountCalculation(DataCell cell, Measure measure) {
		this.cell = cell;
		this.measure = measure;
		this.item = measure.getItem();
	}
	
	@Override
	public Measure getMeasure() {
		return measure;
	}

	@Override
	public void makeCalcul() {
//		if(cell.getValues() != null && cell.getValuesByKey(measure.getItem().getName()) != null && cell.getValuesByKey(measure.getItem().getName()).size() > 0) {
//			int count = 0;
//			List<Double> countedValues = new ArrayList<Double>();
//			
//			for(Double val : cell.getValuesByKey(measure.getItem().getName())) {
//				if(!countedValues.contains(val)) {
//					count++;
//					countedValues.add(val);
//				}
//			}
//			
//			String result = NumberFormat.getInstance().format(count);
//			Double res = null;
//			try {
//				res = NumberFormat.getInstance().parse(result).doubleValue();
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			cell.setResultValue(res);
//		}
//		else {
//			cell.setResultValue(new Double(0));
//		}
	}


	@Override
	public void makeCalculDuringQuery(boolean isOnImprovedQuery) throws ParseException {
		if(cell.getValues() != null && cell.getValuesByKey(measure.getUname()) != null && cell.getValuesByKey(measure.getUname()).size() > 0) {
			
			for(Double val : cell.getValuesByKey(measure.getUname())) {
				if(!values.contains(val)) {
					values.add(val);
					
					if(isOnImprovedQuery) {
						cell.setResultValue(val);
					}
					else {
						// XXX : LCA we need a way to know if the datas are coming already aggregated or not
						// if they are, the ode is right, if they are not, the commented is good
						if(cell.getResultValue() != null) {
							cell.setResultValue(cell.getResultValue() + 1);
						}
						else {
							cell.setResultValue(1.0);
						}
					}
				}
			}
			
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
