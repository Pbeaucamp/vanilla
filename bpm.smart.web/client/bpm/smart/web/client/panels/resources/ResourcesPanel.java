package bpm.smart.web.client.panels.resources;

import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.resources.CiblesPanel;
import bpm.gwt.workflow.commons.client.resources.ListOfValuesPanel;
import bpm.gwt.workflow.commons.client.resources.ParametersPanel;
import bpm.gwt.workflow.commons.client.resources.VariablesPanel;
import bpm.gwt.workflow.commons.client.tabs.MultipleTabPanel;
import bpm.smart.web.client.MainPanel;
import bpm.smart.web.client.panels.WorkflowDisplayPanel;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class ResourcesPanel extends Tab {

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

	public ResourcesPanel(MainPanel mainPanel, WorkflowDisplayPanel displayPanel) {
		super(displayPanel, LabelsCommon.lblCnst.resources_title(), false);
		collapsePanel = new CollapsePanel(250, 55);
		this.add(uiBinder.createAndBindUi(this));
		
		User user = mainPanel.getUserSession().getInfoUser().getUser();
		boolean isSuperUser = user.isSuperUser();
		
		MultipleTabPanel tabManager = new MultipleTabPanel(collapsePanel);
		if (isSuperUser) {
			tabManager.addTab(new AdminPanel(mainPanel), false);
		}
		AirPanel airPanel = new AirPanel(displayPanel.getLogPanel(), mainPanel, displayPanel.getResourceManager());
		tabManager.addTab(new DatasetManagerPanel(mainPanel, airPanel), !isSuperUser);
		tabManager.addTab(airPanel, true);
		tabManager.addTab(new VariablesPanel(displayPanel.getResourceManager()), false);
		tabManager.addTab(new ParametersPanel(displayPanel.getResourceManager()), false);
		tabManager.addTab(new ListOfValuesPanel(displayPanel.getResourceManager()), false);
		
		collapsePanel.setLeftPanel(tabManager);
		
		this.addStyleName(style.mainPanel());
	}

}
