package bpm.united.olap.api.model.impl;

public class DateLevel extends LevelImpl {

	private String datePattern;
	private String dateType;
	private String datePart;
	private String dateOrder;
	
	public String getDatePattern() {
		return datePattern;
	}
	
	public void setDatePattern(String pattern) {
		this.datePattern = pattern;
	}
	
	public String getDateType() {
		return dateType;
	}
	
	public void setDateType(String type) {
		this.dateType = type;
	}
	
	public String getDatePart() {
		return datePart;
	}
	
	public void setDatePart(String part) {
		this.datePart = part;
	}

	public void setDateOrder(String dateOrder) {
		this.dateOrder = dateOrder;
	}

	public String getDateOrder() {
		return dateOrder;
	}
	
}
