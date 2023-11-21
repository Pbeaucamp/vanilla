package bpm.gwt.workflow.commons.client.workflow.properties;

import bpm.gwt.workflow.commons.client.utils.VariableTextAreaHolderBox;
import bpm.vanilla.platform.core.beans.resources.VariableString;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class VariablePropertiesAreaText extends PropertiesAreaText {

	public VariablePropertiesAreaText(VariableTextAreaHolderBox txtValue, Label txtError, Image imgValid, String styleSuccess, String styleError) {
		super(txtValue, txtError, imgValid, styleSuccess, styleError);
	}

	public VariableString getVariableText() {
		return ((VariableTextAreaHolderBox) txtValue).getVariableString();
	}
	
	public void setVariableText(String value) {
		((VariableTextAreaHolderBox) txtValue).setText(value);
	}
}