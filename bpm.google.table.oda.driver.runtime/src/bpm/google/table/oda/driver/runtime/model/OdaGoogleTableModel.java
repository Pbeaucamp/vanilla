package bpm.google.table.oda.driver.runtime.model;

import java.util.ArrayList;

public class OdaGoogleTableModel {
	
	private int tableCountCol, tableCountRow;
	
	private ArrayList<OdaGoogleTableColumn> listColumns;
	private ArrayList<OdaGoogleTableRow> listRows;
	
	public OdaGoogleTableModel(){
		
		listColumns = new ArrayList<OdaGoogleTableColumn>();
		listRows = new ArrayList<OdaGoogleTableRow>();
	}
	
	public void sortList(String[] listNameColsSelected) {
		
		//Method to remove non-selected columns and concerned cells.
		
		ArrayList<OdaGoogleTableColumn> listColSelected = new ArrayList<OdaGoogleTableColumn>();
		ArrayList<Integer> listIndexKeeped = new ArrayList<Integer>();
		
	//Built a arraylist with the selected columns
		for(String nameCol : listNameColsSelected){
			
			for(OdaGoogleTableColumn col : listColumns){
				if(nameCol.equals(col.getColName())){
					listColSelected.add(col);
					listIndexKeeped.add(col.getColIndex());
				}
			}
		}
		
	//Change cells with values from selected column, in the right order.
		ArrayList<String> newListCells = new ArrayList<String>();
		ArrayList<OdaGoogleTableRow> newListRows = new ArrayList<OdaGoogleTableRow>();
		
		for(OdaGoogleTableRow row : listRows){
			
			newListCells.clear();
			
			for(Integer indexCol : listIndexKeeped){
				newListCells.add(row.getListCells().get(indexCol));
			}
			
			newListRows.add(new OdaGoogleTableRow(newListCells, false));
		}
		
		listRows.clear();
		listRows = newListRows;
		
	//Set index of the new column List
		int newIndex = 0;
		
		for(OdaGoogleTableColumn col: listColSelected){
			col.setColIndex(newIndex);
			newIndex++;
		}
		
	//Set the column Count
		
		tableCountCol = listColSelected.size();
		
	//Set the new column list
		
		listColumns = listColSelected;
		
	}
	


	public int getTableCountCol() {
		return tableCountCol;
	}

	public void setTableCountCol(int tableCountCol) {
		this.tableCountCol = tableCountCol;
	}

	public int getTableCountRow() {
		return tableCountRow;
	}

	public void setTableCountRow(int tableCountRow) {
		this.tableCountRow = tableCountRow;
	}

	public ArrayList<OdaGoogleTableColumn> getListColumns() {
		return listColumns;
	}

	public void setListColumns(ArrayList<OdaGoogleTableColumn> listColumns) {
		this.listColumns = listColumns;
	}

	public ArrayList<OdaGoogleTableRow> getListRows() {
		return listRows;
	}

	public void setListRows(ArrayList<OdaGoogleTableRow> listRows) {
		this.listRows = listRows;
	}



}
