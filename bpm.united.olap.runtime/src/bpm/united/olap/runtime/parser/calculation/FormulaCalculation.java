package bpm.united.olap.runtime.parser.calculation;

import java.text.ParseException;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.impl.ComplexMeasure;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.calculation.ICalculation;

public class FormulaCalculation implements ICalculation {	
	
	private DataCell cell;
	private ComplexMeasure measure;
	private boolean isPercentile = false;
	
	public FormulaCalculation(DataCell cell, ComplexMeasure mes) {
		this.cell = cell;
		this.measure = mes;
		if(mes.getFormat() != null && !mes.getFormat().equalsIgnoreCase("")) {
			this.cell.setFormat(mes.getFormat());
		}
	}

	@Override
	public Measure getMeasure() {
		return measure;
	}

	@Override
	public void makeCalcul() {
		
		Object left = measure.getLeftItem();
		Object right = measure.getRightItem();
		Double leftValue = null;
		Double rightValue = null;
		try {
			leftValue = findValueForOperand(left, measure.getOperator());
			rightValue = findValueForOperand(right, measure.getOperator());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		try {
			Double result = computeCalcul(leftValue, rightValue, measure.getOperator(), measure.getFormat());
			cell.setResultValue(result);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private Double computeCalcul(Double leftValue, Double rightValue, String operator, String format) throws ParseException {
		
		Double result = null;
		
		if(leftValue == null || rightValue == null) {
			return result;
		}
		
		if(operator.equalsIgnoreCase("/")) {
			result = leftValue / rightValue;
		}
		else if(operator.equalsIgnoreCase("*")) {
			result = leftValue / rightValue;
		}
		else if(operator.equalsIgnoreCase("+")) {
			result = leftValue / rightValue;
		}
		else if(operator.equalsIgnoreCase("-")) {
			result = leftValue / rightValue;
		}
		
		result = formatResult(result, format);
		
		return result;
	}

	private Double formatResult(Double result, String format) {
		
		if(format.equalsIgnoreCase("Percent")) {
			
		}
		
		return result;
	}

	private Double findValueForOperand(Object item, String operator) throws ParseException {
		return null;
	}

	@Override
	public void makeCalculDuringQuery(boolean isOnImprovedQuery) {
		cell.getValues().clear();
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
	}

	@Override
	public void setItem(DataObjectItem item) {
		
		
	}
}
