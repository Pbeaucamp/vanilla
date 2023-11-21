package bpm.vanilla.portal.client.services;

import java.util.List;

import bpm.gwt.commons.shared.viewer.FormsDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PerFormsServiceAsync {
	
	/**
	 * submits a form
	 * @param form
	 * @param callback
	 */
	public void submit(FormsDTO form, AsyncCallback<String> callback);
	
	/**
	 * validates a form
	 * @param form
	 * @param callback
	 */
	public void validate(FormsDTO form, AsyncCallback<String> callback);
	
	/**
	 * lists forms for my group
	 * 
	 * @param callback
	 */
	public void getForms(AsyncCallback<List<FormsDTO>> callback);
}
