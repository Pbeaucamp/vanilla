package bpm.gwt.workflow.commons.client.workflow.properties;

import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.vanilla.platform.core.beans.resources.VariableString;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class VariablePropertiesText extends PropertiesText {

	public VariablePropertiesText(VariableTextHolderBox txtValue, Label txtError, Image imgValid, String styleWarning, String styleError) {
		super(txtValue, txtError, imgValid, styleWarning, styleError);
	}

	public VariableString getVariableText() {
		return ((VariableTextHolderBox) txtValue).getVariableString();
	}
}