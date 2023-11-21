package bpm.es.datasource.analyzer.parsers;

import java.util.List;

import bpm.vanilla.platform.core.beans.data.OdaInput;


public interface IAnalyzer {

	public boolean match(String xml, IPattern pattern) throws Exception;
	
	public String getObjectTypeName();
	
	public int getObjectType();
	
	/**
	 * extract the Odainput for each encoutered datasets DataSets 
	 * @param xml
	 * @return
	 * @throws Exception 
	 */
	public List<OdaInput> extractDataSets(String xml) throws Exception;
	
}
