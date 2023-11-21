package bpm.architect.web.client.panels.fields;

import java.util.Arrays;

import bpm.architect.web.client.panels.FormFieldPanel;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.forms.FormField;
import bpm.vanilla.platform.core.beans.forms.FormField.TypeField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TextBoxPanel extends Composite implements FieldPanel {

	private static TextBoxPanelUiBinder uiBinder = GWT.create(TextBoxPanelUiBinder.class);

	interface TextBoxPanelUiBinder extends UiBinder<Widget, TextBoxPanel> {}

	@UiField
	LabelTextBox txtName, txtLabel, txtCol;
	
	@UiField
	CheckBox chkMandatory;
	
	@UiField
	ListBoxWithButton<TypeField> lstTypes;
	
	private FormField field;
	
	public TextBoxPanel(final FormField field) {
		initWidget(uiBinder.createAndBindUi(this));
		this.field = field;
		
		txtName.setText(field.getName());
		txtLabel.setText(field.getLabel());
		txtCol.setText(field.getColumnName());
		chkMandatory.setValue(field.isMandatory());
		
		lstTypes.setList(Arrays.asList(TypeField.values()));
		lstTypes.setSelectedObject(field.getType());
		lstTypes.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				FormFieldPanel.getInstance().changeFieldType(TextBoxPanel.this, lstTypes.getSelectedObject());
			}
		});
	}

	@Override
	public FormField getField() {
		field.setColumnName(txtCol.getText());
		field.setLabel(txtLabel.getText());
		field.setName(txtName.getText());
		field.setMandatory(chkMandatory.getValue());
		return field;
	}

	@UiHandler("imgDelete")
	public void onDelete(ClickEvent event) {
		FormFieldPanel.getInstance().deleteField(this);
	}
}
