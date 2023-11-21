package bpm.architect.web.client.panels;

import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.dialogs.ContractDialog;
import bpm.architect.web.client.dialogs.ContractHistoricDialog;
import bpm.architect.web.client.dialogs.DatabaseLinkedDialog;
import bpm.architect.web.client.dialogs.DatabaseSelectionDialog;
import bpm.architect.web.client.dialogs.DatabaseSelectionDialog.IRefreshProvider;
import bpm.architect.web.client.dialogs.DocumentVersionsDialog;
import bpm.architect.web.client.dialogs.IntegrationManagerDialog;
import bpm.architect.web.client.dialogs.UploadDialog;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.services.ArchitectService;
import bpm.architect.web.client.utils.DocumentHelper;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.custom.v2.ColumnTooltip;
import bpm.gwt.commons.client.custom.v2.ColumnTooltip.ITooltipManager;
import bpm.gwt.commons.client.custom.v2.CommonAsyncProvider;
import bpm.gwt.commons.client.custom.v2.CommonAsyncProvider.ILoadDataHandler;
import bpm.gwt.commons.client.custom.v2.SimplePagerFR;
import bpm.gwt.commons.client.dialog.DataPreparationDialog;
import bpm.gwt.commons.client.dialog.DataPreparationDialog.IDataPreparationManager;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.meta.MetaDialog;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.tree.MdmTree;
import bpm.gwt.commons.client.tree.MdmTree.IMdmManager;
import bpm.gwt.commons.client.tree.MdmTreeItem;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.InfoUser;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.MdmDirectory;
import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;

public class ConsultPanel extends CompositeWaitPanel implements IRefreshProvider, IDataPreparationManager, IMdmManager, ILoadDataHandler {

	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
	// private static final Datefo
	
	private static final int PAGE_SIZE = 15;

	private static ConsultPanelUiBinder uiBinder = GWT.create(ConsultPanelUiBinder.class);

	interface ConsultPanelUiBinder extends UiBinder<Widget, ConsultPanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();

		String imgGrid();
		String imgFormat();

		String imgPlanned();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelTree, gridPanel;

	@UiField
	Image btnAddVersion, btnEdit, btnClear, btnShare;
	
	@UiField
	SimplePanel panelPager;

	@UiField
	TextHolderBox txtSearch;

	private InfoUser infoUser;

	private MdmTree mdmTree;

	private DataGrid<Contract> dataGrid;
	private CommonAsyncProvider<Contract> dataProvider;
	private SingleSelectionModel<Contract> selectionModel;
	private SimplePagerFR pager;

	public ConsultPanel(InfoUser infoUser) {
		initWidget(uiBinder.createAndBindUi(this));
		this.infoUser = infoUser;

		mdmTree = new MdmTree(this);
		panelTree.setWidget(mdmTree);

		DataGrid<Contract> datagrid = createGridContracts();
		gridPanel.setWidget(datagrid);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		this.pager = new SimplePagerFR(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.setDisplay(datagrid);
		
		panelPager.setWidget(pager);

		refresh(null);
		updateToolbar(false);
	}

	private void updateToolbar(boolean visible) {
		btnAddVersion.setVisible(visible);
		btnEdit.setVisible(visible);
		btnShare.setVisible(visible);
	}

	private DataGrid<Contract> createGridContracts() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		TextCell cell = new TextCell();
		Column<Contract, String> colName = new ColumnTooltip<Contract, String>(cell, new ITooltipManager<Contract>() {
			
			@Override
			public String buildTooltip(Contract object) {
				if (object.getFileVersions() != null) {
					return object.getFileVersions().getName();
				}
				return Labels.lblCnst.NoDocument();
			}
		}) {

			@Override
			public String getValue(Contract object) {
				if (object.getFileVersions() != null) {
					return object.getFileVersions().getName();
				}
				return Labels.lblCnst.NoDocument();
			}
		};
//		colName.setSortable(true);

		Column<Contract, String> colContractName = new ColumnTooltip<Contract, String>(cell, new ITooltipManager<Contract>() {
			
			@Override
			public String buildTooltip(Contract object) {
				return object.getName();
			}
		}) {

			@Override
			public String getValue(Contract object) {
				return object.getName();
			}
		};
		colContractName.setSortable(true);
		colContractName.setDataStoreName("name");

		Column<Contract, String> colSupplierName = new ColumnTooltip<Contract, String>(cell, new ITooltipManager<Contract>() {
			
			@Override
			public String buildTooltip(Contract object) {
				return object.getParent().getName();
			}
		}) {

			@Override
			public String getValue(Contract object) {
				return object.getParent().getName();
			}
		};
		colSupplierName.setSortable(true);
		colSupplierName.setDataStoreName("parent.name");

		Column<Contract, String> colDirectoryName = new ColumnTooltip<Contract, String>(cell, new ITooltipManager<Contract>() {
			
			@Override
			public String buildTooltip(Contract object) {
				return object.getDirectory() != null ? object.getDirectory().getName() : "";
			}
		}) {

			@Override
			public String getValue(Contract object) {
				return object.getDirectory() != null ? object.getDirectory().getName() : "";
			}
		};
//		colDirectoryName.setSortable(true);

		Column<Contract, String> colModificationDate = new ColumnTooltip<Contract, String>(cell) {

			@Override
			public String getValue(Contract object) {
				if (object.getFileVersions() != null) {
					DocumentVersion version = object.getFileVersions().getCurrentVersion(object.getVersionId());
					if (version != null && version.getModificationDate() != null) {
						return dateFormatter.format(version.getModificationDate());
					}
					else {
						return Labels.lblCnst.Unknown();
					}
				}
				return Labels.lblCnst.NoDocument();
			}
		};
//		colModificationDate.setSortable(true);

		Column<Contract, String> colModifiedBy = new ColumnTooltip<Contract, String>(cell) {

			@Override
			public String getValue(Contract object) {
				if (object.getFileVersions() != null) {
					return object.getFileVersions().getCurrentVersion(object.getVersionId()).getModificator().getLogin();
				}
				return Labels.lblCnst.NoDocument();
			}
		};
//		colModifiedBy.setSortable(true);

		Column<Contract, String> colVersion = new ColumnTooltip<Contract, String>(cell) {

			@Override
			public String getValue(Contract object) {
				if (object.getFileVersions() != null) {
					return "V" + object.getFileVersions().getCurrentVersion(object.getVersionId()).getVersion();
				}
				return Labels.lblCnst.NoDocument();
			}
		};
//		colVersion.setSortable(true);

		FormatImageCell formatCell = new FormatImageCell(style.imgFormat());
		Column<Contract, Contract> colFormat = new Column<Contract, Contract>(formatCell) {

			@Override
			public Contract getValue(Contract object) {
				return object;
			}
		};
//		colFormat.setSortable(true);

		Column<Contract, String> colHasInput = new Column<Contract, String>(cell) {

			@Override
			public String getValue(Contract object) {
				return object.hasInput() ? LabelsConstants.lblCnst.Yes() : LabelsConstants.lblCnst.No();
			}
		};

		ButtonImageCell viewCell = new ButtonImageCell(Images.INSTANCE.ic_visibility_black_18dp(), Labels.lblCnst.ViewCurrentDocumentVersion(), style.imgGrid());
		Column<Contract, String> colView = new Column<Contract, String>(viewCell) {

			@Override
			public String getValue(Contract object) {
				return "";
			}
		};
		colView.setFieldUpdater(new FieldUpdater<Contract, String>() {

			@Override
			public void update(int index, final Contract object, String value) {
				if (object.getFileVersions() != null) {
					DocumentHelper.viewCurrentDocument(ConsultPanel.this, object, object.getVersionId());
				}
				else {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.ThereIsNoDocument());
				}
			}
		});

		ButtonImageCell versionCell = new ButtonImageCell(Images.INSTANCE.ic_my_library_books_black_18dp(), Labels.lblCnst.ViewDocumentVersions(), style.imgGrid());
		Column<Contract, String> colVersions = new Column<Contract, String>(versionCell) {

			@Override
			public String getValue(Contract object) {
				return "";
			}
		};
		colVersions.setFieldUpdater(new FieldUpdater<Contract, String>() {

			@Override
			public void update(int index, final Contract object, String value) {
				if (object.getFileVersions() != null) {
					DocumentVersionsDialog dial = new DocumentVersionsDialog(ConsultPanel.this, object);
					dial.center();
				}
				else {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.ThereIsNoDocument());
				}
			}
		});

		ButtonImageCell metaCell = new ButtonImageCell(Images.INSTANCE.ic_assignment_black_18dp(), LabelsConstants.lblCnst.Meta(), style.imgGrid());
		Column<Contract, String> colMeta = new Column<Contract, String>(metaCell) {

			@Override
			public String getValue(Contract object) {
				return "";
			}
		};
		colMeta.setFieldUpdater(new FieldUpdater<Contract, String>() {

			@Override
			public void update(int index, final Contract object, String value) {
				MetaDialog dial = new MetaDialog(null, object.getId(), TypeMetaLink.ARCHITECT, 1, true, true);
				dial.center();
			}
		});

		ButtonImageCell linkedCell = new ButtonImageCell(Images.INSTANCE.ic_link_black_18dp(), Labels.lblCnst.IntegrationManager(), style.imgGrid());
		Column<Contract, String> colLinked = new Column<Contract, String>(linkedCell) {

			@Override
			public String getValue(Contract object) {
				return "";
			}
		};
		colLinked.setFieldUpdater(new FieldUpdater<Contract, String>() {

			@Override
			public void update(int index, final Contract object, String value) {
				final IntegrationManagerDialog dial = new IntegrationManagerDialog(ConsultPanel.this, infoUser, object);
				dial.center();
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.needRefreshContracts()) {
							refresh(object);
						}
					}
				});
			}
		});

		ButtonImageCell dbCell = new ButtonImageCell(Images.INSTANCE.ic_database_view_black_36px(), Labels.lblCnst.DatabaseLinked(), style.imgGrid());
		Column<Contract, String> colDb = new Column<Contract, String>(dbCell) {

			@Override
			public String getValue(Contract object) {
				return "";
			}
		};
		colDb.setFieldUpdater(new FieldUpdater<Contract, String>() {

			@Override
			public void update(int index, final Contract object, String value) {
				if (object.getDatasourceId() != null && object.getDatasetId() != null) {
					DatabaseLinkedDialog dial = new DatabaseLinkedDialog(ConsultPanel.this, infoUser.getUser(), object);
					dial.center();
				}
				else {
					DatabaseSelectionDialog dial = new DatabaseSelectionDialog(ConsultPanel.this, infoUser.getUser(), object);
					dial.center();
				}
			}
		});
		
		ButtonImageCell historicCell = new ButtonImageCell(Images.INSTANCE.ic_view_list_black_18dp(), Labels.lblCnst.HistoryActionsOnDocument(), style.imgGrid());
		Column<Contract, String> colHistoric = new Column<Contract, String>(historicCell) {

			@Override
			public String getValue(Contract object) {
				return "";
			}
		};
		colHistoric.setFieldUpdater(new FieldUpdater<Contract, String>() {

			@Override
			public void update(int index, final Contract object, String value) {
				ContractHistoricDialog dial = new ContractHistoricDialog(object);
				dial.center();
			}
		});

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), Labels.lblCnst.DeleteDocument(), style.imgGrid());
		Column<Contract, String> colDelete = new Column<Contract, String>(deleteCell) {

			@Override
			public String getValue(Contract object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<Contract, String>() {

			@Override
			public void update(int index, final Contract object, String value) {
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), Labels.lblCnst.DeleteContractConfirm(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							deleteContract(object);
						}
					}
				});
				dial.center();
			}
		});

//		sortHandler = new ListHandler<Contract>(new ArrayList<Contract>());
//		sortHandler.setComparator(colName, new Comparator<Contract>() {
//
//			@Override
//			public int compare(Contract o1, Contract o2) {
//				if (o1.getFileVersions() == null) {
//					return -1;
//				}
//				else if (o2.getFileVersions() == null) {
//					return 1;
//				}
//				return o1.getFileVersions().getName().compareTo(o2.getFileVersions().getName());
//			}
//		});
//		sortHandler.setComparator(colContractName, new Comparator<Contract>() {
//
//			@Override
//			public int compare(Contract o1, Contract o2) {
//				return o1.getName().compareTo(o2.getName());
//			}
//		});
//		sortHandler.setComparator(colSupplierName, new Comparator<Contract>() {
//
//			@Override
//			public int compare(Contract o1, Contract o2) {
//				return o1.getParent().getName().compareTo(o2.getParent().getName());
//			}
//		});
//		sortHandler.setComparator(colDirectoryName, new Comparator<Contract>() {
//
//			@Override
//			public int compare(Contract o1, Contract o2) {
//				if (o1.getDirectory() == null) {
//					return -1;
//				}
//				else if (o2.getDirectory() == null) {
//					return 1;
//				}
//				else {
//					return o1.getDirectory().getName().compareTo(o2.getDirectory().getName());
//				}
//			}
//		});
//		sortHandler.setComparator(colModificationDate, new Comparator<Contract>() {
//
//			@Override
//			public int compare(Contract o1, Contract o2) {
//				if (o1.getFileVersions() == null) {
//					return -1;
//				}
//				else if (o2.getFileVersions() == null) {
//					return 1;
//				}
//				
//				DocumentVersion lv1 = o1.getFileVersions().getCurrentVersion(o1.getVersionId());
//				DocumentVersion lv2 = o2.getFileVersions().getCurrentVersion(o2.getVersionId());
//				if (lv1.getModificationDate() == null) {
//					return -1;
//				}
//				else if (lv2.getModificationDate() == null) {
//					return 1;
//				}
//
//				return lv2.getModificationDate().before(lv1.getModificationDate()) ? -1 : lv2.getModificationDate().after(lv1.getModificationDate()) ? 1 : 0;
//			}
//		});
//		sortHandler.setComparator(colModifiedBy, new Comparator<Contract>() {
//
//			@Override
//			public int compare(Contract o1, Contract o2) {
//				if (o1.getFileVersions() == null) {
//					return -1;
//				}
//				else if (o2.getFileVersions() == null) {
//					return 1;
//				}
//				
//				DocumentVersion lv1 = o1.getFileVersions().getCurrentVersion(o1.getVersionId());
//				DocumentVersion lv2 = o2.getFileVersions().getCurrentVersion(o2.getVersionId());
//
//				return lv1.getModifiedBy() > lv2.getModifiedBy() ? +1 : lv1.getModifiedBy() < lv2.getModifiedBy() ? -1 : 0;
//			}
//		});
//		sortHandler.setComparator(colVersion, new Comparator<Contract>() {
//
//			@Override
//			public int compare(Contract o1, Contract o2) {
//				if (o1.getFileVersions() == null) {
//					return -1;
//				}
//				else if (o2.getFileVersions() == null) {
//					return 1;
//				}
//				
//				DocumentVersion lv1 = o1.getFileVersions().getCurrentVersion(o1.getVersionId());
//				DocumentVersion lv2 = o2.getFileVersions().getCurrentVersion(o2.getVersionId());
//
//				return lv1.getVersion() > lv2.getVersion() ? +1 : lv1.getVersion() < lv2.getVersion() ? -1 : 0;
//			}
//		});
//		sortHandler.setComparator(colFormat, new Comparator<Contract>() {
//
//			@Override
//			public int compare(Contract o1, Contract o2) {
//				if (o1.getFileVersions() == null) {
//					return -1;
//				}
//				else if (o2.getFileVersions() == null) {
//					return 1;
//				}
//				
//				DocumentVersion lv1 = o1.getFileVersions().getCurrentVersion(o1.getVersionId());
//				DocumentVersion lv2 = o2.getFileVersions().getCurrentVersion(o2.getVersionId());
//
//				return lv1.getFormat().compareTo(lv2.getFormat());
//			}
//		});

		DataGrid.Resources resources = new CustomResources();
		dataGrid = new DataGrid<Contract>(PAGE_SIZE, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(colName, Labels.lblCnst.Name());
		dataGrid.setColumnWidth(colName, "250px");
		dataGrid.addColumn(colContractName, Labels.lblCnst.Contract());
		dataGrid.setColumnWidth(colContractName, "230px");
		dataGrid.addColumn(colSupplierName, Labels.lblCnst.Supplier());
		dataGrid.setColumnWidth(colSupplierName, "150px");
		dataGrid.addColumn(colDirectoryName, LabelsConstants.lblCnst.Folder());
		dataGrid.setColumnWidth(colDirectoryName, "150px");
		dataGrid.addColumn(colModificationDate, Labels.lblCnst.ModificationDate());
		dataGrid.setColumnWidth(colModificationDate, "150px");
		dataGrid.addColumn(colModifiedBy, Labels.lblCnst.ModifiedBy());
		dataGrid.setColumnWidth(colModifiedBy, "150px");
		dataGrid.addColumn(colVersion, Labels.lblCnst.Version());
		dataGrid.setColumnWidth(colVersion, "70px");
		dataGrid.addColumn(colHasInput, Labels.lblCnst.HasInput());
		dataGrid.setColumnWidth(colHasInput, "80px");
		dataGrid.addColumn(colFormat, Labels.lblCnst.Format());
		dataGrid.setColumnWidth(colFormat, "80px");
		dataGrid.addColumn(colView);
		dataGrid.setColumnWidth(colView, "70px");
		dataGrid.addColumn(colVersions);
		dataGrid.setColumnWidth(colVersions, "70px");
		dataGrid.addColumn(colMeta);
		dataGrid.setColumnWidth(colMeta, "70px");
		dataGrid.addColumn(colLinked);
		dataGrid.setColumnWidth(colLinked, "70px");
		dataGrid.addColumn(colDb);
		dataGrid.setColumnWidth(colDb, "70px");
		dataGrid.addColumn(colHistoric);
		dataGrid.setColumnWidth(colHistoric, "70px");
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		
	    AsyncHandler sortHandler = new AsyncHandler(dataGrid);
		dataGrid.addColumnSortHandler(sortHandler);

		dataProvider = new CommonAsyncProvider<Contract>(this);
		dataProvider.addDataDisplay(dataGrid);

		this.selectionModel = new SingleSelectionModel<Contract>();
		selectionModel.addSelectionChangeHandler(selectionChange);
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}
	
	public void loadContracts() {
		if (pager != null) {
			pager.firstPage();
		}
		
		loadData(0, PAGE_SIZE);

		updateToolbar(false);
	}

	@Override
	public void refresh(Contract contract) {
		mdmTree.clearSelection();
		loadContracts();
	}

	@Override
	public void loadItems(MdmTreeItem<?> treeItem) {
		loadContracts();
	}

	@Override
	public void loadData(int start, int length) {
		updateToolbar(false);
		
		MdmDirectory directory = mdmTree.getSelectedItem() != null ? (MdmDirectory) mdmTree.getSelectedItem() : null;
		
		String query = txtSearch.getText();
        ColumnSortList sortList = dataGrid.getColumnSortList();
        
        DataSort dataSort = null;
        if (sortList.size() > 0) {
            String columnName = sortList.get(0).getColumn().getDataStoreName();
            boolean ascending = sortList.get(0).isAscending();
            dataSort = new DataSort(columnName, ascending);
        }
        else {
        	dataSort = new DataSort("name", true);
        }

		ArchitectService.Connect.getInstance().getContracts(directory != null ? directory.getId() : null, query, start, length, dataSort, new GwtCallbackWrapper<DataWithCount<Contract>>(this, true, true) {

			@Override
			public void onSuccess(DataWithCount<Contract> result) {
				if (result != null) {
					dataProvider.updateRowCount(((Long) result.getItemsCount()).intValue(), true);
					dataProvider.updateRowData(start, result.getItems());
				}
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		refresh(null);
	}

	@UiHandler("btnAddContract")
	public void onAddContract(ClickEvent event) {
		List<Group> availableGroups = infoUser.getAvailableGroups();

		ContractDialog dial = new ContractDialog(this, null, availableGroups);
		dial.center();
	}

	@UiHandler("btnAddVersion")
	public void onAddVersion(ClickEvent event) {
		Contract selectedContract = selectionModel.getSelectedObject();

		if (selectedContract != null) {
			UploadDialog dial = new UploadDialog(this, selectedContract);
			dial.center();
		}
		else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.NewVersionNotAllowedContactAdmin());
		}
	}

	@UiHandler("btnEdit")
	public void onEditContract(ClickEvent event) {
		Contract selectedContract = selectionModel.getSelectedObject();
		if (selectedContract != null) {
			List<Group> availableGroups = infoUser.getAvailableGroups();
			
			ContractDialog dial = new ContractDialog(this, selectedContract, availableGroups);
			dial.center();
		}
	}

	@UiHandler("btnShare")
	public void onShare(ClickEvent event) {
		Contract selectedContract = selectionModel.getSelectedObject();
		if (selectedContract != null) {
			boolean showSeparator = DocumentHelper.getFormat(selectedContract).equalsIgnoreCase("CSV");
			
			DataPreparationDialog dataPrepDial = new DataPreparationDialog(this, showSeparator);
			dataPrepDial.center();
		}
	}

	@Override
	public void createDataPreparation(String name, String separator) {
		Contract selectedContract = selectionModel.getSelectedObject();
		CommonService.Connect.getInstance().createDataPreparation(name, selectedContract, separator, new GwtCallbackWrapper<Void>(this, true, true) {

			@Override
			public void onSuccess(Void result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.ThePushToDataPreparationWasSuccessful());
			}
		}.getAsyncCallback());
	}

	private void deleteContract(Contract contract) {
		showWaitPart(true);

		ArchitectService.Connect.getInstance().removeContract(contract, new GwtCallbackWrapper<Void>(this, true) {

			@Override
			public void onSuccess(Void result) {
				refresh(null);
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnClear")
	public void onClearClick(ClickEvent event) {
		txtSearch.setText("");
		btnClear.setVisible(false);
		loadContracts();
	}

	@UiHandler("btnSearch")
	public void onSearchClick(ClickEvent event) {
		btnClear.setVisible(true);
		loadContracts();
	}

	private Handler selectionChange = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Contract contract = selectionModel.getSelectedObject();
			updateToolbar(contract != null);
		}
	};

	public InfoUser getInfoUser() {
		return infoUser;
	}
}
