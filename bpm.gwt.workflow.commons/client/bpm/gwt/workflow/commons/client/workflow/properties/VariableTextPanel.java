package bpm.gwt.workflow.commons.client.workflow.properties;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.workflow.commons.client.utils.ValueChangeHandlerWithError;
import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class VariableTextPanel extends Composite {

	private static VariableTextPanelUiBinder uiBinder = GWT.create(VariableTextPanelUiBinder.class);

	interface VariableTextPanelUiBinder extends UiBinder<Widget, VariableTextPanel> {
	}
	
	interface MyStyle extends CssResource {
		String error();
		String warning();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Label label;
	
	@UiField(provided=true)
	VariableTextHolderBox txtValue;
	
	@UiField
	Label txtError;

	public VariableTextPanel(String label, VariableString value, List<Variable> variables, List<Parameter> parameters) {
		this.txtValue = new VariableTextHolderBox(value, label, null, variables, parameters);
		initWidget(uiBinder.createAndBindUi(this));
		
		this.label.setText(label);
	}
	
	public void setLabel(String label) {
		this.label.setText(label);
		this.txtValue.setPlaceHolder(label);
	}
	
	public VariableString getVariableText() {
		return txtValue.getVariableString();
	}
	
	public void setText(String value) {
		txtValue.setText(value);
	}

	public void setTxtError(String error) {
		txtError.setText(error);

		txtError.removeStyleName(style.warning());
		txtError.addStyleName(style.error());
	}

	public void setTxtWarning(String error) {
		txtError.setText(error);

		txtError.addStyleName(style.warning());
		txtError.removeStyleName(style.error());
	}

	public void addValueChangeHandler(PropertiesText txt, ValueChangeHandlerWithError valueChangeHandler) {
		valueChangeHandler.setTxt(txt);
		txtValue.addValueChangeHandler(valueChangeHandler);
	}

	public void setEnabled(boolean enabled) {
		txtValue.setEnabled(enabled);
		txtError.setVisible(enabled);
//		if (imgValid != null) {
//			imgValid.setVisible(enabled);
//		}
	}

	public void setVisible(boolean visible) {
		txtValue.setVisible(visible);
		txtError.setVisible(visible);
//		if (imgValid != null) {
//			imgValid.setVisible(visible);
//		}
	}
	
	public void setPlaceHolder(String placeHolder) {
		txtValue.setPlaceHolder(placeHolder);
	}

	public String getText() {
		return txtValue.getText();
	}

	public int getTextAsInteger() {
		try {
			return Integer.parseInt(txtValue.getText());
		} catch (Exception e) {
			return 0;
		}
	}
}
