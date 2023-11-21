package bpm.fd.web.client;

import bpm.fd.core.Dashboard;
import bpm.fd.web.client.panels.DashboardsPanel;
import bpm.fd.web.client.panels.LeftPanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.tree.TreeObjectWidget.IDragListener;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.repository.IDashboard;
import bpm.vanilla.platform.core.repository.Template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ContentDisplayPanel extends Composite {

	private static ContentDisplayPanelUiBinder uiBinder = GWT.create(ContentDisplayPanelUiBinder.class);

	interface ContentDisplayPanelUiBinder extends UiBinder<Widget, ContentDisplayPanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();
		String panelNavigation();
	}

	@UiField
	MyStyle style;
	
	@UiField(provided=true)
	CollapsePanel collapsePanel;
	
	private LeftPanel leftPanel;
	private DashboardsPanel dashboardsPanel;

	public ContentDisplayPanel(IWait waitPanel, InfoUser infoUser) {
		collapsePanel = new CollapsePanel(300, 50);
		initWidget(uiBinder.createAndBindUi(this));

		this.leftPanel = new LeftPanel(collapsePanel);
		IDragListener meatadataDragListener = leftPanel.getMetadataTree();
		collapsePanel.setLeftPanel(leftPanel);
		
		dashboardsPanel = new DashboardsPanel(waitPanel, infoUser, meatadataDragListener);
		collapsePanel.setCenterPanel(dashboardsPanel);
	}

	public void openTemplate(Template<IDashboard> template) {
		dashboardsPanel.openTemplate(template);
	}

	public void openCreation(Dashboard dashboard) {
		dashboardsPanel.openCreation(dashboard);
	}
}
