package bpm.vanillahub.web.client.properties.parameters;

import bpm.gwt.workflow.commons.client.workflow.properties.parameters.HasParameterWidget;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceParameter;

import com.google.gwt.user.client.ui.CheckBox;

public class CheckBoxParameter extends CheckBox implements HasParameterWidget {

	public CheckBoxParameter(WebServiceParameter param) {
		super(param.getName());
		setTitle(param.getName());

		if (param.getParameterValue() instanceof Boolean) {
			setValue((Boolean) param.getParameterValue());
		}
	}

	@Override
	public Boolean getParameterValue() {
		return getValue();
	}

}
