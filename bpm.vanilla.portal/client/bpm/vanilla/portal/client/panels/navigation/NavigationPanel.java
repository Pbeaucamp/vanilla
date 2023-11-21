package bpm.vanilla.portal.client.panels.navigation;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.panels.MainPanel;
import bpm.vanilla.portal.client.popup.TypeViewerPopup;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class NavigationPanel extends Composite {

	private static NavigationPanelUiBinder uiBinder = GWT.create(NavigationPanelUiBinder.class);

	interface NavigationPanelUiBinder extends UiBinder<Widget, NavigationPanel> {
	}

	interface MyStyle extends CssResource {
		String btnSelected();

		String imgTypeViewer();
	}

	// @UiField
	// SimplePanel contentPanel, lastUsedPanel, openOnStartupPanel;

	@UiField
	MyStyle style;

	@UiField
	SimplePanel contentPanel;

	@UiField
	Image imgChangeView, imgTypeViewer, imgRefresh, imgCollapse, imgExpand;

	@UiField
	Label lblTypeViewer;

	@UiField
	HTMLPanel navigationPanel, toolbarNavigation;

	private MainPanel mainPanel;

	private TypeViewer currentViewer = TypeViewer.REPOSITORY;
	private AbstractRepositoryStackPanel repositoryContentPanel, lastUsedPanel, openItemPanel, watchlistPanel, reportBackgroundPanel;

	public NavigationPanel(MainPanel mainPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;

		imgExpand.setVisible(false);

		buildContentPanel();
		buildLastUsedPanel();
		buildOpenOnStartupPanel();
		buildWatchlistPanel();
		buildReportBackgroundPanel();

		toolbarNavigation.addStyleName(VanillaCSS.NAVIGATION_TOOLBAR);
	}

	private void buildContentPanel() {
		repositoryContentPanel = new TreeRepositoryPanel(mainPanel);
		contentPanel.setWidget(repositoryContentPanel);

		imgTypeViewer.setResource(PortalImage.INSTANCE.repository_24_b());
		imgTypeViewer.addStyleName(style.imgTypeViewer());
		lblTypeViewer.setText(biPortal.get().getInfoUser().getRepository().getName());

		repositoryContentPanel.refresh(true, true);
	}

	private void buildLastUsedPanel() {
		lastUsedPanel = new LastUsedPanel(mainPanel);
		lastUsedPanel.refresh(true, false);
	}

	private void buildOpenOnStartupPanel() {
		openItemPanel = new OpenStartupPanel(mainPanel);
		openItemPanel.refresh(true, false);
	}

	private void buildWatchlistPanel() {
		watchlistPanel = new WatchlistPanel(mainPanel);
		watchlistPanel.refresh(true, false);
	}

	private void buildReportBackgroundPanel() {
		reportBackgroundPanel = new ReportBackgroundPanel(mainPanel);
		reportBackgroundPanel.refresh(true, false);
	}

	@UiHandler("imgChangeView")
	public void onChangeViewClick(ClickEvent event) {
		TypeViewerPopup typeViewerPopup = new TypeViewerPopup(this);
		typeViewerPopup.setPopupPosition(-5, 110);
		typeViewerPopup.addCloseHandler(closeMenu);
		typeViewerPopup.show();

		imgChangeView.addStyleName(style.btnSelected());
	}

	@UiHandler("imgRefresh")
	public void onRefreshClick(ClickEvent event) {
		switch (currentViewer) {
		case REPOSITORY:
			repositoryContentPanel.refresh(true, true);
			break;
		case LAST_USED:
			lastUsedPanel.refresh(true, true);
			break;
		case OPEN_ON_STARTUP:
			openItemPanel.refresh(true, true);
			break;
		case WATCH_LIST:
			watchlistPanel.refresh(true, true);
			break;
		case REPORT_BACKGROUND:
			reportBackgroundPanel.refresh(true, true);
			break;
		default:
			break;
		}
	}

	@UiHandler("imgCollapse")
	public void onCollapseClick(ClickEvent event) {
		mainPanel.collapseNavigationPanel(false);
	}

	@UiHandler("imgExpand")
	public void onExpandClick(ClickEvent event) {
		mainPanel.collapseNavigationPanel(true);
	}

	private void managePanel(boolean isCollapse) {
		if (isCollapse) {
			toolbarNavigation.setVisible(true);
			contentPanel.setVisible(true);

			imgExpand.setVisible(false);

			this.removeStyleName(VanillaCSS.RIGHT_TOOLBAR);
		}
		else {
			toolbarNavigation.setVisible(false);
			contentPanel.setVisible(false);

			imgExpand.setVisible(true);

			this.addStyleName(VanillaCSS.RIGHT_TOOLBAR);
		}
	}

	public void switchPanel(TypeViewer typeViewer) {
		if (currentViewer != typeViewer) {
			this.currentViewer = typeViewer;
			switch (typeViewer) {
			case REPOSITORY:
				contentPanel.setWidget(repositoryContentPanel);

				imgTypeViewer.setResource(PortalImage.INSTANCE.repository_24_b());
				lblTypeViewer.setText(ToolsGWT.lblCnst.Repository());

				repositoryContentPanel.refresh(false, true);
				break;
			case LAST_USED:
				contentPanel.setWidget(lastUsedPanel);

				imgTypeViewer.setResource(PortalImage.INSTANCE.lastused_24_b());
				lblTypeViewer.setText(ToolsGWT.lblCnst.LastUsed());

				lastUsedPanel.refresh(true, true);
				break;
			case OPEN_ON_STARTUP:
				contentPanel.setWidget(openItemPanel);

				imgTypeViewer.setResource(PortalImage.INSTANCE.open_startup_24_b());
				lblTypeViewer.setText(ToolsGWT.lblCnst.OnOpen());

				openItemPanel.refresh(true, true);
				break;
			case WATCH_LIST:
				contentPanel.setWidget(watchlistPanel);

				imgTypeViewer.setResource(PortalImage.INSTANCE.favorite_24_b());
				lblTypeViewer.setText(ToolsGWT.lblCnst.MyWatchList());

				watchlistPanel.refresh(true, true);
				break;
			case REPORT_BACKGROUND:
				contentPanel.setWidget(reportBackgroundPanel);

				imgTypeViewer.setResource(PortalImage.INSTANCE.favorite_24_b());
				lblTypeViewer.setText(ToolsGWT.lblCnst.ReportBackgrounds());

				reportBackgroundPanel.refresh(true, true);
				break;
			default:
				break;
			}
		}
	}

	private CloseHandler<PopupPanel> closeMenu = new CloseHandler<PopupPanel>() {

		@Override
		public void onClose(CloseEvent<PopupPanel> event) {
			imgChangeView.removeStyleName(style.btnSelected());
		}
	};

	public void adaptSize(int navigationPanelWidth, int searchWidth, boolean isCollapse, boolean hideLabel) {
		navigationPanel.setWidth(navigationPanelWidth + "px");
		repositoryContentPanel.setSearchWidth(searchWidth);
		lastUsedPanel.setSearchWidth(searchWidth);
		openItemPanel.setSearchWidth(searchWidth);
		watchlistPanel.setSearchWidth(searchWidth);

		if (hideLabel) {
			lblTypeViewer.setVisible(false);
		}
		else {
			lblTypeViewer.setVisible(true);
		}

		managePanel(isCollapse);
	}

	public void refreshDataPanels() {
		repositoryContentPanel.refresh(true, true);
		lastUsedPanel.refresh(true, false);
		openItemPanel.refresh(true, false);
		watchlistPanel.refresh(true, false);
	}

	public void refreshViewer(TypeViewer viewer, boolean update, boolean canShowInDocumentPanel) {
		boolean showInDocumentPanel = currentViewer != null && viewer == currentViewer;

		switch (viewer) {
		case REPOSITORY:
			repositoryContentPanel.refresh(update, showInDocumentPanel && canShowInDocumentPanel);
			break;
		case LAST_USED:
			lastUsedPanel.refresh(update, showInDocumentPanel && canShowInDocumentPanel);
			break;
		case OPEN_ON_STARTUP:
			openItemPanel.refresh(update, showInDocumentPanel && canShowInDocumentPanel);
			break;
		case WATCH_LIST:
			watchlistPanel.refresh(update, showInDocumentPanel && canShowInDocumentPanel);
			break;
		default:
			break;
		}
	}
}
