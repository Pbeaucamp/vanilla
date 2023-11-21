package bpm.google.spreadsheet.oda.driver.runtime.model;

import java.util.ArrayList;
import java.util.Collections;

public class OdaGoogleSheet {
	
	private String sheetName;
	private int sheetIndex;
	private int sheetCountRow;
	private int sheetCountCol;
	private boolean firstRowRemoved;
	
	private ArrayList<OdaGoogleSheetColumn> sheetListCol;
	private ArrayList<OdaGoogleSheetRow> sheetListRow;
	
	
	public OdaGoogleSheet(String sheetName, int sheetIndex, int pCountCol, int pCountRow) {
		super();
		this.sheetName = sheetName;
		this.sheetIndex = sheetIndex;
		this.sheetCountCol = pCountCol;
		this.sheetCountRow = pCountRow;
		this.sheetListCol = new ArrayList<OdaGoogleSheetColumn>();
		this.sheetListRow = new ArrayList<OdaGoogleSheetRow>();
		firstRowRemoved = false;
	}
	
	
	public void sortColumnsToRemove(String[] listColsSelected) {
		
		ArrayList<OdaGoogleSheetColumn> listColToRemove = new ArrayList<OdaGoogleSheetColumn>();
		ArrayList<Integer> listCellToRemove = new ArrayList<Integer>();
		
		
	//Build an arraylist with all columns to remove
		boolean colFind;
		for(OdaGoogleSheetColumn col: sheetListCol){

			colFind = false;
			for(String colName : listColsSelected){
				
				if(colName.equals(col.getColName())){	
					colFind = true;
				}
			}
			
			if(colFind == false){
				listColToRemove.add(col);
			}
			
		}
		
		
	//Values
		listCellToRemove.clear();
		
		for(OdaGoogleSheetColumn col: listColToRemove){
			
			listCellToRemove.add(col.getColIndex());
		}
		
		Collections.sort(listCellToRemove, Collections.reverseOrder()); 

		for(OdaGoogleSheetRow row : sheetListRow){

			for(Integer indexCell : listCellToRemove){
				
				row.getRowListCell().remove(row.getRowListCell().get(indexCell));
			}
		
		}
		
		
	//Columns : Update Index
				
		for(OdaGoogleSheetColumn colRemove: listColToRemove){
			
			//get index
			int indexColRemoved = colRemove.getColIndex();
			
			//Decrement all next columns
			for(int i = indexColRemoved+1; i < sheetCountCol; i++){
				sheetListCol.get(i).decrementIndexCol();
			}
		}
		
		for(OdaGoogleSheetColumn colRemove: listColToRemove){
			//Remove from arrayList
			sheetListCol.remove(colRemove);
			sheetCountCol--;
		}
		

	}


	public String getSheetName() {
		return sheetName;
	}


	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}


	public int getSheetIndex() {
		return sheetIndex;
	}


	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}


	public ArrayList<OdaGoogleSheetColumn> getSheetListCol() {
		return sheetListCol;
	}


	public void setSheetListCol(ArrayList<OdaGoogleSheetColumn> sheetListCol) {
		this.sheetListCol = sheetListCol;
	}


	public int getSheetCountRow() {
		return sheetCountRow;
	}


	public void setSheetCountRow(int sheetCountRow) {
		this.sheetCountRow = sheetCountRow;
	}


	public int getSheetCountCol() {
		return sheetCountCol;
	}


	public void setSheetCountCol(int sheetCountCol) {
		this.sheetCountCol = sheetCountCol;
	}


	public ArrayList<OdaGoogleSheetRow> getSheetListRow() {
		return sheetListRow;
	}


	public void setSheetListRow(ArrayList<OdaGoogleSheetRow> sheetListRow) {
		this.sheetListRow = sheetListRow;
	}


	public boolean isFirstRowRemoved() {
		return firstRowRemoved;
	}


	public void setFirstRowRemoved(boolean firstRowRemoved) {
		this.firstRowRemoved = firstRowRemoved;
	}



	

}
