package bpm.gateway.core.transformations.outputs;

import java.util.HashMap;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.MappingException;

/**
 * Common interface to allow the using of the same Mapping section for DataBaseOutput and
 * OlapOutput 
 * @author LCA
 *
 */
public interface IOutput extends Transformation{
	public void createMapping(Transformation input, int transfoColNum, int colNum) throws MappingException;
	public void deleteMapping(Transformation input, int transfoColNum, int colNum);
	public HashMap<String, String> getMappingsFor(Transformation t);
	public Integer getMappingValueForInputNum(Transformation t, int colNum);
	public Integer getMappingValueForThisNum(Transformation t, int colNum);
	public void clearMapping(Transformation t);
	public boolean isMapped(String colName);
	public boolean isMapped(Transformation t, String colName);
	
}
