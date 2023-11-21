package bpm.google.spreadsheet.oda.driver.ui.impl.dataset;

public class FilterDescription {
	
	private String filterName;
	private String filterOperator;
	private String filterValue1;
	private String filterValue2;
	private String filterLogicalOperator;
	
	public FilterDescription(String filterName, String filterOperator,
			String filterValue1, String filterValue2, String pLogical) {
		super();
		this.filterName = filterName;
		this.filterOperator = filterOperator;
		this.filterValue1 = filterValue1;
		this.filterValue2 = filterValue2;
		this.filterLogicalOperator =  pLogical;
	}
	
	public String getFilterName() {
		return filterName;
	}
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	public String getFilterOperator() {
		return filterOperator;
	}
	public void setFilterOperator(String filterOperator) {
		this.filterOperator = filterOperator;
	}
	public String getFilterValue1() {
		return filterValue1;
	}
	public void setFilterValue1(String filterValue1) {
		this.filterValue1 = filterValue1;
	}
	public String getFilterValue2() {
		return filterValue2;
	}
	public void setFilterValue2(String filterValue2) {
		this.filterValue2 = filterValue2;
	}

	public String getFilterLogicalOperator() {
		return filterLogicalOperator;
	}

	public void setFilterLogicalOperator(String filterLogicalOperator) {
		this.filterLogicalOperator = filterLogicalOperator;
	}
	
	

}
