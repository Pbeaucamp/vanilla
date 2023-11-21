package bpm.fa.api.olap.query;

import java.util.List;

public class WhereClauseException extends Exception{

	private List<String> whereUname;
	public WhereClauseException(String message, List<String> whereUname) {
		super(message);
		this.whereUname = whereUname;
	}
	
	public List<String> getWhereUname(){
		return whereUname;
	}
	

}
