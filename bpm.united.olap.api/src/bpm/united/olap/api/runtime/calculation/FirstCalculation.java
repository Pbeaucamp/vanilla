package bpm.united.olap.api.runtime.calculation;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.tools.AlphanumComparator;

public class FirstCalculation implements ICalculation {

	private DataCell cell;
	private Measure measure;
	private boolean isPercentile = false;
	private DataObjectItem item;
	private Member previousDate;
	
	public FirstCalculation(DataCell cell, Measure measure) {
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

	}

	@Override
	public void makeCalculDuringQuery(boolean isOnImprovedQuery) {
		if(cell.getValues() != null && cell.getValuesByKey(measure.getUname()) != null && cell.getValuesByKey(measure.getUname()).size() > 0) {
			for(Double val : cell.getValuesByKey(measure.getUname())) {
				cell.setResultValue(val);
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
	
	public boolean validateDate(Member member) {
		if(previousDate == null) {
			previousDate = member;
			return true;
		}
		
		String value1 = null;
		String value2 = null;
		
		if(member.getOrderValue() != null) {
			value1 = member.getOrderUname();
		}
		else {
			value1 = member.getUname();
		}
		
		if(previousDate.getOrderValue() != null) {
			value2 = previousDate.getOrderUname();
		}
		else {
			value2 = previousDate.getUname();
		}
		
		int res = new AlphanumComparator().compare(value1, value2);
		
		if(res <= 0) {
			previousDate = member;
			return true;
		}
		
		return false;
	}

}
