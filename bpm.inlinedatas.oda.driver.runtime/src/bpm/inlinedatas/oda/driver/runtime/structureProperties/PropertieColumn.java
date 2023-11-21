package bpm.inlinedatas.oda.driver.runtime.structureProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PropertieColumn {
	
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
	
	
	
	
	private String propertieColName;
	@SuppressWarnings("unchecked")
	private Class propertieColType;
	private ArrayList<PropertieData> listPropertieData;
	
	public PropertieColumn(String propertieColName, String pType) {
		super();
		this.propertieColName = propertieColName;
		this.listPropertieData = new ArrayList<PropertieData>();
		
		
//Convert the String Parametre into a class
		
	//--For a string	
		if (pType.equals(TYPES_NAMES[INDEX_STRING].getSimpleName())){
			propertieColType = String.class;
		}
		
	//--For an Integer	
		if (pType.equals(TYPES_NAMES[INDEX_INTEGER].getSimpleName())){
			propertieColType = Integer.class;
		}
		
	//--For a long	
		if (pType.equals(TYPES_NAMES[INDEX_LONG].getSimpleName())){
			propertieColType = Long.class;
		}
		
	//--For a float	
		if (pType.equals(TYPES_NAMES[INDEX_FLOAT].getSimpleName())){
			propertieColType = Float.class;
		}
		
		
	//--For a double	
		if (pType.equals(TYPES_NAMES[INDEX_DOUBLE].getSimpleName())){
			propertieColType = Double.class;
		}
		
	//--For a boolean	
		if (pType.equals(TYPES_NAMES[INDEX_BOOLEAN].getSimpleName())){
			propertieColType = Boolean.class;
		}
		
	//--For a char	
		if (pType.equals(TYPES_NAMES[INDEX_CHAR].getSimpleName())){
			propertieColType = Character.class;
		}
		
	//--For a date
		if (pType.equals(TYPES_NAMES[INDEX_DATE].getSimpleName())){
			propertieColType = Date.class;
		}
	
		
		
	}
	
//Method to add a new data into the column
	
	public void addNewData(String strValue){
		
		//Convert the string into the good class
		
		Object tempValue = null;
		
		if(propertieColType == TYPES_NAMES[INDEX_BOOLEAN]){
			tempValue = Boolean.valueOf(strValue);
		}
		
		else if(propertieColType == TYPES_NAMES[INDEX_INTEGER]){
			tempValue = Integer.valueOf(strValue);
		}
		
		else if(propertieColType == TYPES_NAMES[INDEX_LONG]){
			tempValue = Long.valueOf(strValue);
		}
		
		else if(propertieColType == TYPES_NAMES[INDEX_FLOAT]){
			tempValue = Float.valueOf(strValue);
		}
		
		else if(propertieColType == TYPES_NAMES[INDEX_DOUBLE]){
			tempValue = Double.valueOf(strValue);
		}
		
		else if(propertieColType == TYPES_NAMES[INDEX_CHAR]){
			tempValue = (strValue.charAt(0));
		}
		else if(propertieColType == TYPES_NAMES[INDEX_STRING]){
			tempValue = String.valueOf(strValue);
		}
		
		else{
			tempValue = extractDate(strValue);
		}
		
		
		this.listPropertieData.add(new PropertieData(tempValue,this.propertieColType));
		
	}
	
	private Date extractDate(String date){
	
		
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy");
		
		Date d = null;
		
		try {
			d = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return d;
	}

	public String getPropertieColName() {
		return propertieColName;
	}

	public void setPropertieColName(String propertieColName) {
		this.propertieColName = propertieColName;
	}

	@SuppressWarnings("unchecked")
	public Class getPropertieColType() {
		return propertieColType;
	}

	@SuppressWarnings("unchecked")
	public void setPropertieColType(Class propertieColType) {
		this.propertieColType = propertieColType;
	}

	public ArrayList<PropertieData> getListPropertieData() {
		return listPropertieData;
	}

	public void setListPropertieData(ArrayList<PropertieData> plistPropertieData) {
		listPropertieData = plistPropertieData;
	}


	
	

}
