package bpm.google.spreadsheet.oda.driver.runtime.model;


public class OdaGoogleSheetColumn {
	
	private int colIndex;
	private int colIndexSheet;
	private String colName;
	private String colLabelName;
	private Class colClass;
	
	
	public OdaGoogleSheetColumn( int pIndexCol,
			int pIndexSheetCol, String pColName) {
		super();
		this.colIndex = pIndexCol;
		this.colIndexSheet = pIndexSheetCol;
		this.colName = pColName;
		colClass = null;
	}
	
	public void decrementIndexCol(){
		
		colIndex = colIndex-1;
	}



	public int getColIndex() {
		return colIndex;
	}


	public void setColIndex(int colIndex) {
		this.colIndex = colIndex;
	}


	public int getColIndexSheet() {
		return colIndexSheet;
	}


	public void setColIndexSheet(int colIndexSheet) {
		this.colIndexSheet = colIndexSheet;
	}


	public String getColName() {
		return colName;
	}


	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getColLabelName() {
		return colLabelName;
	}

	public void setColLabelName(String colLabelName) {
		this.colLabelName = colLabelName;
	}

	public Class getColClass() {
		return colClass;
	}

	public void setColClass(Class colClass) {
		this.colClass = colClass;
	}

	
	
}
