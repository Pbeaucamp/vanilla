package bpm.architect.web.client.dialogs;

import bpm.gwt.commons.client.custom.LabelTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DatasetExportPanel extends Composite {

	private static DatasetExportPanelUiBinder uiBinder = GWT.create(DatasetExportPanelUiBinder.class);

	interface DatasetExportPanelUiBinder extends UiBinder<Widget, DatasetExportPanel> {}

	@UiField
	LabelTextBox txtDataset;
	
	public DatasetExportPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public String getDatasetName() {
		return txtDataset.getText();
	}
}
