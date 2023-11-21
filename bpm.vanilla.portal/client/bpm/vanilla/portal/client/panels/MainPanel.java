package bpm.vanilla.portal.client.panels;

import bpm.gwt.commons.client.dialog.FeedbackDialog;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.center.RepositoryContentPanel;
import bpm.vanilla.portal.client.panels.navigation.NavigationPanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;
import bpm.vanilla.portal.client.popup.ViewsPopup.TypeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends CompositeWaitPanel {

	private static final int CLASSIC_TOP_TOOLBAR = 70;
	private static final int CLASSIC_NAVIGATION_PANEL = 350;
	private static final int CLASSIC_FOOTER = 30;
	private static final int CLASSIC_NAVIGATION = 345;
	private static final int CLASSIC_SEARCH = 195;

	private static final int FULL_SCREEN_TOP_TOOLBAR = 25;
	private static final int FULL_SCREEN_NAVIGATION_PANEL = 0;
	private static final int FULL_SCREEN_FOOTER = 0;
	private static final int FULL_SCREEN_NAVIGATION = 0;
	private static final int FULL_SCREEN_SEARCH = 0;

	private static final int MINIMAL_TOP_TOOLBAR = 25;
	private static final int MINIMAL_NAVIGATION_PANEL = 200;
	private static final int MINIMAL_FOOTER = 0;
	private static final int MINIMAL_NAVIGATION = 195;
	private static final int MINIMAL_SEARCH = 48;

	private static final int NAVIGATION_COLLAPSE = 35;
	private static final int NAVIGATION_PANEL_COLLAPSE = 30;
	private static final int SEARCH_COLLAPSE = 35;

	private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
	}

	@UiField
	DockLayoutPanel dockPanel;

	@UiField
	SimplePanel toolbarPanel, navigationPanel, footerPanel, mainPanel;

	private NavigationPanel navigation;
	private TopToolbar topToolbar;
	private ContentDisplayPanel contentDisplayPanel;

	public MainPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		buildToolbar();
		buildNavigationPanel();
		buildFooterPanel();
		buildMainPanel();

		this.addStyleName(VanillaCSS.BODY_BACKGROUND);

		InfoUser infoUser = biPortal.get().getInfoUser();
		if (infoUser.feedbackIsNotInited()) {
			Scheduler.get().scheduleDeferred(new Command() {
				@Override
				public void execute() {
					FeedbackDialog dial = new FeedbackDialog();
					dial.center();
				}
			});
		}

		if (infoUser.canSendFeedback()) {
			contentDisplayPanel.addFeedbackPanel(this, infoUser.getUser());
		}
	}

	private void buildToolbar() {
		topToolbar = new TopToolbar(this);
		topToolbar.addStyleName(VanillaCSS.MENU_HEAD);
		toolbarPanel.setWidget(topToolbar);
	}

	private void buildNavigationPanel() {
		navigation = new NavigationPanel(this);
		navigationPanel.setWidget(navigation);
	}

	private void buildFooterPanel() {
		footerPanel.addStyleName(VanillaCSS.BOTTOM);
	}

	private void buildMainPanel() {
		contentDisplayPanel = new ContentDisplayPanel(this);
		mainPanel.setWidget(contentDisplayPanel);
	}

	public RepositoryContentPanel getRepositoryContentPanel() {
		return contentDisplayPanel.getRepositoryContentPanel();
	}

	public ContentDisplayPanel getContentDisplayPanel() {
		return contentDisplayPanel;
	}

	public RepositoryContentPanel openRepositoryContentPanel() {
		contentDisplayPanel.openRepositoryContent();
		return contentDisplayPanel.getRepositoryContentPanel();
	}

	public void switchView(TypeView typeView) {
		switch (typeView) {
		case CLASSIC_VIEW:
			dockPanel.setWidgetSize(toolbarPanel, CLASSIC_TOP_TOOLBAR);

			dockPanel.setWidgetSize(navigationPanel, CLASSIC_NAVIGATION_PANEL);
			navigation.adaptSize(CLASSIC_NAVIGATION, CLASSIC_SEARCH, true, false);

			dockPanel.setWidgetSize(footerPanel, CLASSIC_FOOTER);
			break;
		case FULL_SCREEN_VIEW:
			dockPanel.setWidgetSize(toolbarPanel, FULL_SCREEN_TOP_TOOLBAR);

			dockPanel.setWidgetSize(navigationPanel, FULL_SCREEN_NAVIGATION_PANEL);
			navigation.adaptSize(FULL_SCREEN_NAVIGATION, FULL_SCREEN_SEARCH, true, false);

			dockPanel.setWidgetSize(footerPanel, FULL_SCREEN_FOOTER);
			break;
		case MINIMAL_VIEW:
			dockPanel.setWidgetSize(toolbarPanel, MINIMAL_TOP_TOOLBAR);

			dockPanel.setWidgetSize(navigationPanel, MINIMAL_NAVIGATION_PANEL);
			navigation.adaptSize(MINIMAL_NAVIGATION, MINIMAL_SEARCH, true, true);

			dockPanel.setWidgetSize(footerPanel, MINIMAL_FOOTER);
			break;

		default:
			break;
		}
	}

	public void collapseNavigationPanel(boolean isCollapse) {
		switch (topToolbar.getTypeView()) {
		case CLASSIC_VIEW:
			if (isCollapse) {
				dockPanel.setWidgetSize(navigationPanel, CLASSIC_NAVIGATION_PANEL);
				navigation.adaptSize(CLASSIC_NAVIGATION, CLASSIC_SEARCH, isCollapse, false);
			}
			else {
				dockPanel.setWidgetSize(navigationPanel, NAVIGATION_COLLAPSE);
				navigation.adaptSize(NAVIGATION_PANEL_COLLAPSE, SEARCH_COLLAPSE, isCollapse, false);
			}
			break;
		case FULL_SCREEN_VIEW:
			if (isCollapse) {
				dockPanel.setWidgetSize(navigationPanel, FULL_SCREEN_NAVIGATION_PANEL);
				navigation.adaptSize(FULL_SCREEN_NAVIGATION, FULL_SCREEN_SEARCH, isCollapse, false);
			}
			else {
				dockPanel.setWidgetSize(navigationPanel, NAVIGATION_COLLAPSE);
				navigation.adaptSize(NAVIGATION_PANEL_COLLAPSE, SEARCH_COLLAPSE, isCollapse, false);
			}
			break;
		case MINIMAL_VIEW:
			if (isCollapse) {
				dockPanel.setWidgetSize(navigationPanel, MINIMAL_NAVIGATION_PANEL);
				navigation.adaptSize(MINIMAL_NAVIGATION, MINIMAL_SEARCH, isCollapse, true);
			}
			else {
				dockPanel.setWidgetSize(navigationPanel, NAVIGATION_COLLAPSE);
				navigation.adaptSize(NAVIGATION_PANEL_COLLAPSE, SEARCH_COLLAPSE, isCollapse, true);
			}
			break;

		default:
			break;
		}
	}

	public void refreshDataPanels() {
		navigation.refreshDataPanels();
	}

	public void refreshViewer(TypeViewer viewer, boolean update, boolean canShowInDocumentPanel) {
		navigation.refreshViewer(viewer, update, canShowInDocumentPanel);
	}
	
	public NavigationPanel getNavigation() {
		return navigation;
	}
}
