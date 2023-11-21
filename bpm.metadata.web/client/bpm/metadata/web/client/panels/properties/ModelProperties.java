package bpm.metadata.web.client.panels.properties;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ModelProperties extends Composite implements IPanelProperties {

	private static ModelPropertiesUiBinder uiBinder = GWT.create(ModelPropertiesUiBinder.class);

	interface ModelPropertiesUiBinder extends UiBinder<Widget, ModelProperties> {
	}
	
	@UiField
	LabelTextBox txtName;
	
	private MetadataModel model;

	public ModelProperties(MetadataModel model) {
		initWidget(uiBinder.createAndBindUi(this));
		this.model = model;
		
		txtName.setText(model.getName());
	}

	@Override
	public boolean isValid() {
		String modelName = txtName.getText();
		return modelName != null && !modelName.isEmpty();
	}

	@Override
	public void apply() {
		String modelName = txtName.getText();
		model.setName(modelName);
	}

}
