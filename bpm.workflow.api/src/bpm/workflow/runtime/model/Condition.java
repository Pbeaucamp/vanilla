package bpm.workflow.runtime.model;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;

/**
 * Condition of a transition
 * @author CHARBONNIER,MARTIN
 *
 */
public class Condition {
	public static final String[] OPERATORS = new String[]{"=", "!=", "<", ">", "<=", ">="};
	
	private String variable;
	private String operator;
	private String value;
	
	public Condition() {
		
	}
	
	/**
	 * Create a condition 
	 * @param var : the variable for the condition
	 */
	public Condition(Variable var) {
		variable = var.getName();
	}
	
	/**
	 * 
	 * @return the definition of the condition
	 */
	public String getLabelCondition() {
		String condition = "";
		
		if(ListVariable.getInstance().getVariable(variable) != null){
			Variable v = ListVariable.getInstance().getVariable(variable);
			String op = "";
			if (operator.equals(OPERATORS[0])) {
				op = "==";
			}
			else {
				op = operator;
			}
			
			switch (v.getType()) {
			case Variable.INTEGER:
				condition = v.getId() + " " + op + " " + value;
				break;
				
			case Variable.STRING:
				try {
					condition = v.getId() + ".compareTo(" + Integer.parseInt(value) + ") " + op + " 0";
				}
				catch (Exception e) {
					condition = v.getId() + ".compareTo(\"" + value + "\") " + op + " 0";
				}
				break;
			
			case Variable.DATE:
				condition = v.getId() + ".compareTo(org.ow2.bonita.util.DateUtil.ISO_8601_FORMAT.parse(\"" + value + "\") " + op + " 0";
				break;
			default:
				break;
			}
			
			
			return condition;
		}
		else{
			String op = "";
			if (operator.equals(OPERATORS[0])) {
				op = "==";
			}
			else {
				op = operator;
			}
			condition = variable + " " + op + " " + value;
			
			return condition;
		}
	}

	/**
	 * 
	 * @return the variable of the condition
	 */
	public String getVariable() {
		return variable;
	}

	/**
	 * Set the variable of the condition
	 * @param variable
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}

	/**
	 * 
	 * @return the operator of the condition (=, <= ...)
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * Set the operator of the condition
	 * @param operator
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * 
	 * @return the value of the condition
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value of the condition
	 * @param value : true(-> 1) or false(-> -1) or something else
	 */
	public void setValue(String value) {

		this.value = value;
		
	}

	public Element getXmlNode() {
		Element e = DocumentHelper.createElement("condition");
		try {
			e.addElement("value").setText(value);
			e.addElement("variable").setText(variable);
			e.addElement("operator").setText(operator);
		} catch(Exception e1) {

		}
		
		return e;
	}
	
	
}
