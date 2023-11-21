package bpm.workflow.runtime.model;

import java.util.List;

/**
 * Interface of the activities which cope with reports objects
 * @author CHARBONNIER, MARTIN
 *
 */
public interface IReport {
	/**
	 * Set the ouputname of the report
	 * @param outputName
	 */
	public void setOutputName(String outputName);
	
	/**
	 * 
	 * @return the outputname of the report
	 */
	public String getOutputName();
	
	/**
	 * Set the format of the generated report 
	 * @param i : int (0 = pdf...)
	 */
	public void setOutputFormats(List<Integer> i);
	
	/**
	 * 
	 * @return the format of the generated report
	 */
	public List<Integer> getOutputFormats();

	public void setOutputFormats(int[] selectionIndices);
	
	
}
