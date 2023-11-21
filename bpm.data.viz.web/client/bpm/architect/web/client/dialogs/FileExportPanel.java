package bpm.architect.web.client.dialogs;

import bpm.gwt.commons.client.custom.LabelTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FileExportPanel extends Composite {

	private static FileExportPanelUiBinder uiBinder = GWT.create(FileExportPanelUiBinder.class);

	interface FileExportPanelUiBinder extends UiBinder<Widget, FileExportPanel> {}

	@UiField
	LabelTextBox txtSeparator;
	
	public FileExportPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		txtSeparator.setText(",");
	}

	public String getSeparator() {
		return txtSeparator.getText();
	}
}
