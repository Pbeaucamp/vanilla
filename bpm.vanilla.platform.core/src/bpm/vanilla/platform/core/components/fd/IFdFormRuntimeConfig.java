package bpm.vanilla.platform.core.components.fd;

import java.util.HashMap;

import bpm.vanilla.platform.core.components.IRuntimeConfig;

public interface IFdFormRuntimeConfig extends IRuntimeConfig{
		
	
	public String getSubmissionUrl();
	
	/**
	 * Hidden fields to be able to generate within the HTML form fields that will be
	 * used to identify a specific item from the submissionUrl
	 * 
	 * ex : if the form has been used in a Workflow, one of the hidden field will be
	 * the IRunIdentifier.getKey() generated when instanciating the Process 
	 * 
	 * @return
	 */
	public HashMap<String, String> getHiddenFields();
}
