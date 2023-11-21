package bpm.workflow.runtime.model;

import java.util.List;

/**
 * Interface of the activities which are conditionnable (loops..)
 * @author Charles MARTIN
 *
 */

public interface IConditionnable {

	/**
	 * 
	 * @return the variables of the activity
	 */
	public List<ActivityVariables> getVariables();
	
	public String getSuccessVariableSuffix();

}
