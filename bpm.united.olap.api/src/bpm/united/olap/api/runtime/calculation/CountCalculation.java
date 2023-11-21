package bpm.united.olap.api.runtime.calculation;

import java.text.NumberFormat;
import java.text.ParseException;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.RunResult;

public class CountCalculation implements ICalculation {

	private DataCell cell;
	private Measure measure;
	private boolean isPercentile = false;
	private DataObjectItem item;
	
	public CountCalculation(DataCell cell, Measure measure) {
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
//			String result = NumberFormat.getInstance().format(cell.getValuesByKey(measure.getItem().getName()).size());
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
			
			if(isOnImprovedQuery) {
				for(Double val : cell.getValuesByKey(measure.getUname())) {
					if (cell.getResultValue() != null){
						cell.setResultValue(val + cell.getResultValue());
					}
					else{
						cell.setResultValue(val);
					}
					
				}
				
				
				cell.getValues().clear();
				
			}
			else {
				
				double count = NumberFormat.getInstance().parse(cell.getValues().size()+"").doubleValue();
//				 XXX : LCA we need a way to know if the datas are coming already aggregated or not
//				 if they are, the ode is right, if they are not, the commented is good

				if(cell.getResultValue() != null) {
					cell.setResultValue(cell.getResultValue() + count);
				}
				else {
					cell.setResultValue(count);
				}
			}
			//double count = NumberFormat.getInstance().parse(cell.getValues().size()+"").doubleValue();
			// XXX : LCA we need a way to know if the datas are coming already aggregated or not
			// if they are, the ode is right, if they are not, the commented is good

//			if(cell.getResultValue() != null) {
//				cell.setResultValue(cell.getResultValue() + count);
//			}
//			else {
//				cell.setResultValue(count);
//			}

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
