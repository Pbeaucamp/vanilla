package bpm.metadata.web.client.wizard;

import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.metadata.web.client.panels.MetadataOptionsPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MetadataOptionPage extends Composite implements IGwtPage {

	private static MetadataOptionPageUiBinder uiBinder = GWT.create(MetadataOptionPageUiBinder.class);

	interface MetadataOptionPageUiBinder extends UiBinder<Widget, MetadataOptionPage> {
	}

	@UiField
	SimplePanel panelContent;
	
	private MetadataOptionsPanel optionsPanel;

	public MetadataOptionPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		optionsPanel = new MetadataOptionsPanel();
		panelContent.setWidget(optionsPanel);
	}

	@Override
	public boolean canGoBack() {
		return false;
	}

	@Override
	public boolean isComplete() {
		String name = optionsPanel.getName();
		return name != null && !name.isEmpty();
	}

	@Override
	public boolean canGoFurther() {
		return isComplete();
	}

	@Override
	public int getIndex() {
		return 0;
	}

	public String getName() {
		String name = optionsPanel.getName();
		return name;
	}
	
	public String getDescription() {
		String description = optionsPanel.getDescription();
		return description;
	}

}
