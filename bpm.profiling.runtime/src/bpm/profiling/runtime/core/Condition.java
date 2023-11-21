package bpm.profiling.runtime.core;



public class Condition {
	public static final String[] operators = {"=", "!=", "IS NULL", "IN", "<" , "<=" , ">" , ">=", "LIKE", "BETWEEN"};
	
	public static final int EQUAL = 0;
	public static final int DIF = 1;
	public static final int NULL = 2;
	public static final int IN = 3;
	public static final int LESS = 4;
	public static final int LESS_E = 5;
	public static final int GREAT = 6;
	public static final int GREAT_E = 7;
	public static final int LIKE = 8;

	public static final int BETWEEN = 9;
	
	
	
	private String value1 = "", value2 = "";
	private int operator;
		
	private int id;
	private int ruleSetId;
	
	public Condition() {
		
	}
	
	
	
	
	public int getOperator(){
		return operator;
	}
	
	public void setOperator(int op){
		this.operator = op;
	}
	
	public String getValue1(){
		return value1;
	}
	
	public void setValue1(String value){
		 value1 = value;
	}
	
	public String getValue2(){
		return value2;
	}
	
	public void setValue2(String value){
		 value2 = value;
	}




	public int getId() {
		return id;
	}




	public void setId(int id) {
		this.id = id;
	}




	public int getRuleSetId() {
		return ruleSetId;
	}




	public void setRuleSetId(int ruleSetId) {
		this.ruleSetId = ruleSetId;
	}




	public String getSql(boolean isString) {
		StringBuffer buf = new StringBuffer();
		
		buf.append(" where $column " + operators[operator]);
		
		switch(operator){
		case DIF:
		case EQUAL:
		case GREAT:
		case GREAT_E:
		case LESS:
		case LESS_E:
			
			if (isString){
				buf.append("'" + value1 + "'");
			}
			else{
				buf.append(value1);
			}
			
			break;
		case NULL:
			break;
			
		case BETWEEN:
			
			if (isString){
				buf.append("('" + value1 + "' AND '" +value2 + "')");
			}
			else{
				buf.append("(" + value1 + " AND " +value2 + ")");
			}
			
		case IN:
			if (isString){
				buf.append("('" + value1 + "', '" +value2 + "')");
			}
			else{
				buf.append("(" + value1 + ", " +value2 + ")");
			}
			
			break;
		case LIKE:
			if (isString){
				buf.append("'" + value1 + "'");
			}
			else{
				buf.append(value1);
			}
			break;
		}
		
		
		return buf.toString();
	}
	
	
	}
