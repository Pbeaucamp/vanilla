package bpm.odata.service.data;

import java.util.Date;

public class FilterResult {

	private Date startDate;
	private Date endDate;
	
	public FilterResult(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
}
