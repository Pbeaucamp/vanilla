package bpm.fwr.client.wizard.template;

import bpm.gwt.commons.client.wizard.IGwtPage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportOptionPage extends Composite implements IGwtPage {

	private static MetadataOptionPageUiBinder uiBinder = GWT.create(MetadataOptionPageUiBinder.class);

	interface MetadataOptionPageUiBinder extends UiBinder<Widget, ReportOptionPage> {
	}

	@UiField
	SimplePanel panelContent;
	
	private ReportOptionsPanel optionsPanel;

	public ReportOptionPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		optionsPanel = new ReportOptionsPanel();
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
