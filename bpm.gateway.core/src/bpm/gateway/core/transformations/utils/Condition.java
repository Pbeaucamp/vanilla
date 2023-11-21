package bpm.gateway.core.transformations.utils;

import java.sql.Types;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.Transformation;


public class Condition {
	public static final String[] OPERATORS = new String[]{"=", "!=", "<", ">", "<=", ">=", "CONTAINS", "STARTSWITH", "ENDSWITH", "IN", "IS NULL"};
	
	public static final String[] LOGICALS = new String[]{"NONE", "AND", "OR"}; 
	
	public static final int EQUAL = 0;
	public static final int DIFFERENT = 1;
	public static final int LESSER_THAN = 2;
	public static final int GREATER_THAN = 3;
	public static final int LESSER_EQ_THAN = 4;
	public static final int GREATER_EQ_THAN = 5;
	public static final int CONTAINS = 6;
	public static final int STARTSWIDTH = 7;
	public static final int ENDSWIDTH = 8;
	public static final int IN = 9;
	public static final int NULL = 10;
	
	public static final int NONE = 0;
	public static final int AND = 1;
	public static final int OR = 2;
	
	
	private String streamElementName;
	private String operator = OPERATORS[0];
	private String value = "";
	private Transformation output = null;
	private String transformationName ;
	private int logical = 0;

	/*
	 * For old model only, we use it for digester and then it stay to null
	 */
	private Integer streamElementNumber;
	
	public Transformation getOutput() {
		return output;
	}

	public void setLogical(int i){
		logical = i;
	}
	
	public void setLogical(String value){
		try{
			logical = Integer.parseInt(value);
		}catch(NumberFormatException e){
			
		}
		
	}
	
	public int getLogical(){
		return logical;
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
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
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
		
		e.addElement("logical").setText(logical + "");	
		
	
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

	public String getValue(int colType) {
		if(colType == Types.BIGINT || colType == Types.DECIMAL || colType == Types.DOUBLE || colType == Types.FLOAT 
				|| colType == Types.INTEGER || colType == Types.NUMERIC || colType == Types.SMALLINT || colType == Types.TINYINT) {
			return value;
		}
			
		return "'" + value + "'";
	}
}
