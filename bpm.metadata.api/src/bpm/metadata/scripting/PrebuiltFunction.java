package bpm.metadata.scripting;

public enum PrebuiltFunction {
	Group_Name(0, VariableType.String, "Return the Vanilla Group used to run a query"), 
	Group_Value(1, VariableType.String, "Return the Vanilla Group Custom Field for the given Group name"),
	
	User_Id(0, VariableType.Integer, "Return the user id running the query"),
	User_Function(0, VariableType.String, "Return the user function running the query"),
	User_SkypeName(0, VariableType.String, "Return the user skype name running the query");
	
	private final int numberOfParameter;
	private final VariableType returnType;
	private final String description;
	private final String template;
	
	PrebuiltFunction(int numberOfParameter, VariableType returnType, String description){
		this.numberOfParameter = numberOfParameter;
		this.description = description;
		this.returnType = returnType;
		
		StringBuffer buf = new StringBuffer();
		buf.append(name() + "(");
		for(int i = 0; i < this.numberOfParameter; i++){
			if ( i != 0){
				buf.append(", ");
			}
			buf.append("?");
		}
		buf.append(")");
		
		this.template = buf.toString();
	}


	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @return the numberOfParameter
	 */
	public int getNumberOfParameter() {
		return numberOfParameter;
	}


	/**
	 * @return the returnType
	 */
	public VariableType getReturnType() {
		return returnType;
	}


	public String getTemplate() {
		return template;
	}


	
	
}
