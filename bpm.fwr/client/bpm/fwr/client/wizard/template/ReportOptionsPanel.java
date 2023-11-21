package bpm.fwr.client.wizard.template;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.client.Bpm_fwr;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ReportOptionsPanel extends Composite {

	private static ReportOptionsPanelUiBinder uiBinder = GWT.create(ReportOptionsPanelUiBinder.class);

	interface ReportOptionsPanelUiBinder extends UiBinder<Widget, ReportOptionsPanel> {
	}
	
	@UiField
	LabelTextBox txtName;
	
	@UiField
	LabelTextArea txtDescription;

	public ReportOptionsPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		txtName.setText(Bpm_fwr.LBLW.MyReport());
		txtDescription.setText("");
	}

	public void loadItem(FWRReport item) {
		txtName.setText(item.getName());
		txtDescription.setText(item.getDescription());
	}
	
	public void loadOptions(String name, String description) {
		txtName.setText(name);
		txtDescription.setText(description);
	}

	public String getName() {
		return txtName.getText();
	}

	public String getDescription() {
		return txtDescription.getText();
	}

}
