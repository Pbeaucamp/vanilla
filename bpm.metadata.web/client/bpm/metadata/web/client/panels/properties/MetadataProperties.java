package bpm.metadata.web.client.panels.properties;

import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.metadata.web.client.panels.MetadataOptionsPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MetadataProperties extends Composite implements IPanelProperties {

	private static MetadataPropertiesUiBinder uiBinder = GWT.create(MetadataPropertiesUiBinder.class);

	interface MetadataPropertiesUiBinder extends UiBinder<Widget, MetadataProperties> {
	}
	
	@UiField
	MetadataOptionsPanel optionPanel;

	private Metadata item;
	
	public MetadataProperties(Metadata item) {
		initWidget(uiBinder.createAndBindUi(this));
		this.item = item;
		
		optionPanel.loadItem(item);
	}

	@Override
	public void apply() {
		String name = optionPanel.getName();
		String description = optionPanel.getDescription();
		
		item.setName(name);
		item.setDescription(description);
	}

	@Override
	public boolean isValid() {
		String name = optionPanel.getName();
		return name != null && !name.isEmpty();
	}
}
