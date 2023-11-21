package bpm.es.web.shared.beans;

import java.util.Date;

public class NameDateItem {

	private int id;
	private String name;
	private Date date;
	private Date date2;
	
	public NameDateItem() { }
	
	public NameDateItem(String name, Date date, Date date2) {
		this.name = name;
		this.date = date;
		this.date2 = date2;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Date getDate2() {
		return date2;
	}
	
	public void setDate2(Date date2) {
		this.date2 = date2;
	}
}
