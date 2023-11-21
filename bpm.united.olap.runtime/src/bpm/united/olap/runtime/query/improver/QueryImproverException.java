package bpm.united.olap.runtime.query.improver;

import bpm.united.olap.api.runtime.DataCellIdentifier2;

public class QueryImproverException extends Exception{

	private DataCellIdentifier2 identifier;
	public QueryImproverException(String message, DataCellIdentifier2 identifier) {
		super();
		this.identifier = identifier;
	}
	public QueryImproverException() {
		super();
		
	}

	public QueryImproverException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		
	}

	public QueryImproverException(String arg0) {
		super(arg0);
		
	}

	public QueryImproverException(Throwable arg0) {
		super(arg0);
		
	}
	
	public DataCellIdentifier2 getDataCellIdentifierFailure(){
		return identifier;
	}

}
