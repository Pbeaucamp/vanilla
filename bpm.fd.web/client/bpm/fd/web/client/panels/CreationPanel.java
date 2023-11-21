package bpm.fd.web.client.panels;

import bpm.fd.core.Dashboard;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.utils.DashboardCreationHelper;
import bpm.gwt.commons.client.panels.AbstractTabHeader;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.tree.TreeObjectWidget.IDragListener;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.repository.IDashboard;
import bpm.vanilla.platform.core.repository.Template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class CreationPanel extends Tab {

	private static CreationPanelUiBinder uiBinder = GWT.create(CreationPanelUiBinder.class);

	interface CreationPanelUiBinder extends UiBinder<Widget, CreationPanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel mainPanel;

	private WorkspacePanel workspacePanel;
	private InfoUser infoUser;
	private IDragListener meatadataDragListener;

	private boolean isLoaded = false;
	private boolean isModified = false;

	private String description;

	public CreationPanel(TabManager tabManager, InfoUser infoUser, IDragListener meatadataDragListener) {
		super(tabManager, Labels.lblCnst.MyDashboard(), true);
		this.infoUser = infoUser;
		this.meatadataDragListener = meatadataDragListener;
		buildContent(tabManager);

		Dashboard dashboard = DashboardCreationHelper.buildDefaultDashboard(null, null);
		loadDashboard(dashboard, true);
	}

	public CreationPanel(TabManager tabManager, InfoUser infoUser, Template<IDashboard> template, IDragListener meatadataDragListener) {
		super(tabManager, template.getItem().getName(), true);
		this.infoUser = infoUser;
		this.meatadataDragListener = meatadataDragListener;
		
		buildContent(tabManager);
		loadTemplate(template, true);
	}

	public CreationPanel(TabManager tabManager, InfoUser infoUser, Dashboard dashboard, IDragListener meatadataDragListener) {
		super(tabManager, dashboard.getName(), true);
		this.infoUser = infoUser;
		this.meatadataDragListener = meatadataDragListener;
		buildContent(tabManager);
		loadDashboard(dashboard, true);
	}

	@Override
	public AbstractTabHeader buildTabHeader() {
		if (tabHeader == null) {
			tabHeader = new PageHeader(getTabTitle(), this, getTabManager(), null, isCloseable(), true, false);
		}
		return tabHeader;
	}

	private void buildContent(TabManager tabManager) {
		this.add(uiBinder.createAndBindUi(this));
		this.addStyleName(style.mainPanel());

		workspacePanel = new WorkspacePanel(this);
		mainPanel.add(workspacePanel);
	}

	public void loadDashboard(Dashboard dashboard, boolean newTab) {
		this.isLoaded = true;
		this.description = dashboard.getDescription();

		if (!newTab) {
			setTabHeaderTitle(dashboard.getName());
			refreshTitle();
		}

		workspacePanel.loadDashboard(dashboard);
		setModified(false);
	}

	public void loadTemplate(Template<IDashboard> template, boolean newTab) {
		this.isLoaded = true;
		
		Dashboard dashboard = (Dashboard) template.getItem();
		this.description = dashboard.getDescription();

		if (!newTab) {
			setTabHeaderTitle(dashboard.getName());
			refreshTitle();
		}

		workspacePanel.loadTemplate(template);
		setModified(true);
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setModified(boolean isModified) {
		if (!this.isModified && isModified) {
			this.isModified = true;

			if (getTabHeader() != null) {
				getTabHeader().setModified(true);
			}
		}
		else if (this.isModified && !isModified) {
			this.isModified = false;

			if (getTabHeader() != null) {
				getTabHeader().setModified(false);
			}
		}
	}

	public boolean isModified() {
		return isModified;
	}

	public String getDashboardName() {
		return getTabTitle();
	}

	public String getDashboardDescription() {
		return description;
	}

	public void refreshDashboardOptions(String name, String description) {
		tabHeader.setTitle(name);
		this.description = description;
	}

	public InfoUser getInfoUser() {
		return infoUser;
	}

	public IDragListener getMeatadataDragListener() {
		return meatadataDragListener;
	}

	public WorkspacePanel getWorkspacePanel() {
		return workspacePanel;
	}

	public AbstractTabHeader getTabHeader() {
		return tabHeader;
	}
}
