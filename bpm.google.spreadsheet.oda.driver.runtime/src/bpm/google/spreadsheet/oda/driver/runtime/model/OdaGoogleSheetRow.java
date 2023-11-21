package bpm.google.spreadsheet.oda.driver.runtime.model;

import java.util.ArrayList;

public class OdaGoogleSheetRow {
	
	private ArrayList<String> rowListCell;
	private boolean isFiltered;
	
	public OdaGoogleSheetRow(){
		
		rowListCell = new ArrayList<String>();
		isFiltered = false;
	}
	

	public OdaGoogleSheetRow(ArrayList<String> rowListCell, boolean isFiltered) {
		super();
		this.rowListCell = rowListCell;
		this.isFiltered = isFiltered;
	}


	public ArrayList<String> getRowListCell() {
		return rowListCell;
	}

	public void setRowListCell(ArrayList<String> rowListCell) {
		this.rowListCell = rowListCell;
	}

	public boolean isFiltered() {
		return isFiltered;
	}

	public void setFiltered(boolean isFiltered) {
		this.isFiltered = isFiltered;
	}

}
