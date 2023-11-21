package bpm.google.table.oda.driver.runtime.model;

import java.util.ArrayList;

public class OdaGoogleTableRow {
	
	private ArrayList<String> listCells;
	private boolean isFiltered;
	
	
	public OdaGoogleTableRow(ArrayList<String> pListCells, boolean isFiltered) {
		super();
		this.listCells = new ArrayList<String>();
		listCells.addAll(pListCells);
		
		
		this.isFiltered = isFiltered;
	}
	
	
	public ArrayList<String> getListCells() {
		return listCells;
	}
	public void setListCells(ArrayList<String> listCells) {
		this.listCells = listCells;
	}
	public boolean isFiltered() {
		return isFiltered;
	}
	public void setFiltered(boolean isFiltered) {
		this.isFiltered = isFiltered;
	}


}
