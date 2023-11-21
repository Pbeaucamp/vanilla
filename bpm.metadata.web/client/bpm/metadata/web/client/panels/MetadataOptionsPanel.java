package bpm.metadata.web.client.panels;

import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.metadata.web.client.I18N.Labels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MetadataOptionsPanel extends Composite {

	private static MetadataOptionsPanelUiBinder uiBinder = GWT.create(MetadataOptionsPanelUiBinder.class);

	interface MetadataOptionsPanelUiBinder extends UiBinder<Widget, MetadataOptionsPanel> {
	}
	
	@UiField
	LabelTextBox txtName;
	
	@UiField
	LabelTextArea txtDescription;

	public MetadataOptionsPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		txtName.setText(Labels.lblCnst.MyMetadata());
		txtDescription.setText("");
	}

	public void loadItem(Metadata item) {
		txtName.setText(item.getName());
		txtDescription.setText(item.getDescription());
	}

	public String getName() {
		return txtName.getText();
	}

	public String getDescription() {
		return txtDescription.getText();
	}
}
