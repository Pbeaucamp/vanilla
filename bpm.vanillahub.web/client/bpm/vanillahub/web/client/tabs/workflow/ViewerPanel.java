package bpm.vanillahub.web.client.tabs.workflow;

import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.hub.IHubManager;
import bpm.gwt.workflow.commons.client.workflow.hub.VanillaHubViewer;
import bpm.vanillahub.web.client.ContentDisplayPanel;
import bpm.workflow.commons.beans.Workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ViewerPanel extends Tab implements IHubManager {

	private static ViewerPanelUiBinder uiBinder = GWT.create(ViewerPanelUiBinder.class);

	interface ViewerPanelUiBinder extends UiBinder<Widget, ViewerPanel> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel mainPanel;
	
	private ContentDisplayPanel displayPanel;
	
	private VanillaHubViewer vanillaHubViewer;
	
	public ViewerPanel(ContentDisplayPanel displayPanel, InfoUser infoUser) {
		super(displayPanel, LabelsCommon.lblCnst.consult_title(), false);
		this.displayPanel = displayPanel;
		this.add(uiBinder.createAndBindUi(this));
		
		this.addStyleName(style.mainPanel());
		
		this.vanillaHubViewer = new VanillaHubViewer(this, infoUser);
		mainPanel.add(vanillaHubViewer);
	}

	public void loadWorkflows() {
		vanillaHubViewer.loadWorkflows();
	}

	@Override
	public void doActionAfterSelection() {
		vanillaHubViewer.redraw();
	}

	@Override
	public boolean canEditWorkflow() {
		return true;
	}

	@Override
	public void displayWorkflow(Workflow workflow) {
		displayPanel.displayWorkflow(workflow);
	}

	@Override
	public IResourceManager getResourceManager() {
		return displayPanel.getResourceManager();
	}
}
