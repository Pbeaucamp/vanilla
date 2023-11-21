package bpm.gwt.commons.client.viewer.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTMLPanel;

import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

public class CustomRadioButtonGroupPanel extends HTMLPanel {
	
	private static final String CSS_GROUP_RADIO = "radioGroupButton";

	private VanillaParameter parameter;

	public CustomRadioButtonGroupPanel(VanillaParameter parameter) {
		super("");
		this.parameter = parameter;
		
		this.addStyleName(CSS_GROUP_RADIO);
	}
	
	public void addRadioButton(CustomRadioButton radioButton, boolean first){
		radioButton.addValueChangeHandler(radioBtnHandler);
		radioButton.setValue(first, true);
		
		HTMLPanel panel = new HTMLPanel("");
		panel.add(radioButton);
		
		this.add(panel);
	}
	
	private ValueChangeHandler<Boolean> radioBtnHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			if(event.getValue()){
				CustomRadioButton source = (CustomRadioButton)event.getSource();
				
				List<String> selectedValues = new ArrayList<String>();
				selectedValues.add(source.getParameterValue());
				
				parameter.setSelectedValues(selectedValues);
			}
		}
	};
}
