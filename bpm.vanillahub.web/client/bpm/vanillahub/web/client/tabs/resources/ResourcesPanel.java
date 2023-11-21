package bpm.vanillahub.web.client.tabs.resources;

import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.resources.CiblesPanel;
import bpm.gwt.workflow.commons.client.resources.DatabaseServersPanel;
import bpm.gwt.workflow.commons.client.resources.ParametersPanel;
import bpm.gwt.workflow.commons.client.resources.VariablesPanel;
import bpm.gwt.workflow.commons.client.tabs.MultipleTabPanel;
import bpm.vanillahub.web.client.ContentDisplayPanel;

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

	@UiField(provided = true)
	CollapsePanel collapsePanel;

	public ResourcesPanel(ContentDisplayPanel displayPanel, IResourceManager resourceManager, boolean isConnectedToVanilla) {
		super(displayPanel, LabelsCommon.lblCnst.resources_title(), false);
		collapsePanel = new CollapsePanel(250, 55);
		this.add(uiBinder.createAndBindUi(this));

		MultipleTabPanel tabManager = new MultipleTabPanel(collapsePanel);
		if (!isConnectedToVanilla) {
			tabManager.addTab(new UsersPanel(resourceManager), true);
		}
		tabManager.addTab(new CertificatsPanel(resourceManager), false);
		tabManager.addTab(new CiblesPanel(resourceManager), false);
		tabManager.addTab(new SourcesPanel(resourceManager), false);
		tabManager.addTab(new XSDPanel(resourceManager), false);
		tabManager.addTab(new ServerMailPanel(resourceManager), false);
		tabManager.addTab(new VariablesPanel(resourceManager), false);
		tabManager.addTab(new ParametersPanel(resourceManager), false);
		tabManager.addTab(new DatabaseServersPanel(resourceManager), false);
		tabManager.addTab(new ListOfValuesPanel(resourceManager), false);
		tabManager.addTab(new ApplicationServerPanel(resourceManager), false);
		tabManager.addTab(new SocialNetworkPanel(resourceManager), false);

		collapsePanel.setLeftPanel(tabManager);

		this.addStyleName(style.mainPanel());
	}

}
