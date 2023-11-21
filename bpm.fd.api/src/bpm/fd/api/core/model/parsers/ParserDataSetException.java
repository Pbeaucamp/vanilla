package bpm.fd.api.core.model.parsers;

import bpm.fd.api.core.model.datas.DataSet;

public class ParserDataSetException extends AbstractParserException {
	private DataSet dataSet;
	
	public ParserDataSetException(DataSet dataSet, String message, Throwable cause) {
		super(message, cause);
		this.dataSet = dataSet;
	}

	
	public Object getDatas(){
		return dataSet;
	}
}
