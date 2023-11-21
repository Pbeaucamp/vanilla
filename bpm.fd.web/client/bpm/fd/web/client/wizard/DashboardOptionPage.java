package bpm.fd.web.client.wizard;

import bpm.fd.web.client.panels.properties.DashboardOptionsPanel;
import bpm.gwt.commons.client.wizard.IGwtPage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DashboardOptionPage extends Composite implements IGwtPage {

	private static MetadataOptionPageUiBinder uiBinder = GWT.create(MetadataOptionPageUiBinder.class);

	interface MetadataOptionPageUiBinder extends UiBinder<Widget, DashboardOptionPage> {
	}

	@UiField
	SimplePanel panelContent;
	
	private DashboardOptionsPanel optionsPanel;

	public DashboardOptionPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		optionsPanel = new DashboardOptionsPanel();
		panelContent.setWidget(optionsPanel);
	}

	@Override
	public boolean canGoBack() {
		return false;
	}

	@Override
	public boolean isComplete() {
		String name = getName();
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
		return optionsPanel.getName();
	}
	
	public String getDescription() {
		return optionsPanel.getDescription();
	}

}
