package bpm.architect.web.client.panels;

import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.resources.DatabaseServersPanel;
import bpm.gwt.workflow.commons.client.tabs.MultipleTabPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class ResourcesPanel extends CompositeWaitPanel {

	private static ConsultPanelUiBinder uiBinder = GWT.create(ConsultPanelUiBinder.class);

	interface ConsultPanelUiBinder extends UiBinder<Widget, ResourcesPanel> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
	}
	
	@UiField
	MyStyle style;
	
	@UiField(provided=true)
	CollapsePanel collapsePanel;

	public ResourcesPanel(IResourceManager resourceManager) {
		collapsePanel = new CollapsePanel(250, 55);
		initWidget(uiBinder.createAndBindUi(this));
		
		MultipleTabPanel tabManager = new MultipleTabPanel(collapsePanel);
//		tabManager.addTab(new VariablesPanel(resourceManager), false);
//		tabManager.addTab(new ParametersPanel(resourceManager), false);
		tabManager.addTab(new DatabaseServersPanel(resourceManager), true);
		
		collapsePanel.setLeftPanel(tabManager);
		
		this.addStyleName(style.mainPanel());
	}

}
