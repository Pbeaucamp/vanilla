package bpm.inlinedatas.oda.driver.ui.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CellDescription {
	
	private Object valueCell;
	@SuppressWarnings("unchecked")
	private Class typeCell;
	private int numCol;
	private boolean valueValid, valueModified;
	
	@SuppressWarnings("unchecked")
	public CellDescription(Class pTypeCell, int pNumCol) {
		super();
		this.typeCell = pTypeCell;
		numCol = pNumCol;
		valueCell =  null;
		valueModified = false;
		valueValid = false;
	}
	
	
	public Object getValueCell() {
		return valueCell;
	}
	
	@SuppressWarnings("unchecked")
	public void setValueCell(String pValue) {
		
		Class[] tabClass = ColumnsDescription.TYPES_NAMES;
		
		if(typeCell == tabClass[ColumnsDescription.INDEX_BOOLEAN]){
			valueCell = Boolean.valueOf(pValue);		
		}

		else if (typeCell == tabClass[ColumnsDescription.INDEX_CHAR]){
			valueCell = Character.valueOf(pValue.charAt(0));	
		}
		
		else if (typeCell == tabClass[ColumnsDescription.INDEX_DOUBLE]){
			valueCell = Double.valueOf(pValue);

		}
		
		else if (typeCell == tabClass[ColumnsDescription.INDEX_FLOAT]){
			valueCell = Float.valueOf(pValue);
		}
		
		else if (typeCell == tabClass[ColumnsDescription.INDEX_INTEGER]){
			valueCell = Integer.valueOf(pValue);	
		}
		
		else if (typeCell == tabClass[ColumnsDescription.INDEX_LONG]){
			valueCell = Long.valueOf(pValue);			
		}
		
		else if (typeCell == tabClass[ColumnsDescription.INDEX_STRING]){
			valueCell = (String) pValue;
		}
		
		else if (typeCell == tabClass[ColumnsDescription.INDEX_DATE]){
			valueCell = extractDate(pValue);
		}
		
	}
	
	private Date extractDate(String date){
		
		//SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy");
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		Date d = null;
		try {
			d = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return d;
	}

	


	public int getNumCol() {
		return numCol;
	}


	public void setNumCol(int numCol) {
		this.numCol = numCol;
	}


	@SuppressWarnings("unchecked")
	public Class getTypeCell() {
		return typeCell;
	}


	public boolean isValueValid() {
		return valueValid;
	}


	public void setValueValid(boolean valueValid) {
		this.valueValid = valueValid;
	}


	public boolean isValueModified() {
		return valueModified;
	}


	public void setValueModified(boolean valueModified) {
		this.valueModified = valueModified;
	}
	
	
}
