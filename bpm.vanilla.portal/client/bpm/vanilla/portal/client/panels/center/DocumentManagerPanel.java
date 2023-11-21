package bpm.vanilla.portal.client.panels.center;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ImageButton;
import bpm.gwt.commons.client.dialog.PublicUrlDialog;
import bpm.gwt.commons.client.dialog.ged.IndexFormDialog;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.popup.IShare;
import bpm.gwt.commons.client.popup.SharePopup;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.viewer.aklabox.AklaboxShareDialog;
import bpm.gwt.commons.client.viewer.dialog.CmisShareWizard;
import bpm.gwt.commons.client.viewer.dialog.MailShareDialog;
import bpm.gwt.commons.shared.InfoShare;
import bpm.gwt.commons.shared.InfoShareCmis;
import bpm.gwt.commons.shared.InfoShareMail;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.utils.ExportResult;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.Listeners.GedMenu;
import bpm.vanilla.portal.client.dialog.RequestAccessDialog;
import bpm.vanilla.portal.client.dialog.ged.CheckinDialog;
import bpm.vanilla.portal.client.panels.IGedCheck;
import bpm.vanilla.portal.client.popup.TypeFilterPopup;
import bpm.vanilla.portal.client.services.GedService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class DocumentManagerPanel extends Tab implements IGedCheck, IShare {

	public enum TypeFilter {
		TYPE_ALL_DOC, 
		TYPE_GED_DOC, 
		TYPE_MDM_DOC
	}

	private static final String DATE_FORMAT = "HH:mm - dd/MM/yyyy";

	private static DocumentManagerPanelUiBinder uiBinder = GWT.create(DocumentManagerPanelUiBinder.class);

	interface DocumentManagerPanelUiBinder extends UiBinder<Widget, DocumentManagerPanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();
		String pager();
		String labelRowDocument();
		String imgGrantedDocument();
		String textDocument();
		String imgArrowDocument();
		String grid();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar, panelFormWopi;

	@UiField
	SimplePanel panelDocument, panelContent, panelPager;

	@UiField
	Image btnClear, btnIndexNewVersion;

	@UiField(provided = true)
	ImageButton btnFilters;

	@UiField
	TextBox txtSearch;
	
	private InfoUser infoUser;

	private ListDataProvider<DocumentDefinitionDTO> documentDataProvider;
	private ListHandler<DocumentDefinitionDTO> sortDocumentHandler;
	
	private ListDataProvider<DocumentVersionDTO> versionDataProvider;
	private ListHandler<DocumentVersionDTO> sortVersionHandler;

	private TypeFilter selectedFilter = TypeFilter.TYPE_ALL_DOC;
	private List<DocumentDefinitionDTO> documents;
	private DocumentDefinitionDTO selectedDocument;
	private DocumentVersionDTO selectedVersion;

	public DocumentManagerPanel(TabManager tabManager) {
		super(tabManager, ToolsGWT.lblCnst.DocumentManagerTab(), true);
		btnFilters = new ImageButton(CommonImages.INSTANCE.arrow_down());
		this.add(uiBinder.createAndBindUi(this));
		this.infoUser = biPortal.get().getInfoUser();
		
		panelDocument.setWidget(createDocumentList());
		panelContent.setWidget(createGridData());
		
		btnClear.setVisible(false);
		btnIndexNewVersion.setVisible(false);
		btnFilters.setText(getTypeFilterLabel(selectedFilter));
		
		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		this.addStyleName(style.mainPanel());
	    
	    loadDocument();
	}
	
	public String getTypeFilterLabel(TypeFilter filter) {
		if(filter == TypeFilter.TYPE_ALL_DOC) {
			return ToolsGWT.lblCnst.AllDocuments();
		}
		else if(filter == TypeFilter.TYPE_GED_DOC) {
			return ToolsGWT.lblCnst.OtherDocuments();
		}
		else if(filter == TypeFilter.TYPE_MDM_DOC) {
			return ToolsGWT.lblCnst.MDMDocuments();
		}
		return ToolsGWT.lblCnst.Unknown();
	}

	private void fillDocuments(List<DocumentDefinitionDTO> documents, String search, TypeFilter typeFilter) {
		this.documents = documents != null ? documents : new ArrayList<DocumentDefinitionDTO>();

		List<DocumentDefinitionDTO> docs = new ArrayList<DocumentDefinitionDTO>();
		if (search != null && !search.isEmpty()) {
			if (documents != null) {
				for (DocumentDefinitionDTO doc : documents) {
					if (doc.getName().toLowerCase().contains(search.toLowerCase())) {
						if (typeFilter == TypeFilter.TYPE_ALL_DOC || (typeFilter == TypeFilter.TYPE_GED_DOC && !doc.isMdm()) || (typeFilter == TypeFilter.TYPE_MDM_DOC && doc.isMdm())) {
							docs.add(doc);
						}
					}
				}
			}
		}
		else {
			if (documents != null) {
				for (DocumentDefinitionDTO doc : documents) {
					if (typeFilter == TypeFilter.TYPE_ALL_DOC || (typeFilter == TypeFilter.TYPE_GED_DOC && !doc.isMdm()) || (typeFilter == TypeFilter.TYPE_MDM_DOC && doc.isMdm())) {
						docs.add(doc);
					}
				}
			}
		}

		documentDataProvider.setList(docs);
		sortDocumentHandler.setList(documentDataProvider.getList());
	}

	private void fillVersions(DocumentDefinitionDTO documentDef) {
		if(documentDef == null || documentDef.getVersions() == null) {
			versionDataProvider.setList(new ArrayList<DocumentVersionDTO>());
		}
		else {
			List<DocumentVersionDTO> directChilds = documentDef.getVersions();
			versionDataProvider.setList(directChilds);
		}
		sortVersionHandler.setList(versionDataProvider.getList());
	}

	private DataGrid<DocumentDefinitionDTO> createDocumentList() {
		// Create a CellList.
		DocumentDefinitionCell documentCell = new DocumentDefinitionCell();

		// Add a selection model so we can select cells.
		SingleSelectionModel<DocumentDefinitionDTO> selectionDocumentModel = new SingleSelectionModel<DocumentDefinitionDTO>();
		selectionDocumentModel.addSelectionChangeHandler(selectionChangeHandler);

		Column<DocumentDefinitionDTO, DocumentDefinitionDTO> columnName = new Column<DocumentDefinitionDTO, DocumentDefinitionDTO>(documentCell) {

			@Override
			public DocumentDefinitionDTO getValue(DocumentDefinitionDTO object) {
				return object;
			}
		};
		columnName.setSortable(true);

		DataGrid.Resources resources = new CustomResources();
		DataGrid<DocumentDefinitionDTO> dataGrid = new DataGrid<DocumentDefinitionDTO>(1000, resources);
//		CustomTableResources resources = new CustomTableResources();
//		CellTable<DocumentDefinitionDTO> listDocument = new CellTable<DocumentDefinitionDTO>(1000, resources);
		dataGrid.setSelectionModel(selectionDocumentModel);
		dataGrid.addColumn(columnName, ToolsGWT.lblCnst.ListDocuments());
		dataGrid.setSize("100%", "100%");
		dataGrid.addStyleName(style.grid());
		
		documentDataProvider = new ListDataProvider<DocumentDefinitionDTO>();
		documentDataProvider.addDataDisplay(dataGrid);
		
		sortDocumentHandler = new ListHandler<DocumentDefinitionDTO>(new ArrayList<DocumentDefinitionDTO>());
		sortDocumentHandler.setComparator(columnName, new Comparator<DocumentDefinitionDTO>() {
			
			@Override
			public int compare(DocumentDefinitionDTO o1, DocumentDefinitionDTO o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		dataGrid.addColumnSortHandler(sortDocumentHandler);
		
		return dataGrid;
	}

	private DataGrid<DocumentVersionDTO> createGridData() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		CustomCell cell = new CustomCell();
		Column<DocumentVersionDTO, String> nameColumn = new Column<DocumentVersionDTO, String>(cell) {

			@Override
			public String getValue(DocumentVersionDTO object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);

		Column<DocumentVersionDTO, String> creationDateColumn = new Column<DocumentVersionDTO, String>(cell) {

			@Override
			public String getValue(DocumentVersionDTO object) {
				Date date = object.getCreationDate();
				if (date == null || date.equals(new Date(0))) {
					return ToolsGWT.lblCnst.Never();
				}
				return dateFormatter.format(date);
			}
		};
		creationDateColumn.setSortable(true);

		Column<DocumentVersionDTO, String> formatColumn = new Column<DocumentVersionDTO, String>(cell) {

			@Override
			public String getValue(DocumentVersionDTO object) {
				return object.getFormat();
			}
		};
		formatColumn.setSortable(true);

		Column<DocumentVersionDTO, String> summaryColumn = new Column<DocumentVersionDTO, String>(cell) {

			@Override
			public String getValue(DocumentVersionDTO object) {
				return object.getSummary();
			}
		};
		summaryColumn.setSortable(true);

		DataGrid.Resources resources = new CustomResources();
		DataGrid<DocumentVersionDTO> dataGrid = new DataGrid<DocumentVersionDTO>(12, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(nameColumn, ToolsGWT.lblCnst.HistoricName());
		dataGrid.addColumn(creationDateColumn, ToolsGWT.lblCnst.HistoricDateCreation());
		dataGrid.addColumn(formatColumn, ToolsGWT.lblCnst.HistoricFormat());
		dataGrid.addColumn(summaryColumn, ToolsGWT.lblCnst.Summary());
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoResult()));

		versionDataProvider = new ListDataProvider<DocumentVersionDTO>();
		versionDataProvider.addDataDisplay(dataGrid);

		sortVersionHandler = new ListHandler<DocumentVersionDTO>(new ArrayList<DocumentVersionDTO>());
		sortVersionHandler.setComparator(nameColumn, new Comparator<DocumentVersionDTO>() {
			
			@Override
			public int compare(DocumentVersionDTO o1, DocumentVersionDTO o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		sortVersionHandler.setComparator(creationDateColumn, new Comparator<DocumentVersionDTO>() {
			
			@Override
			public int compare(DocumentVersionDTO o1, DocumentVersionDTO o2) {
				if(o1.getCreationDate() == null) {
					return -1;
				}
				else if(o2.getCreationDate() == null) {
					return 1;
				}
				
				return o2.getCreationDate().before(o1.getCreationDate()) ? -1 : o2.getCreationDate().after(o1.getCreationDate()) ? 1 : 0;
			}
		});
		sortVersionHandler.setComparator(formatColumn, new Comparator<DocumentVersionDTO>() {
			
			@Override
			public int compare(DocumentVersionDTO o1, DocumentVersionDTO o2) {
				return o1.getFormat().compareTo(o2.getFormat());
			}
		});
		sortVersionHandler.setComparator(summaryColumn, new Comparator<DocumentVersionDTO>() {
			
			@Override
			public int compare(DocumentVersionDTO o1, DocumentVersionDTO o2) {
				return o1.getSummary().compareTo(o2.getSummary());
			}
		});
		dataGrid.addColumnSortHandler(sortVersionHandler);
		
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

	private void refreshBtnNewVersion(DocumentDefinitionDTO selectedDoc) {
		if(selectedDoc.isGranted()) {
			btnIndexNewVersion.setTitle(ToolsGWT.lblCnst.AddVersion() + " " + selectedDoc.getName());
			btnIndexNewVersion.setVisible(true);
		}
		else {
			btnIndexNewVersion.setVisible(false);
		}
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		loadDocument();
	}

	@UiHandler("btnIndex")
	public void onIndexClick(ClickEvent event) {
		IndexFormDialog dial = new IndexFormDialog(null, biPortal.get().getInfoUser().getAvailableGroups());
		dial.center();
		dial.addCloseHandler(closeHandler);
	}

	@UiHandler("btnIndexNewVersion")
	public void onIndexNewVersion(ClickEvent event) {
		if (selectedDocument != null && selectedDocument.isGranted()) {
			IndexFormDialog dial = new IndexFormDialog(selectedDocument, biPortal.get().getInfoUser().getAvailableGroups());
			dial.center();
			dial.addCloseHandler(closeHandler);
		}
		else {
			MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.ChooseDocToAddVersion());
		}
	}

	@UiHandler("btnSearch")
	public void onSearchClick(ClickEvent event) {
		if (!txtSearch.getText().isEmpty()) {
			btnClear.setVisible(true);

			fillDocuments(documents, txtSearch.getText(), selectedFilter);
		}
	}

	@UiHandler("btnClear")
	public void onClearClick(ClickEvent event) {
		clearSearch();

		fillDocuments(documents, txtSearch.getText(), selectedFilter);
	}
	
	@UiHandler("btnFilters")
	public void onFilterChange(ClickEvent event) {
		TypeFilterPopup displayPopup = new TypeFilterPopup(this);
		displayPopup.setPopupPosition(event.getClientX(), event.getClientY());
		displayPopup.show();
	}
	
	public void changeFilter(TypeFilter filter) {
		this.selectedFilter = filter;
		btnFilters.setText(getTypeFilterLabel(selectedFilter));
		fillDocuments(documents, txtSearch.getText(), selectedFilter);
	}

	private void clearSearch() {
		txtSearch.setText("");

		btnClear.setVisible(false);
	}

	public void loadDocument() {
		showWaitPart(true);

		clearSearch();
		
		selectedDocument = null;
		btnIndexNewVersion.setVisible(false);

		GedService.Connect.getInstance().getAllDocuments(new AsyncCallback<List<DocumentDefinitionDTO>>() {

			@Override
			public void onSuccess(List<DocumentDefinitionDTO> result) {
				showWaitPart(false);
				
				fillDocuments(result, "", selectedFilter);
				fillVersions(null);
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				fillVersions(null);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.FailedGetDocuments());
			}
		});
	}

	public void dispatchAction(final DocumentVersionDTO obj, NativeEvent event) {
		if (event.getType().equals("dblclick")) {
			if (obj.getDocumentParent().isGranted()) {
				boolean isOffice = obj.getFormat().equalsIgnoreCase(Formats.CSV.getExtension()) || 
						obj.getFormat().equalsIgnoreCase(Formats.EXCEL.getExtension()) ||
						obj.getFormat().equalsIgnoreCase(Formats.EXCELX.getExtension()) ||
						obj.getFormat().equalsIgnoreCase(Formats.WORD.getExtension()) ||
						obj.getFormat().equalsIgnoreCase(Formats.WORDX.getExtension()) ||
						obj.getFormat().equalsIgnoreCase(Formats.PPT.getExtension());
				if(biPortal.get().getInfoUser().getWopiServiceUrl() != null && !biPortal.get().getInfoUser().getWopiServiceUrl().isEmpty() && isOffice) {
					String wopiCallUrl = biPortal.get().getInfoUser().getWopiServiceUrl() + obj.getDocumentParent().getId();
					FormPanel form = new FormPanel("_blank");
					form.setAction(wopiCallUrl);
					form.setMethod(FormPanel.METHOD_POST);
					
					HTMLPanel pan = new HTMLPanel("");
					
					TextBox txtToken = new TextBox();
					txtToken.setName("access_token");
					txtToken.setText(obj.getId() + biPortal.get().getInfoUser().getUser().getLogin());
					
					TextBox txtTokenTtl = new TextBox();
					txtTokenTtl.setName("access_token_ttl");
					txtTokenTtl.setText("10000");
					
					pan.add(txtToken);
					pan.add(txtTokenTtl);
					form.add(pan);
					panelFormWopi.clear();
					panelFormWopi.add(form);
					form.submit();
				}
				else {
					GedService.Connect.getInstance().loadDocument(obj.getKey(), obj.getFormat(), new AsyncCallback<String>() {
	
						@Override
						public void onSuccess(String result) {
							if (result.indexOf("<error>") > -1) {
								String msg = result.replace("<error>", "").replace("</error>", "");
								if (msg.equalsIgnoreCase("")) {
									msg = ToolsGWT.lblCnst.NoHistoryFound() + " " + obj.getName();
								}
	
								MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Error(), ToolsGWT.lblCnst.DocumentNotFound());
							}
							else {
								String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" 
										+ CommonConstants.REPORT_HASHMAP_NAME + "=" + result + "&" + CommonConstants.REPORT_OUTPUT + "=" + obj.getFormat();
	
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
			}
			else {
				final InformationsDialog dial = new InformationsDialog(ToolsGWT.lblCnst.AskForRequestTitle(), ToolsGWT.lblCnst.Ok(), ToolsGWT.lblCnst.Cancel(), ToolsGWT.lblCnst.AskForRequest(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							askForRequest(obj);
						}
					}
				});
				dial.center();
			}
		}
		else if (event.getButton() == NativeEvent.BUTTON_RIGHT) {
			showGedContextMenu(obj, event.getClientX(), event.getClientY());
		}
	}

	private void showGedContextMenu(DocumentVersionDTO item, int x, int y) {
		if (item.getDocumentParent().isGranted()) {
			GedMenu contextuel = new GedMenu(this, item);
			contextuel.setPopupPosition(x, y);
			contextuel.setAutoHideEnabled(true);
			contextuel.show();
		}
	}

	private void askForRequest(DocumentVersionDTO item) {
		RequestAccessDialog dialog = new RequestAccessDialog(item);
		dialog.center();
	}

	@Override
	public void checkin(final DocumentVersionDTO item) {
		showWaitPart(true);

		GedService.Connect.getInstance().checkIfItemCanBeCheckin(item.getDocumentParent().getId(), biPortal.get().getInfoUser().getUser().getId(), new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				showWaitPart(false);

				if (result != null && result) {
					CheckinDialog dial = new CheckinDialog(DocumentManagerPanel.this, item);
					dial.center();
				}
				else {
					MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.CheckinImpossible());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}
		});
	}

	@Override
	public void checkout(final DocumentVersionDTO item) {
		showWaitPart(true);

		GedService.Connect.getInstance().checkout(item.getDocumentParent().getId(), biPortal.get().getInfoUser().getUser().getId(), item.getKey(), item.getFormat(), new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				showWaitPart(false);

				String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" 
						+ CommonConstants.REPORT_HASHMAP_NAME + "=" + result + "&" + CommonConstants.REPORT_OUTPUT + "=" + item.getFormat() + "&" + CommonConstants.CHECKOUT + "=true";
				ToolsGWT.doRedirect(fullUrl);
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.AlreadyLocked());
			}
		});
	}

	@Override
	public void comeBackToVersion(DocumentVersionDTO item) {
		showWaitPart(true);

		GedService.Connect.getInstance().comeBackToVersion(item, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				showWaitPart(false);

				loadDocument();
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

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
				GedService.Connect.getInstance().loadDocument(selectedVersion.getKey(), selectedVersion.getFormat(), new GwtCallbackWrapper<String>(this, true, true) {
		
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
							MailShareDialog mailDialog = new MailShareDialog(DocumentManagerPanel.this, result, itemInfo, infoUser.getAvailableGroups());
							mailDialog.center();
							break;
						case CMIS:
							CmisShareWizard cmisDial = new CmisShareWizard(DocumentManagerPanel.this, itemInfo.getReportKey(), itemInfo);
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
			showWaitPart(true);

			InfoShareMail infoShareReport = (InfoShareMail) infoShare;

			ReportingService.Connect.getInstance().sendEmail(infoShareReport, new GwtCallbackWrapper<ExportResult>(this, true) {

				@Override
				public void onSuccess(ExportResult result) {
					MessageHelper.openMessageMailResult(LabelsConstants.lblCnst.Information(), result);
				}
				
			}.getAsyncCallback());
		}
		else if (infoShare instanceof InfoShareCmis) {
			ReportingService.Connect.getInstance().shareCmis((InfoShareCmis) infoShare, new GwtCallbackWrapper<ExportResult>(this, true, true) {

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

	private CloseHandler<PopupPanel> closeHandler = new CloseHandler<PopupPanel>() {

		@Override
		public void onClose(CloseEvent<PopupPanel> event) {
			loadDocument();
			versionDataProvider.setList(new ArrayList<DocumentVersionDTO>());
			sortVersionHandler.setList(versionDataProvider.getList());
		}
	};

	private Handler selectionChangeHandler = new Handler() {

		@Override
		@SuppressWarnings("unchecked")
		public void onSelectionChange(SelectionChangeEvent event) {
			SingleSelectionModel<DocumentDefinitionDTO> selectionModel = (SingleSelectionModel<DocumentDefinitionDTO>) event.getSource();
			selectedDocument = selectionModel.getSelectedObject();

			refreshBtnNewVersion(selectedDocument);

			fillVersions(selectedDocument);
		}
	};

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
			DocumentVersionDTO obj = (DocumentVersionDTO) context.getKey();
			dispatchAction(obj, event);
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

	private class DocumentDefinitionCell extends AbstractCell<DocumentDefinitionDTO> {

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context, DocumentDefinitionDTO value, SafeHtmlBuilder sb) {
			String checkUrl = GWT.getHostPageBaseURL() + "images/documentManager/cancel.png";
			if (value.isGranted()) {
				checkUrl = GWT.getHostPageBaseURL() + "images/documentManager/apply.png";
			}
			String doubleArrowUrl = GWT.getHostPageBaseURL() + "images/documentManager/double_arrow.png";

			sb.appendHtmlConstant("<div class='" + style.labelRowDocument() + "'>");
			sb.appendHtmlConstant("<img src='" + checkUrl + "' class='" + style.imgGrantedDocument() + "' />");
			sb.appendHtmlConstant("<div class='" + style.textDocument() + "'>");
			sb.appendEscaped(value.getName());
			sb.appendHtmlConstant("</div>");
			sb.appendHtmlConstant("<img src='" + doubleArrowUrl + "' class='" + style.imgArrowDocument() + "' />");
			sb.appendHtmlConstant("</div>");
		}
	}

}
