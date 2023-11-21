package bpm.united.olap.api.model.impl;

import bpm.united.olap.api.model.impl.MeasureImpl;

public class ComplexMeasure extends MeasureImpl {

	public static String BASICFORMULA = "basicFormula";
	
	private String operator;
	private Object leftItem;
	private Object rightItem;
	private String type;
	private String format;
	
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public Object getLeftItem() {
		return leftItem;
	}
	public void setLeftItem(Object leftItem) {
		this.leftItem = leftItem;
	}
	public Object getRightItem() {
		return rightItem;
	}
	public void setRightItem(Object rightItem) {
		this.rightItem = rightItem;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getFormat() {
		return format;
	}
	
	
	
	
	
}
