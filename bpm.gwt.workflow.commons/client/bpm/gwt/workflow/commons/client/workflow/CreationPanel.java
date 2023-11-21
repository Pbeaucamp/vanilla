package bpm.gwt.workflow.commons.client.workflow;

import java.util.List;

import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.NavigationPanel;
import bpm.gwt.commons.client.panels.StackNavigationPanel;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.workflow.commons.client.IWorkflowAppManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.workflow.commons.beans.Workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class CreationPanel extends Tab {

	private static ConsultPanelUiBinder uiBinder = GWT.create(ConsultPanelUiBinder.class);

	interface ConsultPanelUiBinder extends UiBinder<Widget, CreationPanel> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
		String panelNavigation();
	}

	@UiField
	MyStyle style;
	
	@UiField(provided=true)
	CollapsePanel collapsePanel;
	
	private WorkspacePanel workspacePanel;

	public CreationPanel(IWorkflowAppManager appManager, TabManager tabManager) {
		super(tabManager, LabelsCommon.lblCnst.creation_title(), true);
		collapsePanel = new CollapsePanel(300, 50);
		this.add(uiBinder.createAndBindUi(this));
		this.addStyleName(style.mainPanel());

		List<StackNavigationPanel> panels = appManager.getCategories(collapsePanel);
		
		NavigationPanel navigationPanel = new NavigationPanel();
		navigationPanel.addStyleName(style.panelNavigation());
		navigationPanel.setPanels(panels);
		collapsePanel.setLeftPanel(navigationPanel);
		
		workspacePanel = new WorkspacePanel(appManager, this);
		collapsePanel.setCenterPanel(workspacePanel);
	}

	public void openCreation(Workflow workflow) {
		workspacePanel.openCreation(workflow);
	}

	public void initNewCreation() {
		workspacePanel.initNewCreation();
	}
}
