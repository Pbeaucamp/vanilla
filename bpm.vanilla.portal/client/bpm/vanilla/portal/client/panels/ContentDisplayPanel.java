package bpm.vanilla.portal.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.feedback.FeedbackPanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.panels.AbstractTabHeader;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.viewer.VanillaViewer;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.PortailItemCube;
import bpm.gwt.commons.shared.repository.PortailItemCubeView;
import bpm.gwt.commons.shared.repository.PortailItemFasd;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.dialog.SubscribeDialog;
import bpm.vanilla.portal.client.dialog.properties.PropertyDialog;
import bpm.vanilla.portal.client.panels.center.AccessRequestPanel;
import bpm.vanilla.portal.client.panels.center.AlertManagerPanel;
import bpm.vanilla.portal.client.panels.center.ArchivePanel;
import bpm.vanilla.portal.client.panels.center.DocumentManagerPanel;
import bpm.vanilla.portal.client.panels.center.ForumPanel;
import bpm.vanilla.portal.client.panels.center.HistoryPanel;
import bpm.vanilla.portal.client.panels.center.ItemInformationPanel;
import bpm.vanilla.portal.client.panels.center.MapDesignerPanel;
import bpm.vanilla.portal.client.panels.center.ProcessManagerPanel;
import bpm.vanilla.portal.client.panels.center.RepositoryContentPanel;
import bpm.vanilla.portal.client.panels.center.SearchManagerPanel;
import bpm.vanilla.portal.client.panels.center.TaskPanel;
import bpm.vanilla.portal.client.panels.center.ValidationManagerPanel;
import bpm.vanilla.portal.client.panels.center.VanillaHubPanel;
import bpm.vanilla.portal.client.panels.center.WelcomePanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;
import bpm.vanilla.portal.client.services.AdminService;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.services.HistoryService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContentDisplayPanel extends Composite implements TabManager {

	// private static final double DEFAULT_PERCENTAGE = 9.1;
	private static final double MIN_DEFAULT_PERCENTAGE = 0.5;

	private static ContentDisplayPanelUiBinder uiBinder = GWT.create(ContentDisplayPanelUiBinder.class);

	interface ContentDisplayPanelUiBinder extends UiBinder<Widget, ContentDisplayPanel> {
	}

	interface MyStyle extends CssResource {
		String displayNone();
	}

	@UiField
	MyStyle style;

	@UiField
	Image btnProcessManager, btnVanillaHub;

	@UiField
	HTMLPanel contentDisplayPanel, tabHeaderPanel, rightToolbar;

	@UiField
	HTMLPanel contentPanel;
	
	private InfoUser infoUser;

	private MainPanel mainPanel;

	private WelcomePanel welcomePanel;
	private RepositoryContentPanel repositoryContentPanel;
	private TaskPanel taskPanel;
	private HistoryPanel historyPanel;
	private ProcessManagerPanel processManagerPanel;
	private AlertManagerPanel alertManagerPanel;
	private AccessRequestPanel accessRequestPanel;
	private DocumentManagerPanel documentManagerPanel;
	private SearchManagerPanel searchManagerPanel;
	private ArchivePanel archivePanel;
	private ForumPanel forumPanel;
	private VanillaHubPanel vanillaHubPanel;
	private ValidationManagerPanel validationManagerPanel;

	private AbstractTabHeader selectedBtn;
	private Tab selectedPanel;

	private List<AbstractTabHeader> openTabs = new ArrayList<AbstractTabHeader>();

	private MapDesignerPanel mapDesignerPanel;

	public ContentDisplayPanel(MainPanel mainPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.infoUser = biPortal.get().getInfoUser();
		this.mainPanel = mainPanel;

		buildContentPanel();

		rightToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);

		if (!biPortal.get().getInfoUser().getUser().isSuperUser()) {
			btnProcessManager.removeFromParent();
		}
		
		if (!infoUser.canAccess(IRepositoryApi.HUB)) {
			btnVanillaHub.removeFromParent();
		}
	}

	private void buildContentPanel() {
		if (welcomePanel == null) {
			welcomePanel = new WelcomePanel(this);
		}

		changeTab(welcomePanel);
	}

	@UiHandler("btnHome")
	public void onHomeClick(ClickEvent event) {
		if (welcomePanel == null) {
			welcomePanel = new WelcomePanel(this);
		}

		changeTab(welcomePanel);
	}

	@UiHandler("btnRepositoryContent")
	public void onRepositoryContentClick(ClickEvent event) {
		openRepositoryContent();
	}

	public void openRepositoryContent() {
		if (repositoryContentPanel == null) {
			repositoryContentPanel = new RepositoryContentPanel(this);
			repositoryContentPanel.refresh();
		}

		changeTab(repositoryContentPanel);
	}

	// @UiHandler("btnViewer")
	// public void onViewerClick(ClickEvent event) {
	// boolean addTab = false;
	// if (vanillaViewer == null) {
	// vanillaViewer = new VanillaViewer(this,
	// biPortal.get().getInfoUser().getGroup(),
	// biPortal.get().getInfoUser().getAvailableGroups());
	// addTab = true;
	// }
	//
	// changeTab(vanillaViewer, addTab);
	// }

	@UiHandler("btnTask")
	public void onTaskClick(ClickEvent event) {
		if (taskPanel == null) {
			taskPanel = new TaskPanel(this);
		}

		changeTab(taskPanel);
	}

	@UiHandler("btnHisto")
	public void onHistoClick(ClickEvent event) {
		if (historyPanel == null) {
			historyPanel = new HistoryPanel(this, null, "");
		}

		changeTab(historyPanel);
	}

	@UiHandler("btnSearch")
	public void onSearchClick(ClickEvent event) {
		if (searchManagerPanel == null) {
			searchManagerPanel = new SearchManagerPanel(this);
		}

		changeTab(searchManagerPanel);
	}

	@UiHandler("btnDocumentManager")
	public void onDocumentManagerClick(ClickEvent event) {
		if (documentManagerPanel == null) {
			documentManagerPanel = new DocumentManagerPanel(this);
		}

		changeTab(documentManagerPanel);
	}

	@UiHandler("btnRequest")
	public void onRequestClick(ClickEvent event) {
		if (accessRequestPanel == null) {
			accessRequestPanel = new AccessRequestPanel(this);
		}

		changeTab(accessRequestPanel);
	}

	@UiHandler("btnAlertManager")
	public void onAlertManagerClick(ClickEvent event) {
		if (alertManagerPanel == null) {
			alertManagerPanel = new AlertManagerPanel(this);
		}

		changeTab(alertManagerPanel);
	}

	@UiHandler("btnProcessManager")
	public void onProcessManagerClick(ClickEvent event) {
		if (processManagerPanel == null) {
			processManagerPanel = new ProcessManagerPanel(this);
		}

		changeTab(processManagerPanel);
	}
	
	@UiHandler("btnArchiveManager")
	public void onArchiveManagerClick(ClickEvent event) {
		if (archivePanel == null) {
			archivePanel = new ArchivePanel(this);
		}

		changeTab(archivePanel);
	}

	@UiHandler("btnMapDesigner")
	public void onMapDesignerClick(ClickEvent event) {
		if (mapDesignerPanel == null) {
			mapDesignerPanel = new MapDesignerPanel(this);
		}

		changeTab(mapDesignerPanel);
	}
	
	@UiHandler("btnForum")
	public void onForumClick(ClickEvent event) {
		if (forumPanel == null) {
			forumPanel = new ForumPanel(this);
		}

		changeTab(forumPanel);
	}
	
	@UiHandler("btnVanillaHub")
	public void onVanillaHubClick(ClickEvent event) {
		if (vanillaHubPanel == null) {
			vanillaHubPanel = new VanillaHubPanel(this, infoUser);
		}

		changeTab(vanillaHubPanel);
	}

	@UiHandler("btnValidationManager")
	public void onValidationManager(ClickEvent event) {
		if (validationManagerPanel == null) {
			validationManagerPanel = new ValidationManagerPanel(this);
		}

		changeTab(validationManagerPanel);
	}

	public RepositoryContentPanel getRepositoryContentPanel() {
		return repositoryContentPanel != null ? (RepositoryContentPanel) repositoryContentPanel : null;
	}

	public void openViewer(IRepositoryObject item) {
		VanillaViewer vanillaViewer = new VanillaViewer(this, biPortal.get().getInfoUser().getGroup(), biPortal.get().getInfoUser().getAvailableGroups());
		vanillaViewer.openViewer(item, biPortal.get().getInfoUser(), false, false);

		changeTab(vanillaViewer);
	}

	private void changeTab(Tab selectedPanel) {
		if (selectedBtn != null) {
			this.selectedBtn.setSelected(false);
		}

		if (this.selectedPanel != null) {
			this.selectedPanel.addStyleName(style.displayNone());
		}

		AbstractTabHeader header = selectedPanel.buildTabHeader();
		if (!header.isOpen()) {
			tabHeaderPanel.add(header);
			openTabs.add(header);

			updateSize(openTabs);
		}

		this.selectedBtn = header;
		this.selectedBtn.setSelected(true);

		this.selectedPanel = selectedPanel;
		this.selectedPanel.removeStyleName(style.displayNone());

		if (!selectedPanel.isOpen()) {
			header.setOpen(true);
			this.contentPanel.add(selectedPanel);
		}
	}

	private void updateSize(List<AbstractTabHeader> openTabs) {
		double percentage = calcPercentage(openTabs.size());

		for (AbstractTabHeader tabHeader : openTabs) {
			tabHeader.applySize(percentage);
		}
	}

	private double calcPercentage(int tabNumber) {
		if (tabNumber > 0) {
			double value = (100 / tabNumber) - (0.05 * tabNumber);
			return value < MIN_DEFAULT_PERCENTAGE ? MIN_DEFAULT_PERCENTAGE : value;
		}
		else {
			return 0;
		}
	}

	@Override
	public void closeTab(AbstractTabHeader tabHeader) {
		int index = openTabs.indexOf(tabHeader);
		openTabs.remove(index);
		tabHeaderPanel.remove(tabHeader);
		contentPanel.remove(tabHeader.getTab());

		updateSize(openTabs);

		if (selectedBtn == tabHeader && !openTabs.isEmpty()) {
			if (index > 0) {
				changeTab(openTabs.get(index - 1).getTab());
			}
			else {
				changeTab(openTabs.get(0).getTab());
			}
		}
	}

	@Override
	public void selectTab(AbstractTabHeader tabHeader) {
		changeTab(tabHeader.getTab());
	}
	
	@Override
	public void postProcess() {
		biPortal.get().checkReportBackgrounds(mainPanel, 2);
	}

	public void refreshTreeRepository() {
		mainPanel.refreshViewer(TypeViewer.REPOSITORY, false, false);
	}

	public void openView(final IWait waitPanel, final PortailRepositoryItem item) {
		waitPanel.showWaitPart(true);

		HistoryService.Connect.getInstance().getLastView(item.getId(), new AsyncCallback<DisplayItem>() {

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.NoHistoryFound() + " " + item.getName());
			}

			@Override
			public void onSuccess(DisplayItem lastView) {
				waitPanel.showWaitPart(false);

				openViewer(lastView);
			}
		});
	}

	public void openItemHistory(PortailRepositoryItem item) {
		HistoryPanel historyPanel = new HistoryPanel(this, item, " - " + item.getName());

		changeTab(historyPanel);
	}

	public void openFasd(final IWait waitPanel, PortailRepositoryItem dtoItem) {
		waitPanel.showWaitPart(true);

		int fasdDirItemId = -1;
		String cubeName = null;
		String viewName = null;

		if (dtoItem instanceof PortailItemFasd) {
			fasdDirItemId = dtoItem.getId();
		}
		else if (dtoItem instanceof PortailItemCube) {
			fasdDirItemId = ((PortailItemCube) dtoItem).getFASDParentId();
			cubeName = ((PortailItemCube) dtoItem).getName();
		}
		else if (dtoItem instanceof PortailItemCubeView) {
			fasdDirItemId = ((PortailItemCubeView) dtoItem).getCubeDto().getFASDParentId();
			cubeName = ((PortailItemCubeView) dtoItem).getCubeDto().getName();
			viewName = ((PortailItemCubeView) dtoItem).getName();
		}
		else if (dtoItem.getType() != IRepositoryApi.FAV_TYPE) {
			MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Error(), ToolsGWT.lblCnst.UnexpectedType());
			return;
		}

		ReportingService.Connect.getInstance().getForwardUrlForCubes(fasdDirItemId, cubeName, viewName, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String url) {
				ToolsGWT.doRedirect(url);

				waitPanel.showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.FailedGetUrl());
			}
		});

	}

	public void openBirtViewer(final IWait waitPanel, PortailRepositoryItem item) {
		waitPanel.showWaitPart(true);

		BiPortalService.Connect.getInstance().runBIRTFromRep(item.getId(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}

			@Override
			public void onSuccess(String url) {
				waitPanel.showWaitPart(false);

				ToolsGWT.doRedirect(url);
			}

		});
	}

	public void showProperties(final IWait waitPanel, final IRepositoryObject repositoryObject) {
		if (repositoryObject instanceof PortailRepositoryItem) {
			PortailRepositoryItem item = (PortailRepositoryItem) repositoryObject;

			waitPanel.showWaitPart(true);

			AdminService.Connect.getInstance().getDirectoryItemById(item, new AsyncCallback<PortailRepositoryItem>() {

				@Override
				public void onSuccess(PortailRepositoryItem result) {
					waitPanel.showWaitPart(false);

					PropertyDialog dial = new PropertyDialog(ContentDisplayPanel.this, result);
					dial.center();
				}

				@Override
				public void onFailure(Throwable caught) {
					waitPanel.showWaitPart(false);

					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, "");
				}
			});
		}
		else if (repositoryObject instanceof PortailRepositoryDirectory) {
			PortailRepositoryDirectory dir = (PortailRepositoryDirectory) repositoryObject;

			PropertyDialog dial = new PropertyDialog(dir);
			dial.center();
		}
	}

	public void subscribe(final IWait waitPanel, PortailRepositoryItem item) {
		SubscribeDialog dial = new SubscribeDialog(item);
		dial.center();
//		waitPanel.showWaitPart(true);
//
//		BiPortalService.Connect.getInstance().subscribeToItem(item, new AsyncCallback<Boolean>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				waitPanel.showWaitPart(false);
//
//				caught.printStackTrace();
//
//				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.SubscribeFailure());
//			}
//
//			@Override
//			public void onSuccess(Boolean result) {
//				waitPanel.showWaitPart(false);
//
//				if (!result) {
//					MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.SubscribeSuccess());
//				}
//				else {
//					MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.AlreadySubscribe());
//				}
//			}
//		});
	}

	public void refreshDataPanels() {
		mainPanel.refreshDataPanels();
	}

	public void deleteItem(final IWait waitPanel, final PortailRepositoryItem item) {
		final InformationsDialog dial = new InformationsDialog(ToolsGWT.lblCnst.Delete(), ToolsGWT.lblCnst.Ok(), ToolsGWT.lblCnst.Cancel(), ToolsGWT.lblCnst.ConfirmDeleteItem(), true);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					waitPanel.showWaitPart(true);

					BiPortalService.Connect.getInstance().deleteItem(item.getId(), new AsyncCallback<Void>() {

						public void onFailure(Throwable caught) {
							waitPanel.showWaitPart(false);

							caught.printStackTrace();

							ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
						}

						public void onSuccess(Void arg0) {
							waitPanel.showWaitPart(false);

							mainPanel.refreshDataPanels();

							MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.ElementRemoveSuccesfully());
						}
					});
				}
			}
		});
		dial.center();
	}

	public void addToWatchlist(final IWait waitPanel, PortailRepositoryItem item, final ItemInformationPanel menu) {
		waitPanel.showWaitPart(true);

		BiPortalService.Connect.getInstance().addToMyWatchList(item.getId(), item.getType(), new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				waitPanel.showWaitPart(false);

				if (result != null && result) {
					refreshWatchlist(waitPanel, ToolsGWT.lblCnst.ElementAddSuccesfully(), menu);
				}
				else {
					MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.Error());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}
		});

	}

	public void removeFromWatchlist(final IWait waitPanel, PortailRepositoryItem item, final ItemInformationPanel menu) {
		waitPanel.showWaitPart(true);

		BiPortalService.Connect.getInstance().removeToMyWatchList(item.getId(), new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				waitPanel.showWaitPart(false);

				if (result.indexOf("<error>") == -1) {
					refreshWatchlist(waitPanel, ToolsGWT.lblCnst.ElementRemoveSuccesfully(), menu);
				}
				else {
					String msg = result.replace("<error>", "").replace("</error>", "\n");

					MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Error(), msg);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}
		});
	}

	private void refreshWatchlist(final IWait waitPanel, final String messageSuccess, final ItemInformationPanel menu) {
		BiPortalService.Connect.getInstance().getMyWatchList(new AsyncCallback<PortailRepositoryDirectory>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				waitPanel.showWaitPart(false);

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}

			@Override
			public void onSuccess(PortailRepositoryDirectory item) {
				waitPanel.showWaitPart(false);

				biPortal.get().getInfoUser().setMyWatchlist(item);

				MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), messageSuccess);

				mainPanel.refreshViewer(TypeViewer.WATCH_LIST, false, true);

				if (menu != null) {
					menu.refreshButtons();
				}
			}
		});
	}

	public void removeFromOnOpen(final IWait waitPanel, PortailRepositoryItem item, final ItemInformationPanel menu) {
		waitPanel.showWaitPart(true);

		BiPortalService.Connect.getInstance().removeOpenItem(item.getId(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.ErrorRemovingElement());
			}

			@Override
			public void onSuccess(Void arg0) {
				refreshOpenItems(waitPanel, ToolsGWT.lblCnst.ElementRemoveSuccesfully(), menu);
			}
		});
	}

	public void addToOnOpen(final IWait waitPanel, PortailRepositoryItem item, final ItemInformationPanel menu) {
		waitPanel.showWaitPart(true);

		BiPortalService.Connect.getInstance().addOpenItem(item.getId(), new AsyncCallback<Void>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());

				waitPanel.showWaitPart(false);
			}

			public void onSuccess(Void arg0) {
				refreshOpenItems(waitPanel, ToolsGWT.lblCnst.ElementAddSuccesfully(), menu);
			}
		});
	}

	private void refreshOpenItems(final IWait waitPanel, final String messageSuccess, final ItemInformationPanel menu) {
		BiPortalService.Connect.getInstance().getOpenItems(new AsyncCallback<PortailRepositoryDirectory>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				waitPanel.showWaitPart(false);

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}

			@Override
			public void onSuccess(PortailRepositoryDirectory item) {
				waitPanel.showWaitPart(false);

				biPortal.get().getInfoUser().setOpenOnStartup(item);

				MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), messageSuccess);

				mainPanel.refreshViewer(TypeViewer.OPEN_ON_STARTUP, false, true);

				if (menu != null) {
					menu.refreshButtons();
				}
			}

		});
	}
	
	public void addFeedbackPanel(IWait waitPanel, User user) {
		FeedbackPanel panel = new FeedbackPanel(waitPanel, user, 44);
		contentDisplayPanel.add(panel);
	}

	@Override
	public int getIndex(AbstractTabHeader tabHeader) {
		return -1;
	}

	@Override
	public void updatePosition(String tabId, int index) { }
}
