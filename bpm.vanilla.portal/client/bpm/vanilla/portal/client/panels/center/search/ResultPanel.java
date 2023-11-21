package bpm.vanilla.portal.client.panels.center.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.PublicUrlDialog;
import bpm.gwt.commons.client.popup.IShare;
import bpm.gwt.commons.client.popup.SharePopup;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.viewer.aklabox.AklaboxShareDialog;
import bpm.gwt.commons.client.viewer.dialog.CmisShareWizard;
import bpm.gwt.commons.client.viewer.dialog.MailShareDialog;
import bpm.gwt.commons.shared.InfoShare;
import bpm.gwt.commons.shared.InfoShareCmis;
import bpm.gwt.commons.shared.InfoShareMail;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.utils.ExportResult;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.Listeners.GedMenu;
import bpm.vanilla.portal.client.dialog.RequestAccessDialog;
import bpm.vanilla.portal.client.dialog.ged.CheckinDialog;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.panels.IGedCheck;
import bpm.vanilla.portal.client.panels.center.SearchManagerPanel;
import bpm.vanilla.portal.client.services.GedService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class ResultPanel extends Composite implements IGedCheck, IShare {

	private static ResultPanelUiBinder uiBinder = GWT.create(ResultPanelUiBinder.class);

	interface ResultPanelUiBinder extends UiBinder<Widget, ResultPanel> {
	}

	interface MyStyle extends CssResource {
		String pager();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelContent, panelPager;

	private InfoUser infoUser;

	private SearchManagerPanel parentPanel;

	private ListDataProvider<DocumentVersionDTO> dataProvider;
	private ListHandler<DocumentVersionDTO> sortHandler;
	private List<DocumentVersionDTO> documents;
	private DocumentVersionDTO selectedVersion;

	private float max = 0;
	private float min = 0;

	public ResultPanel(SearchManagerPanel parentPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parentPanel = parentPanel;
		this.infoUser = biPortal.get().getInfoUser();

		panelContent.setWidget(createGridData());
	}

	public void onPageLoad() {
		if (documents != null) {
			if (!documents.isEmpty()) {
				max = documents.get(0).getDocumentParent().getScore();
				min = documents.get(documents.size() - 1).getDocumentParent().getScore();
			}

			dataProvider.setList(documents);
			sortHandler.setList(dataProvider.getList());
		}
	}

	public void setDocuments(List<DocumentVersionDTO> documents) {
		this.documents = documents;
	}

	private void loadDocument(final DocumentVersionDTO item) {
		if (item.getDocumentParent().isGranted()) {
			GedService.Connect.getInstance().loadDocument(item.getKey(), item.getFormat(), new AsyncCallback<String>() {

				@Override
				public void onSuccess(String result) {
					if (result.indexOf("<error>") > -1) {
						String msg = result.replace("<error>", "").replace("</error>", "");
						if (msg.equalsIgnoreCase("")) {
							msg = ToolsGWT.lblCnst.NoHistoryFound() + " " + item.getName();
						}

						MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Error(), ToolsGWT.lblCnst.DocumentNotFound());
					}
					else {
						String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + result + "&" + CommonConstants.REPORT_OUTPUT + "=" + item.getFormat();

						ToolsGWT.doRedirect(fullUrl);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.NoHistory());
				}
			});
		}
		else {
			final InformationsDialog dial = new InformationsDialog(ToolsGWT.lblCnst.AskForRequestTitle(), ToolsGWT.lblCnst.Ok(), ToolsGWT.lblCnst.Cancel(), ToolsGWT.lblCnst.AskForRequest(), true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						askForRequest(item);
					}
				}
			});
			dial.center();
		}
	}

	private DataGrid<DocumentVersionDTO> createGridData() {
		CustomImageCell imageCell = new CustomImageCell();
		Column<DocumentVersionDTO, ImageResource> iconColumn = new Column<DocumentVersionDTO, ImageResource>(imageCell) {

			@Override
			public ImageResource getValue(DocumentVersionDTO object) {
				if (object.getDocumentParent().isGranted()) {
					return PortalImage.INSTANCE.apply();
				}
				else {
					return PortalImage.INSTANCE.cancel();
				}
			}
		};

		CustomCell cell = new CustomCell();
		Column<DocumentVersionDTO, String> titleColumn = new Column<DocumentVersionDTO, String>(cell) {

			@Override
			public String getValue(DocumentVersionDTO object) {
				return object.getName();
			}
		};
		titleColumn.setSortable(true);

		Column<DocumentVersionDTO, String> summaryColumn = new Column<DocumentVersionDTO, String>(cell) {

			@Override
			public String getValue(DocumentVersionDTO object) {
				return object.getSummary();
			}
		};
		summaryColumn.setSortable(true);

		ImageResourceCell imgCell = new ImageResourceCell();
		Column<DocumentVersionDTO, ImageResource> creationDateColumn = new Column<DocumentVersionDTO, ImageResource>(imgCell) {

			@Override
			public ImageResource getValue(DocumentVersionDTO object) {
				float percent = (object.getDocumentParent().getScore() - min) / (max - min);

				if (percent > 0.8f) {
					return PortalImage.INSTANCE.star5();
				}
				else if (percent > 0.6f) {
					return PortalImage.INSTANCE.star4();
				}
				else if (percent > 0.4f) {
					return PortalImage.INSTANCE.star3();
				}
				else if (percent > 0.2f) {
					return PortalImage.INSTANCE.star2();
				}
				else if (percent > 0.0f) {
					return PortalImage.INSTANCE.star1();
				}
				else {
					return PortalImage.INSTANCE.star0();
				}
			}

		};

		Column<DocumentVersionDTO, String> versionColumn = new Column<DocumentVersionDTO, String>(cell) {

			@Override
			public String getValue(DocumentVersionDTO object) {
				return object.getVersion();
			}
		};
		versionColumn.setSortable(true);

		Column<DocumentVersionDTO, String> grantedColumn = new Column<DocumentVersionDTO, String>(cell) {

			@Override
			public String getValue(DocumentVersionDTO object) {
				if (object.getDocumentParent().isGranted()) {
					return ToolsGWT.lblCnst.AccessAuthorized();
				}
				else {
					return ToolsGWT.lblCnst.AccessUnauthorized();
				}
			}
		};
		grantedColumn.setSortable(true);

		final DataGrid.Resources resources = new CustomResources();
		DataGrid<DocumentVersionDTO> dataGrid = new DataGrid<DocumentVersionDTO>(12, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(iconColumn);
		dataGrid.setColumnWidth(iconColumn, "50px");
		dataGrid.addColumn(titleColumn, ToolsGWT.lblCnst.Title());
		dataGrid.addColumn(summaryColumn, ToolsGWT.lblCnst.Summary());
		dataGrid.addColumn(creationDateColumn);
		dataGrid.setColumnWidth(creationDateColumn, "110px");
		dataGrid.addColumn(versionColumn, ToolsGWT.lblCnst.Version());
		dataGrid.setColumnWidth(versionColumn, "80px");
		dataGrid.addColumn(grantedColumn, ToolsGWT.lblCnst.Access());
		dataGrid.setColumnWidth(grantedColumn, "120px");
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoResult()));

		dataProvider = new ListDataProvider<DocumentVersionDTO>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<DocumentVersionDTO>(new ArrayList<DocumentVersionDTO>());
		sortHandler.setComparator(titleColumn, new Comparator<DocumentVersionDTO>() {

			@Override
			public int compare(DocumentVersionDTO o1, DocumentVersionDTO o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		sortHandler.setComparator(summaryColumn, new Comparator<DocumentVersionDTO>() {

			@Override
			public int compare(DocumentVersionDTO o1, DocumentVersionDTO o2) {
				return o1.getSummary().compareTo(o2.getSummary());
			}
		});
		sortHandler.setComparator(versionColumn, new Comparator<DocumentVersionDTO>() {

			@Override
			public int compare(DocumentVersionDTO o1, DocumentVersionDTO o2) {
				return o1.getVersion().compareTo(o2.getVersion());
			}
		});
		sortHandler.setComparator(grantedColumn, new Comparator<DocumentVersionDTO>() {

			@Override
			public int compare(DocumentVersionDTO o1, DocumentVersionDTO o2) {
				String access1 = "";
				String access2 = "";
				if (o1.getDocumentParent().isGranted()) {
					access1 = ToolsGWT.lblCnst.AccessAuthorized();
				}
				else {
					access1 = ToolsGWT.lblCnst.AccessUnauthorized();
				}

				if (o2.getDocumentParent().isGranted()) {
					access2 = ToolsGWT.lblCnst.AccessAuthorized();
				}
				else {
					access2 = ToolsGWT.lblCnst.AccessUnauthorized();
				}

				return access1.compareTo(access2);
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		SelectionModel<DocumentVersionDTO> selectionModel = new SingleSelectionModel<DocumentVersionDTO>();
		dataGrid.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		panelPager.setWidget(pager);

		return dataGrid;
	}

	private void askForRequest(DocumentVersionDTO item) {
		RequestAccessDialog dialog = new RequestAccessDialog(item);
		dialog.center();
	}

	private void showGedContextMenu(DocumentVersionDTO item, int x, int y) {
		if (item.getDocumentParent().isGranted()) {
			GedMenu contextuel = new GedMenu(this, item);
			contextuel.setPopupPosition(x, y);
			contextuel.setAutoHideEnabled(true);
			contextuel.show();
		}
	}

	@Override
	public void checkin(final DocumentVersionDTO item) {
		parentPanel.showWaitPart(true);

		GedService.Connect.getInstance().checkIfItemCanBeCheckin(item.getDocumentParent().getId(), biPortal.get().getInfoUser().getUser().getId(), new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				parentPanel.showWaitPart(false);

				if (result != null && result) {
					CheckinDialog dial = new CheckinDialog(null, item);
					dial.center();
				}
				else {
					MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.CheckinImpossible());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				parentPanel.showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}
		});
	}

	@Override
	public void checkout(final DocumentVersionDTO item) {
		parentPanel.showWaitPart(true);

		GedService.Connect.getInstance().checkout(item.getDocumentParent().getId(), biPortal.get().getInfoUser().getUser().getId(), item.getKey(), item.getFormat(), new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				parentPanel.showWaitPart(false);

				String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + result + "&" + CommonConstants.REPORT_OUTPUT + "=" + item.getFormat() + "&" + CommonConstants.CHECKOUT + "=true";
				ToolsGWT.doRedirect(fullUrl);
			}

			@Override
			public void onFailure(Throwable caught) {
				parentPanel.showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.AlreadyLocked());
			}
		});
	}

	@Override
	public void comeBackToVersion(DocumentVersionDTO item) {
		parentPanel.showWaitPart(true);

		GedService.Connect.getInstance().comeBackToVersion(item, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				parentPanel.showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				parentPanel.showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}
		});
	}

	@Override
	public void share(DocumentVersionDTO item) {
		this.selectedVersion = item;
		boolean isConnectedToAklabox = infoUser != null ? infoUser.isConnected(IRepositoryApi.AKLABOX) : false;

		SharePopup sharePopup = null;
		if (isConnectedToAklabox) {
			sharePopup = new SharePopup(this, TypeShare.EMAIL, TypeShare.EXPORT, TypeShare.CMIS, TypeShare.AKLABOX);
		}
		else {
			sharePopup = new SharePopup(this, TypeShare.EMAIL, TypeShare.EXPORT, TypeShare.CMIS);
		}
		sharePopup.center();
	}

	@Override
	public void openShare(final TypeShare typeShare) {
		if (selectedVersion != null) {

			if (selectedVersion.getDocumentParent().isGranted()) {
				GedService.Connect.getInstance().loadDocument(selectedVersion.getKey(), selectedVersion.getFormat(), new GwtCallbackWrapper<String>(parentPanel, true, true) {

					@Override
					public void onSuccess(String result) {
						List<String> formats = new ArrayList<String>();
						formats.add(selectedVersion.getFormat());

						RepositoryItem repItem = new RepositoryItem();
						repItem.setItemName(selectedVersion.getName());
						PortailRepositoryItem dummyItem = new PortailRepositoryItem(repItem, null);

						LaunchReportInformations itemInfo = new LaunchReportInformations();
						itemInfo.setOutputs(formats);
						itemInfo.setItem(dummyItem);
						itemInfo.setReportKey(result);

						switch (typeShare) {
						case AKLABOX:
							AklaboxShareDialog dial = new AklaboxShareDialog(itemInfo.getItem(), itemInfo.getReportKey(), itemInfo.getOutputs());
							dial.center();
							break;
						case EMAIL:
							MailShareDialog mailDialog = new MailShareDialog(ResultPanel.this, result, itemInfo, infoUser.getAvailableGroups());
							mailDialog.center();
							break;
						case CMIS:
							CmisShareWizard cmisDial = new CmisShareWizard(ResultPanel.this, itemInfo.getReportKey(), itemInfo);
							cmisDial.center();
							break;

						default:
							break;
						}
					}
				}.getAsyncCallback());
			}
			else {
				final InformationsDialog dial = new InformationsDialog(ToolsGWT.lblCnst.AskForRequestTitle(), ToolsGWT.lblCnst.Ok(), ToolsGWT.lblCnst.Cancel(), ToolsGWT.lblCnst.AskForRequest(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							askForRequest(selectedVersion);
						}
					}
				});
				dial.center();
			}
		}
	}

	@Override
	public void share(InfoShare infoShare) {
		if (infoShare instanceof InfoShareMail) {
			InfoShareMail infoShareReport = (InfoShareMail) infoShare;

			ReportingService.Connect.getInstance().sendEmail(infoShareReport, new GwtCallbackWrapper<ExportResult>(parentPanel, true, true) {

				@Override
				public void onSuccess(ExportResult result) {
					MessageHelper.openMessageMailResult(LabelsConstants.lblCnst.Information(), result);
				}
				
			}.getAsyncCallback());
		}
		else if (infoShare instanceof InfoShareCmis) {
			ReportingService.Connect.getInstance().shareCmis((InfoShareCmis) infoShare, new GwtCallbackWrapper<ExportResult>(parentPanel, true, true) {

				@Override
				public void onSuccess(ExportResult result) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.SuccessDocumentCreation());
				}
				
			}.getAsyncCallback());
		}
	}

	@Override
	public void displayPublicUrls(DocumentVersionDTO item) {
		PublicUrlDialog dial = new PublicUrlDialog(infoUser, item.getId(), null, TypeURL.DOCUMENT_VERSION);
		dial.center();
	}

	private class CustomImageCell extends ImageResourceCell {

		public CustomImageCell() {
			super();
		}

		@Override
		public Set<String> getConsumedEvents() {
			Set<String> consumedEvents = new HashSet<String>();
			consumedEvents.add("dblclick");
			consumedEvents.add("contextmenu");
			return consumedEvents;
		}

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, ImageResource value, NativeEvent event, ValueUpdater<ImageResource> valueUpdater) {

			final DocumentVersionDTO item = (DocumentVersionDTO) context.getKey();
			if (event.getButton() == NativeEvent.BUTTON_RIGHT) {
				showGedContextMenu(item, event.getClientX(), event.getClientY());
			}
			else if (event.getType().equals("dblclick")) {
				loadDocument(item);
			}
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

	private class CustomCell extends TextCell {

		public CustomCell() {
			super();
		}

		@Override
		public Set<String> getConsumedEvents() {
			Set<String> consumedEvents = new HashSet<String>();
			consumedEvents.add("dblclick");
			consumedEvents.add("contextmenu");
			return consumedEvents;
		}

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
			final DocumentVersionDTO item = (DocumentVersionDTO) context.getKey();
			if (event.getButton() == NativeEvent.BUTTON_RIGHT) {
				showGedContextMenu(item, event.getClientX(), event.getClientY());
			}
			else if (event.getType().equals("dblclick")) {
				loadDocument(item);
			}
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}
}
