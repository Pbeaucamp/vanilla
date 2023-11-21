package bpm.gateway.core.transformations.utils;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.Transformation;

public class ConditionNull {
	public static final String[] OPERATORS = new String[]{"IS NULL"};

	public static final int NULL = 10;

	private String streamElementName;
	private String operator = OPERATORS[0];
	private String value ;
	private Transformation output = null;
	private String transformationName ;

	/*
	 * For old model only, we use it for digester and then it stay to null
	 */
	private Integer streamElementNumber;
	
	public Transformation getOutput() {
		return output;
	}


	
	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}



	public void setOutput(Transformation output) {
		this.output = output;
	}
	
	public void setOutput(String outputRef) {
		transformationName = outputRef;
	}

	public String getStreamElementName() {
		return streamElementName;
	}
	
	public void setStreamElementName(String streamElementName) {
		this.streamElementName = streamElementName;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}



	public String getOutputRef() {
		return transformationName;
	}

	public int getOperatorConstant(){
		for(int i = 0; i < OPERATORS.length; i++){
			if (OPERATORS[i].equals(operator)){
				return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @return the condition as a String
	 */
	public String dump() {
		return "{ {$VALUE} " + OPERATORS[getOperatorConstant()] + " " + value + "}";
	}
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("condition");
		e.addElement("elementName").setText(streamElementName);
		e.addElement("operator").setText(operator);
		e.addElement("value").setText(value);

		if (output != null){
			e.addElement("outputRef").setText(output.getName());
		}
		
		return e;
	}
	
	/**
	 * For digester use only
	 * 
	 * @param streamElementNumber
	 */
	public void setStreamElementNumber(String streamElementNumber) {
		try{
			this.streamElementNumber = Integer.parseInt(streamElementNumber);
		}catch(NumberFormatException e){ }
	}
	
	/**
	 * Only used by the filter refreshDescriptor method
	 * @return the streamElementNumber if it is an old model, null otherwise
	 */
	public Integer getStreamElementNum(){
		return streamElementNumber;
	}
	
	/**
	 * Only used by the filter refreshDescriptor method
	 */
	public void setStreamElementNumberToNull(){
		this.streamElementNumber = null;
	}
}
