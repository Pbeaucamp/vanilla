package bpm.gwt.commons.client.panels;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.forms.Form;
import bpm.vanilla.platform.core.beans.forms.FormField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FormFilterPanel extends Composite {

	private static FormFilterPanelUiBinder uiBinder = GWT.create(FormFilterPanelUiBinder.class);

	interface FormFilterPanelUiBinder extends UiBinder<Widget, FormFilterPanel> {}

	@UiField
	ListBoxWithButton<FormField> lstCol;
	
	@UiField
	LabelTextBox txtValue;
	
	public FormFilterPanel(Form form) {
		initWidget(uiBinder.createAndBindUi(this));
		
		lstCol.setList(form.getFields());
	}

	public void fillField() {
		lstCol.getSelectedObject().setValue(txtValue.getText());
	}
}
