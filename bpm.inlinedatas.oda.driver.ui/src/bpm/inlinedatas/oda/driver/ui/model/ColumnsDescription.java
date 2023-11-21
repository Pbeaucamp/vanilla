package bpm.inlinedatas.oda.driver.ui.model;

import java.util.ArrayList;
import java.util.Date;

public class ColumnsDescription {
	

	@SuppressWarnings("unchecked")
	public static final Class[] TYPES_NAMES =  {String.class, Integer.class, Long.class, Float.class,  Double.class, Boolean.class, Character.class, Date.class};
		
	public static final int INDEX_STRING = 0;
	public static final int INDEX_INTEGER = 1;
	public static final int INDEX_LONG = 2;
	public static final int INDEX_FLOAT = 3;
	public static final int INDEX_DOUBLE = 4;
	public static final int INDEX_BOOLEAN = 5;
	public static final int INDEX_CHAR = 6;
	public static final int INDEX_DATE = 7;
	
	
//----- DataSource Properties
	public static final String PROPERTIES_COUNT_COL = "P_COLUMN_NUMBER";
	public static final String PROPERTIES_NAME_COL = "P_COLUMN_NAMES";
	public static final String PROPERTIES_VALUES_COL = "P_COLUMN_VALUES";
	public static final String PROPERTIES_TYPE_COL = "P_COLUMN_TYPE";
		
	
	private String colName;
	private int indexCol;
	@SuppressWarnings("unchecked")
	private Class colType;
		
	
	@SuppressWarnings("unchecked")
	public ColumnsDescription(String pName, Class pType, int pPosition) {
			this.colName = pName;
			this.indexCol = pPosition;
			this.colType = pType;
			
		}
	
	
//------- Class method to update Position if user delete a column
	
	public static void removeColumn(ArrayList<ColumnsDescription> list, int indexColSupress){
		
		list.remove(indexColSupress);
		
		for (int i = indexColSupress; i< list.size(); i++){
			
			list.get(i).setIndexCol(i);
			
			
		}
		
	}
	

		
	public String getColName() {
			return colName;
		}

		
	public void setColName(String colName) {
			this.colName = colName;
		}

	


	public int getIndexCol() {
		return indexCol;
	}


	public void setIndexCol(int indexCol) {
		this.indexCol = indexCol;
	}


	@SuppressWarnings("unchecked")
	public Class getColType() {
		return colType;
	}


	@SuppressWarnings("unchecked")
	public void setColType(Class colType) {
		this.colType = colType;
	}




		
		
	}

