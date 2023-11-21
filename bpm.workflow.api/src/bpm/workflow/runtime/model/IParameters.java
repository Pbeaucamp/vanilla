package bpm.workflow.runtime.model;

import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.IRepositoryApi;

/**
 * Interface of the activities with parameters
 * @author CHARBONNIER, MARTIN
 *
 */
public interface IParameters {
	/**
	 * 
	 * @return the parameters Names
	 */
	public List<String> getParameters(IRepositoryApi sock);
	
	/**
	 * Add a mapping between the form and the report
	 * @param activityParamName
	 * @param formParamName
	 */
	public void addParameterLink(String activityParamName, String formParamName);

	public HashMap<String, String> getMappings();

	public void removeParameterLink(String act, String orb);

}
