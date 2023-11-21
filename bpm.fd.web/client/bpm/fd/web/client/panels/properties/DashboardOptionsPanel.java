package bpm.fd.web.client.panels.properties;

import bpm.fd.core.Dashboard;
import bpm.fd.web.client.I18N.Labels;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DashboardOptionsPanel extends Composite {

	private static MetadataOptionsPanelUiBinder uiBinder = GWT.create(MetadataOptionsPanelUiBinder.class);

	interface MetadataOptionsPanelUiBinder extends UiBinder<Widget, DashboardOptionsPanel> {
	}
	
	@UiField
	LabelTextBox txtName;
	
	@UiField
	LabelTextArea txtDescription;

	public DashboardOptionsPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		txtName.setText(Labels.lblCnst.MyDashboard());
		txtDescription.setText("");
	}

	public void loadItem(Dashboard item) {
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
