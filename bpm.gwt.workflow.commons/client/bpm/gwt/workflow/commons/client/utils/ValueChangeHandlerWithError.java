package bpm.gwt.workflow.commons.client.utils;

import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;

import com.google.gwt.event.logical.shared.ValueChangeHandler;

public abstract class ValueChangeHandlerWithError implements ValueChangeHandler<String> {

	private PropertiesText txt;

	public void setTxt(PropertiesText txt) {
		this.txt = txt;
	}

	public PropertiesText getText() {
		return txt;
	}
	
	public void setTxtError(String error) {
		txt.setTxtError(error);
	}
}
