package bpm.inlinedatas.oda.driver.runtime.structureProperties;

public class PropertieData {
	
	private Object propertieDataValue;
	@SuppressWarnings("unchecked")
	private Class propertieDataType;
	private boolean isHidenAfterFilter;
	
	
	@SuppressWarnings("unchecked")
	public PropertieData(Object propertieDataValue, Class propertieDataType) {
		super();
		this.propertieDataValue = propertieDataValue;
		this.propertieDataType = propertieDataType;
		this.isHidenAfterFilter = false;
	}
	
	
	public Object getPropertieDataValue() {
		return propertieDataValue;
	}
	public void setPropertieDataValue(Object propertieDataValue) {
		this.propertieDataValue = propertieDataValue;
	}
	@SuppressWarnings("unchecked")
	public Class getPropertieDataType() {
		return propertieDataType;
	}
	@SuppressWarnings("unchecked")
	public void setPropertieDataType(Class propertieDataType) {
		this.propertieDataType = propertieDataType;
	}
	
	public boolean isHidenAfterFilter() {
		return isHidenAfterFilter;
	}


	public void setHidenAfterFilter(boolean isHidenAfterFilter) {
		this.isHidenAfterFilter = isHidenAfterFilter;
	}
	

}
