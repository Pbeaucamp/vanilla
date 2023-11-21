package bpm.gwt.commons.client.viewer.widget;

import com.google.gwt.dom.client.Style.Clear;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

public class CustomComboParameter extends HTMLPanel implements IListParameter {
	private VanillaParameter param;
	
	private CustomListBoxWithWait listBox;
	private TextBox txtNewValue;
	private RadioButton radioList, radioText;
	
	public CustomComboParameter(VanillaParameter param, CustomListBoxWithWait listBox, TextBox txtNewValue) {
		super("");
		this.param = param;
		this.listBox = listBox;
		this.txtNewValue = txtNewValue;
		
		this.radioList = new RadioButton(param.getName());
		this.radioText = new RadioButton(param.getName());
		
		addRadioButton(radioList, true, listBox);
		addRadioButton(radioText, false, txtNewValue);
		
		this.getElement().getStyle().setClear(Clear.BOTH);
	}
	
	public void addRadioButton(RadioButton radioButton, boolean first, Widget widget){
		radioButton.addValueChangeHandler(radioBtnHandler);
		radioButton.setValue(first, true);

		HorizontalPanel panel = new HorizontalPanel();
		panel.add(radioButton);
		panel.add(widget);
		
		this.add(panel);
	}

	public VanillaParameter getParam() {
		return param;
	}

	public ListBox getListBox() {
		return listBox.getListBox();
	}
	
	public String getName(){
		return listBox.getName();
	}

	@Override
	public Widget getWidget() {
		return this;
	}

	@Override
	public CustomListBoxWithWait getCustomListBox() {
		return listBox;
	}
	
	private ValueChangeHandler<Boolean> radioBtnHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			if(event.getValue()){
				RadioButton source = (RadioButton)event.getSource();
				
				if(source.equals(radioText)) {
					txtNewValue.setEnabled(true);
					listBox.getListBox().setEnabled(false);
				}
				else {
					txtNewValue.setEnabled(false);
					listBox.getListBox().setEnabled(true);
				}
//				List<String> selectedValues = new ArrayList<String>();
//				selectedValues.add(source.getParameterValue());
//				
//				parameter.setSelectedValues(selectedValues);
			}
		}
	};
}
