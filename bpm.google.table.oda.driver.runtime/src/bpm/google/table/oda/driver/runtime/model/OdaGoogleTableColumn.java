package bpm.google.table.oda.driver.runtime.model;

public class OdaGoogleTableColumn {
	
	private int colIndex;
	private String colName;
	private String colLabelName;
	private Class colClass;
	
	public OdaGoogleTableColumn(){
		
	}
	
	public OdaGoogleTableColumn(String name, String classCol, int index){
		
		colIndex = index;
		colName = name;
		colLabelName = name;
		
		if(classCol.equals(String.class.getSimpleName().toLowerCase())){
			colClass = String.class;
		}
		
		else{
			colClass = Number.class;
		}
		
		
		
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
