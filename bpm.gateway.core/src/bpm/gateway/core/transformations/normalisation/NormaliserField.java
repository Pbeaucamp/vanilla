package bpm.gateway.core.transformations.normalisation;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class NormaliserField {
	private String fieldName;
	private Integer inputFieldValueIndex;
	private String value = "";
	private Denormalisation owner;
	
	
	public void setOwner(Denormalisation owner){
		this.owner = owner;
	}
	
	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
		if (owner != null){
			owner.refreshDescriptor();
		}
		
		
	}
	/**
	 * @return the inputFieldValueIndex
	 */
	public Integer getInputFieldValueIndex() {
		return inputFieldValueIndex;
	}
	/**
	 * @param inputFieldValueIndex the inputFieldValueIndex to set
	 */
	public void setInputFieldValueIndex(Object inputFieldValueIndex) {
		if (inputFieldValueIndex == null){
			this.inputFieldValueIndex = null;
		}
		
		if (inputFieldValueIndex instanceof Integer){
			this.inputFieldValueIndex = (Integer)inputFieldValueIndex;
			
			
			
		}
		else if (inputFieldValueIndex instanceof String){
			try{
				this.inputFieldValueIndex = Integer.parseInt((String)inputFieldValueIndex);
			}catch(NumberFormatException e){
				
			}
		}
		
		
		if (this.inputFieldValueIndex != null){
			try{
				int i = owner.getFields().indexOf(this);
				owner.getDescriptor(null).getStreamElements().get(i).className = owner.getInputs().get(0).getDescriptor(null).getJavaClass(this.inputFieldValueIndex);
			}catch(Exception e){
				
			}
			
		}
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	public Element getElement() {
		Element e = DocumentHelper.createElement("normalisationField");
		e.addElement("fieldName").setText(getFieldName());
		if (inputFieldValueIndex != null){
			e.addElement("inputFieldValueIndex").setText(inputFieldValueIndex + "");
		}
		e.addElement("value").setText(value);
		
		
		return e;
	}
	
	
}
