package bpm.gwt.commons.client.viewer.widget;

import com.google.gwt.user.client.ui.RadioButton;

public class CustomRadioButton extends RadioButton{

	private String parameterValue;
	
	public CustomRadioButton(String name, String parameterValue) {
		super(name);
		this.parameterValue = parameterValue;
	}
	
	public String getParameterValue(){
		return parameterValue;
	}
}
