package bpm.workflow.runtime.model;

import java.util.List;

import bpm.vanilla.platform.core.IRepositoryApi;

public interface ICanFillVariable {
	
	public void addParameterMapping(String form, String biObject);
	
	/**
	 * Remove the mapping between the form field and the other object parameter
	 * @param form
	 * @param biObject
	 */
	public void removeParameterMapping(String form, String biObject);

	
	public List<String> getParameters(IRepositoryApi sock);

}
